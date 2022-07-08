package com.lkd.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkd.common.VMSystem;
import com.lkd.entity.OrderCollectEntity;
import com.lkd.service.OrderCollectService;
import com.lkd.service.OrderService;
import com.lkd.service.ReportService;
import com.lkd.viewmodel.BarCharCollect;
import com.lkd.viewmodel.Pager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderCollectService orderCollectService;
    private final OrderService orderService;
    private final RestHighLevelClient esClient;

    @Override
    public Pager<OrderCollectEntity> getPartnerCollect(Long pageIndex, Long pageSize, String name, LocalDate start, LocalDate end) {
        Page<OrderCollectEntity> page=new Page<>(pageIndex,pageSize);
        var qw=new QueryWrapper<OrderCollectEntity>();

        qw.select( "IFNULL( sum( order_count ),0) as order_count" ,
                "IFNULL( sum( total_bill ),0) as total_bill",
                "IFNULL( sum( order_total_money ),0) as order_total_money  " ,
                "IFNULL( min(ratio),0) as ratio ",
                "owner_name","date" );

        if(!Strings.isNullOrEmpty(name) ){
            qw.lambda().like(OrderCollectEntity::getOwnerName,name );
        }

        qw.lambda()
                .ge(OrderCollectEntity::getDate,start)
                .le(OrderCollectEntity::getDate,end)
                .groupBy( OrderCollectEntity::getOwnerName,OrderCollectEntity::getDate )
                .orderByDesc( OrderCollectEntity::getDate );


        return Pager.build( orderCollectService.page(page, qw) );
    }

    @Override
    public List<OrderCollectEntity> getTop12(Integer partnerId) {

        var qw=new LambdaQueryWrapper<OrderCollectEntity>();

        qw.select( OrderCollectEntity::getDate,  OrderCollectEntity::getNodeName,
                OrderCollectEntity::getOrderCount ,OrderCollectEntity::getTotalBill)
                .eq(OrderCollectEntity::getOwnerId,partnerId)
                .orderByDesc( OrderCollectEntity::getDate )
                .last("limit 12");
        return orderCollectService.list(qw);
    }

    @Override
    public Pager<OrderCollectEntity> search(Long pageIndex, Long pageSize, Integer partnerId, String nodeName, LocalDate start, LocalDate end) {
        var qw=new LambdaQueryWrapper<OrderCollectEntity>();

        qw.select( OrderCollectEntity::getDate,  OrderCollectEntity::getNodeName,
                OrderCollectEntity::getOrderCount ,OrderCollectEntity::getTotalBill)
                .eq(OrderCollectEntity::getOwnerId,partnerId);

        if(!Strings.isNullOrEmpty(nodeName)){
            qw.like( OrderCollectEntity::getNodeName ,nodeName);
        }
        if( start!=null && end!=null){
            qw.ge(OrderCollectEntity::getDate,start  )
                    .le(OrderCollectEntity::getDate,end);
        }
        qw.orderByDesc( OrderCollectEntity::getDate );

        Page<OrderCollectEntity> page=new Page<>(pageIndex,pageSize);
        return Pager.build( orderCollectService.page(page, qw) );
    }

    @Override
    public List<OrderCollectEntity> getList(Integer partnerId, String nodeName, LocalDate start, LocalDate end) {

        var qw=new LambdaQueryWrapper<OrderCollectEntity>();

        qw.select( OrderCollectEntity::getDate,  OrderCollectEntity::getNodeName,
                OrderCollectEntity::getOrderCount ,OrderCollectEntity::getTotalBill)
                .eq(OrderCollectEntity::getOwnerId,partnerId);

        if(!Strings.isNullOrEmpty(nodeName)){
            qw.like( OrderCollectEntity::getNodeName ,nodeName);
        }
        if( start!=null && end!=null){
            qw.ge(OrderCollectEntity::getDate,start  )
                    .le(OrderCollectEntity::getDate,end);
        }
        qw.orderByDesc( OrderCollectEntity::getDate );

        return orderCollectService.list(qw);
    }

    @Override
    public BarCharCollect getCollect(Integer partnerId, LocalDate start, LocalDate end) {

        var qw=new QueryWrapper<OrderCollectEntity>();
        qw.select(" IFNULL( sum(total_bill),0) as total_bill" ,"date" )
                .lambda()
                .ge(OrderCollectEntity::getDate ,start)
                .le(OrderCollectEntity::getDate,end)
                .eq(OrderCollectEntity::getOwnerId,partnerId)
                .groupBy(OrderCollectEntity::getDate);

        var mapCollect = orderCollectService.list(qw).stream()
                .collect(Collectors.toMap(OrderCollectEntity::getDate, OrderCollectEntity::getTotalBill));

        var result=new BarCharCollect();

        start.datesUntil( end.plusDays(1), Period.ofDays(1))
                .forEach( date->{
                    result.getXAxis().add( date.format(DateTimeFormatter.ISO_LOCAL_DATE) );
                    if( mapCollect.containsKey(date ) ){
                        result.getSeries().add( mapCollect.get(date) );
                    }else{
                        result.getSeries().add(0);
                    }
                } );

        return result;
    }

    @Override
    public BarCharCollect getAmountCollect(Integer collectType, LocalDate start, LocalDate end) {

        var groupColumn=  collectType==2?"MONTH(date)":"date";

        var dateColumn=  collectType==2?"min(date) as date":"date";

        var formatter=  collectType==2?DateTimeFormatter.ofPattern("yyyy-MM"):DateTimeFormatter.ISO_LOCAL_DATE;

        var period = collectType==2?Period.ofMonths(1):Period.ofDays(1);

        var qw=new QueryWrapper<OrderCollectEntity>();

        qw.select("IFNULL(sum(order_total_money),0) as order_total_money",dateColumn)
                .groupBy(groupColumn)
                .lambda()
                .ge( OrderCollectEntity::getDate,start )
                .le( OrderCollectEntity::getDate,end)
                .orderByAsc( OrderCollectEntity::getDate );

        var mapCollect=  orderCollectService.list( qw )
                .stream().collect( Collectors.toMap( o-> o.getDate().format( formatter ) ,OrderCollectEntity::getOrderTotalMoney  ) );
        var result=new BarCharCollect() ;

        start.datesUntil( end.plusDays(1), period)
                .forEach( date->{
                    String key= date.format( formatter );
                    result.getXAxis().add( key );
                    if( mapCollect.containsKey(key)){
                        result.getSeries().add( mapCollect.get(key) );
                    }else{
                        result.getSeries().add(0 );
                    }
                } );
        return result;
    }

    @Override
    public BarCharCollect getCollectByRegion(LocalDate start, LocalDate end) {

        SearchRequest searchRequest=new SearchRequest("order");
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.filter( QueryBuilders.rangeQuery("create_time").gte( start ).lte(end) );
        boolQueryBuilder.filter( QueryBuilders.termQuery( "pay_status",VMSystem.PAY_STATUS_PAYED ) );
        sourceBuilder.query(boolQueryBuilder );
        sourceBuilder.size(0);//不需要查询具体的数据

        AggregationBuilder regionAgg = AggregationBuilders.terms("region")
                .field("region_name")
                .subAggregation(AggregationBuilders.sum("amount_sum").field("amount"))
                .order(BucketOrder.aggregation("amount_sum", false))
                .size(30);

        sourceBuilder.aggregation(regionAgg);
        searchRequest.source(sourceBuilder);


        var result=new BarCharCollect();
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

            Aggregations aggregations = searchResponse.getAggregations();
            if(aggregations==null) return  result;
            var term= (ParsedStringTerms)aggregations.get("region");
            var buckets = term.getBuckets();
            if(buckets.size()<=0) return  result;

            //柱状图数据封装
            buckets.stream().forEach( b->{
                result.getXAxis().add( b.getKeyAsString() );
                var sumAgg= (ParsedSum)b.getAggregations().get("amount_sum");
                Double value= sumAgg.getValue();
                result.getSeries().add(  value.intValue() );
            } );


        } catch (IOException e) {
            e.printStackTrace();
            log.error("根据区域汇总数据出错",e);
        }

        return result;
    }


}
