package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lkd.common.VMSystem;
import com.lkd.conf.OrderConfig;
import com.lkd.config.TopicConfig;
import com.lkd.contract.VendoutReq;
import com.lkd.contract.VendoutReqData;
import com.lkd.contract.VendoutResp;
import com.lkd.contract.server.OrderCheck;
import com.lkd.dao.OrderDao;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.OrderEntity;
import com.lkd.feignService.UserService;
import com.lkd.feignService.VMService;
import com.lkd.http.viewModel.CreateOrderReq;
import com.lkd.http.viewModel.OrderResp;
import com.lkd.service.OrderCollectService;
import com.lkd.service.OrderService;
import com.lkd.utils.JsonUtil;
import com.lkd.viewmodel.*;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao,OrderEntity> implements OrderService{

    @Autowired
    private MqttProducer mqttProducer;


    @Autowired
    private OrderCollectService orderCollectService;
    @Autowired
    private VMService vmService;
    @Autowired
    private UserService userService;


    @Autowired
    private RestHighLevelClient esClient;

/*

    @Override
    public OrderResp createOrder(CreateOrderReq req) {
//        VendingMachineViewModel vendingMachineViewModel = vmService.getCompanyId(req.getInnerCode()).getData();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(req.getOrderNo());
        orderEntity.setAmount(req.getAmount());
        orderEntity.setInnerCode(req.getInnerCode());
//        orderEntity.setAreaId(vendingMachineViewModel.getAreaId());
        orderEntity.setSkuId(req.getSkuId());
        orderEntity.setSkuName(vmService.getSkuById(req.getSkuId()).getSkuName());
        orderEntity.setPrice(req.getPrice());
        orderEntity.setThirdNo(req.getThirdNO());
        orderEntity.setPayStatus(0);
        orderEntity.setStatus(0);
        orderEntity.setPayType(req.getPayType());

        this.save(orderEntity);

        OrderResp resp = new OrderResp();
        resp.setAmount(req.getAmount());
        resp.setInnerCode(req.getInnerCode());
        resp.setOrderNo(req.getOrderNo());
        resp.setPrice(req.getPrice());
        resp.setSkuId(req.getSkuId());
        resp.setThirdNO(req.getThirdNO());

        return resp;
    }
*/


    @Override
    public OrderEntity createOrder(CreateOrder createOrder) {
        VendingMachineViewModel vm = vmService.getVMInfo(createOrder.getInnerCode());
        SkuViewModel sku = vmService.getSku(createOrder.getInnerCode(),createOrder.getSkuId());
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setAddr(vm.getNodeAddr());
        orderEntity.setNodeId(vm.getNodeId());
        orderEntity.setNodeName(vm.getNodeName());
        orderEntity.setSkuId(sku.getSkuId());
        orderEntity.setSkuName(sku.getSkuName());
        orderEntity.setAmount(sku.getRealPrice());
        orderEntity.setClassId(sku.getClassId());
        orderEntity.setPrice(sku.getPrice());
        orderEntity.setBusinessId(vm.getBusinessId());
        orderEntity.setBusinessName(vm.getBusinessName());
        orderEntity.setInnerCode(createOrder.getInnerCode());
        orderEntity.setOpenId(createOrder.getOpenId());
        orderEntity.setPayStatus(VMSystem.PAY_STATUS_NOPAY);
        orderEntity.setRegionId(vm.getRegionId());
        orderEntity.setRegionName(vm.getRegionName());
        //orderEntity.setOrderNo(createOrder.getInnerCode()+createOrder.getSkuId()+System.nanoTime());
        orderEntity.setOrderNo(createOrder.getInnerCode()+System.nanoTime());
        //微信支付
        orderEntity.setPayType(createOrder.getPayType());

        orderEntity.setStatus(VMSystem.ORDER_STATUS_CREATE);
        orderEntity.setOwnerId(vm.getOwnerId());

        //合作商分成计算

        PartnerViewModel partner = userService.getPartner(vm.getOwnerId()); //获取合作商
        BigDecimal price = new BigDecimal(sku.getRealPrice());//价格
        BigDecimal bill = price.multiply(new BigDecimal(partner.getRatio())).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP);
        orderEntity.setBill(bill.intValue());//分成金额

        this.save(orderEntity);

        //将订单放到延迟队列中，10分钟后检查支付状态！！！！！！！！！！！！！！！！！！
        OrderCheck orderCheck = new OrderCheck();
        orderCheck.setOrderNo(orderEntity.getOrderNo());
        try {
            mqttProducer.send("$delayed/60/"+OrderConfig.ORDER_DELAY_CHECK_TOPIC,2,orderCheck);
        } catch (JsonProcessingException e) {
            log.error("send to emq error",e);
        }

        return orderEntity;
    }


    @Override
    public boolean vendoutResult(VendoutResp vendoutResp) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNo(vendoutResp.getVendoutResult().getOrderNo());
        UpdateWrapper<OrderEntity> uw = new UpdateWrapper<>();
        LambdaUpdateWrapper<OrderEntity> lambdaUpdateWrapper = uw.lambda();
        lambdaUpdateWrapper.set(OrderEntity::getPayStatus,1);
        if(vendoutResp.getVendoutResult().isSuccess()){
            lambdaUpdateWrapper.set(OrderEntity::getStatus,VMSystem.ORDER_STATUS_VENDOUT_SUCCESS);
        }else {
            lambdaUpdateWrapper.set(OrderEntity::getStatus,VMSystem.ORDER_STATUS_VENDOUT_FAIL);
        }
        lambdaUpdateWrapper.eq(OrderEntity::getOrderNo,vendoutResp.getVendoutResult().getOrderNo());

        return this.update(lambdaUpdateWrapper);
    }


    @Override
    public boolean payComplete(String orderNo) {
        sendVendout(orderNo);
        return true;
    }


    @Override
    public OrderEntity getByOrderNo(String orderNo) {
        QueryWrapper<OrderEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(OrderEntity::getOrderNo,orderNo);

        return this.getOne(qw);
    }

    @Override
    public Boolean cancel(String orderNo) {
        var order = this.getByOrderNo(orderNo);
        if(order.getStatus() > VMSystem.ORDER_STATUS_CREATE)
            return true;

        order.setStatus(VMSystem.ORDER_STATUS_INVALID);
        order.setCancelDesc("用户取消");

        return true;
    }

    @Override
    public Pager<OrderViewModel> search(Integer pageIndex, Integer pageSize, String orderNo, String openId, String startDate, String endDate) {

        //1.封装查询条件

        SearchRequest searchRequest=new SearchRequest("order");
        SearchSourceBuilder sourceBuilder =new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder= QueryBuilders.boolQuery();
        //订单号查询
        if(!Strings.isNullOrEmpty(orderNo)){
            boolQueryBuilder.must(  QueryBuilders.termQuery("order_no",orderNo)  );
        }
        //根据openId查询
        if(!Strings.isNullOrEmpty(openId)){
            boolQueryBuilder.must(  QueryBuilders.termQuery("open_id",openId)  );
        }
        //时间范围查询
        if(!Strings.isNullOrEmpty( startDate ) &&  !Strings.isNullOrEmpty(endDate) ){
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("update_time");
            rangeQueryBuilder.gte(startDate );
            rangeQueryBuilder.lte(endDate);
        }

        sourceBuilder.from((pageIndex-1)* pageSize);
        sourceBuilder.size(pageSize);

        sourceBuilder.trackTotalHits(true);
        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);

        //2.封装查询结果

        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();

            List<OrderViewModel> orderList=Lists.newArrayList();

            for(SearchHit hit:searchHits){

                String hitResult = hit.getSourceAsString();
                OrderViewModel order=new OrderViewModel();

                JsonNode jsonNode = JsonUtil.getTreeNode(hitResult);
                order.setId(jsonNode.findPath("id").asLong());
                order.setStatus(jsonNode.findPath("status").asInt());
                order.setBill(jsonNode.findPath("bill").asInt());
                order.setOwnerId(jsonNode.findPath("owner_id").asInt());
                order.setPayType(jsonNode.findPath("pay_type").asText());
                order.setOrderNo(jsonNode.findPath("order_no").asText());
                order.setInnerCode(jsonNode.findPath("inner_code").asText());
                order.setSkuName(jsonNode.findPath("sku_name").asText());
                order.setSkuId(jsonNode.findPath("sku_id").asLong());
                order.setPayStatus(jsonNode.findPath("pay_status").asInt());
                order.setBusinessName(jsonNode.findPath("business_name").asText());
                order.setBusinessId(jsonNode.findPath("business_id").asInt());
                order.setRegionId(jsonNode.findPath("region_id").asLong());
                order.setRegionName(jsonNode.findPath("region_name").asText());
                order.setPrice(jsonNode.findPath("price").asInt());
                order.setAmount(jsonNode.findPath("amount").asInt());
                order.setAddr(jsonNode.findPath("addr").asText());
                order.setOpenId(jsonNode.findPath("open_id").asText());

                order.setCreateTime(  LocalDateTime.parse( jsonNode.findPath("create_time").asText(),DateTimeFormatter.ISO_DATE_TIME ) );
                order.setUpdateTime(  LocalDateTime.parse( jsonNode.findPath("update_time").asText(),DateTimeFormatter.ISO_DATE_TIME ));

                orderList.add(order);
            }

            Pager<OrderViewModel> pager=new Pager<>();
            pager.setCurrentPageRecords(orderList);
            pager.setTotalCount( hits.getTotalHits().value );
            pager.setPageSize( searchHits.length );
            pager.setPageIndex(pageIndex);
            return  pager;

        } catch (IOException e) {
            e.printStackTrace();
            return Pager.buildEmpty();

        }

    }

    @Override
    public List<Long> getTop10Sku(Integer businessId) {

        //1.查询条件封装
        SearchRequest searchRequest=new SearchRequest("order");
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        //查询条件  最近三个月
        RangeQueryBuilder rangeQueryBuilder=QueryBuilders.rangeQuery("update_time");
        rangeQueryBuilder.gte( LocalDateTime.now().plusMonths(-3).format( DateTimeFormatter.ISO_DATE_TIME  )  );
        rangeQueryBuilder.lte( LocalDateTime.now().format( DateTimeFormatter.ISO_DATE_TIME  )  );

        BoolQueryBuilder boolQueryBuilder=QueryBuilders.boolQuery();

        boolQueryBuilder.must(rangeQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.termQuery( "business_id",businessId ) );//商圈

        sourceBuilder.query(boolQueryBuilder);

        AggregationBuilder orderAgg = AggregationBuilders.terms("sku").field("sku_id")
                .subAggregation(AggregationBuilders.count("count").field("sku_id"))
                .order(BucketOrder.aggregation("count", false))
                .size(10);
        sourceBuilder.aggregation(orderAgg);
        searchRequest.source(sourceBuilder);

        //2.结果的封装

        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null ) return  Lists.newArrayList();

            var terms = (Terms)aggregations.get("sku");
            var buckets = terms.getBuckets();

            return buckets.stream().map( b-> Long.valueOf(b.getKey().toString())   ).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }

    }


    /**
     * 获取特定合作商时间范围之内的收入合计
     * @param partnerId
     * @param start
     * @param end
     * @return
     */
    @Override
    public Long getAmount(Integer partnerId, String innerCode,LocalDateTime start, LocalDateTime end){
        SearchRequest searchRequest = new SearchRequest("order");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("create_time");
        rangeQueryBuilder.gte(start);
        rangeQueryBuilder.lte(end);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(rangeQueryBuilder);
        if(partnerId != null && partnerId > 0){
            //搜索特定合作商
            boolQueryBuilder.filter(QueryBuilders.termQuery("owner_id",partnerId));
        }
        if(!Strings.isNullOrEmpty(innerCode)){
            boolQueryBuilder.filter(QueryBuilders.termQuery("inner_code",innerCode));
        }
        //只对支付成功的订单做统计
        boolQueryBuilder.filter(QueryBuilders.termQuery("pay_status", VMSystem.PAY_STATUS_PAYED));
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.size(0);

        AggregationBuilder orderAgg = null;
        if(partnerId != null && partnerId > 0) {
            orderAgg = AggregationBuilders.sum("sum").field("bill");
        }else {
            orderAgg = AggregationBuilders.sum("sum").field("amount");
        }

        sourceBuilder.aggregation(orderAgg);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            var aggregations = searchResponse.getAggregations();
            if(aggregations == null) return 0L;
            Double value = ((ParsedSum)aggregations.get("sum")).getValue();
            return value.longValue();
        } catch (IOException e) {
            log.error("检索订单失败",e);
            return 0L;
        }

    }


    /**
     * 出货
     * @param orderNo
     */
    private void sendVendout(String orderNo){
        OrderEntity orderEntity = this.getByOrderNo(orderNo);

        VendoutReqData reqData = new VendoutReqData();
        reqData.setOrderNo(orderNo);
        reqData.setPayPrice(orderEntity.getAmount());
        reqData.setPayType(Integer.parseInt(orderEntity.getPayType()));
        reqData.setSkuId(orderEntity.getSkuId());
        reqData.setTimeout(60);
        reqData.setRequestTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        VendoutReq req = new VendoutReq();
        req.setVendoutData(reqData);
        req.setSn(System.nanoTime());
        req.setInnerCode(orderEntity.getInnerCode());
        req.setNeedResp(true);
        //向售货机发送出货请求
        try {
            mqttProducer.send(TopicConfig.getVendoutTopic(orderEntity.getInnerCode()),2,req);
        } catch (JsonProcessingException e) {
            log.error("send vendout req error.",e);
        }
    }




}
