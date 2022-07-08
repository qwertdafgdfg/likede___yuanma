package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lkd.common.VMSystem;
import com.lkd.config.TopicConfig;
import com.lkd.contract.*;
import com.lkd.dao.VendingMachineDao;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.*;
import com.lkd.exception.LogicException;
import com.lkd.feignService.UserService;
import com.lkd.http.viewModel.CreateVMReq;
import com.lkd.service.*;
import com.lkd.utils.JsonUtil;
import com.lkd.viewmodel.*;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VendingMachineServiceImpl extends ServiceImpl<VendingMachineDao,VendingMachineEntity> implements VendingMachineService{


    @Autowired
    private VendoutRunningService vendoutRunningService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private VmCfgVersionService versionService;

    @Autowired
    private UserService userService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private VmTypeService vmTypeService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private MqttProducer mqttProducer;

//    @Autowired
//    private Sender sender;

    @Autowired
    private RestHighLevelClient eslClient;


    @Override
    public VendingMachineEntity findByInnerCode(String innerCode) {
        LambdaQueryWrapper<VendingMachineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VendingMachineEntity::getInnerCode,innerCode);

        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean add(CreateVMReq vendingMachine) {
        VendingMachineEntity vendingMachineEntity = new VendingMachineEntity();
        vendingMachineEntity.setInnerCode("");
        vendingMachineEntity.setNodeId(Long.valueOf(vendingMachine.getNodeId()));
        vendingMachineEntity.setVmType(vendingMachine.getVmType());
        NodeEntity nodeEntity = nodeService.getById(vendingMachine.getNodeId());
        if(nodeEntity == null){
            throw new LogicException("所选点位不存在");
        }
        String cityCode = nodeEntity.getArea().getCityCode();
        vendingMachineEntity.setAreaId(nodeEntity.getArea().getId());
        vendingMachineEntity.setBusinessId(nodeEntity.getBusinessId());
        vendingMachineEntity.setRegionId(nodeEntity.getRegionId());

        vendingMachineEntity.setCityCode(cityCode);
        vendingMachineEntity.setCreateUserId(Long.valueOf(vendingMachine.getCreateUserId()));
        vendingMachineEntity.setOwnerId(nodeEntity.getOwnerId());
        vendingMachineEntity.setOwnerName(nodeEntity.getOwnerName());


        //调用用户服务获取创建者姓名
        vendingMachineEntity.setCreateUserName(userService.getUser(vendingMachine.getCreateUserId()).getUserName());
        this.save(vendingMachineEntity);

        //设置售货机的innerCode
        UpdateWrapper<VendingMachineEntity> uw = new UpdateWrapper<>();
        String innerCode = generateInnerCode(vendingMachineEntity.getNodeId());
        uw.lambda()
                .set(VendingMachineEntity::getInnerCode,innerCode)
                .eq(VendingMachineEntity::getId,vendingMachineEntity.getId());
        this.update(uw);

        vendingMachineEntity.setInnerCode(innerCode);
        vendingMachineEntity.setClientId(generateClientId(innerCode));
        //创建货道数据
        createChannel(vendingMachineEntity);

        //创建版本数据
        versionService.initVersionCfg(vendingMachineEntity.getId(),innerCode);

        return true;
    }

    @Override
    public boolean update(Long id, Long nodeId) {
        VendingMachineEntity vm = this.getById(id);
        if(vm.getVmStatus() == VMSystem.VM_STATUS_RUNNING)
            throw new LogicException("改设备正在运营");
        NodeEntity nodeEntity = nodeService.getById(nodeId);
        vm.setNodeId(nodeId);
        vm.setRegionId(nodeEntity.getRegionId());
        vm.setBusinessId(nodeEntity.getBusinessId());
        vm.setOwnerName(nodeEntity.getOwnerName());
        vm.setOwnerId(nodeEntity.getOwnerId());

        return this.updateById(vm);
    }

    @Override
    public List<ChannelEntity> getAllChannel(String innerCode) {
        return channelService.getChannelesByInnerCode(innerCode);
    }


    @Override
    public List<SkuViewModel> getSkuList(String innerCode) {

        //货道查询
        List<ChannelEntity> channelList = this.getAllChannel(innerCode)
                .stream()
                .filter(c->c.getSkuId() > 0 && c.getSku() != null)
                .collect(Collectors.toList());

        //获取商品库存余量

        Map<SkuEntity, Integer> skuMap = channelList.stream().collect(Collectors.groupingBy(ChannelEntity::getSku, Collectors.summingInt(ChannelEntity::getCurrentCapacity)));

        //商品价格表  map   key  商品id
        Map<Long, IntSummaryStatistics> skuPrice =
                channelList.stream().collect(Collectors.groupingBy(ChannelEntity::getSkuId, Collectors.summarizingInt(ChannelEntity::getPrice)));

        return skuMap.entrySet().stream().map( entry->{
            SkuEntity sku = entry.getKey();
            sku.setRealPrice( skuPrice.get(sku.getSkuId()).getMin() );//真实价格
            SkuViewModel skuViewModel=new SkuViewModel();
            BeanUtils.copyProperties( sku,skuViewModel );
            skuViewModel.setImage( sku.getSkuImage() );
            skuViewModel.setCapacity(entry.getValue());//库存数
            return  skuViewModel;
        } ).sorted(Comparator.comparing(SkuViewModel::getCapacity).reversed())
                .collect( Collectors.toList() );


/*
        //获取有商品的货道
        List<ChannelEntity> channelList = this.getAllChannel(innerCode)
                                                .stream()
                                                .filter(c->c.getSkuId() > 0 && c.getSku() != null)
                                                .collect(Collectors.toList());
        Map<Long,SkuEntity> skuMap = Maps.newHashMap();
        //将商品列表去重之后计算出最终售价返回
        channelList
                .forEach(c->{
                    SkuEntity sku = c.getSku();

                    sku.setRealPrice(channelService.getRealPrice(innerCode,c.getSkuId()));
                    if(!skuMap.containsKey(sku.getSkuId())) {
                        sku.setCapacity(c.getCurrentCapacity());//货道库存
                        skuMap.put(sku.getSkuId(), sku);
                    }else {
                        SkuEntity value = skuMap.get(sku.getSkuId());
                        value.setCapacity(value.getCapacity()+c.getCurrentCapacity());//库存累加
                        skuMap.put(sku.getSkuId(),value);
                    }
                });
        if(skuMap.values().size() <= 0) return Lists.newArrayList();

        return skuMap
                    .values()
                    .stream()
                    .map(s->{
                        SkuViewModel sku = new SkuViewModel();
                        sku.setCapacity(s.getCapacity());
                        sku.setDiscount(s.isDiscount());
                        sku.setImage(s.getSkuImage());
                        sku.setPrice(s.getPrice());
                        sku.setRealPrice(s.getRealPrice());
                        sku.setSkuId(s.getSkuId());
                        sku.setSkuName(s.getSkuName());
                        sku.setUnit(s.getUnit());
                        return sku;
                    })
                    .sorted(Comparator.comparing(SkuViewModel::getCapacity).reversed())
                    .collect(Collectors.toList());
*/
    }




    @Override
    public SkuEntity getSku(String innerCode, long skuId) {
        SkuEntity skuEntity = skuService.getById(skuId);
        skuEntity.setRealPrice(channelService.getRealPrice(innerCode,skuId));
        LambdaQueryWrapper<ChannelEntity> qw = new LambdaQueryWrapper<>();
        qw
                .eq(ChannelEntity::getSkuId,skuId)
                .eq(ChannelEntity::getInnerCode,innerCode);
        List<ChannelEntity> channelList = channelService.list(qw);
        int capacity = 0;
        if(channelList == null || channelList.size() <= 0)
            capacity = 0;
        else
            capacity = channelList
                    .stream()
                    .map(ChannelEntity::getCurrentCapacity)
                    .reduce(Integer::sum).get();
        skuEntity.setCapacity(capacity);

        return skuEntity;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean supply(SupplyCfg supply) {
        VendingMachineEntity vendingMachineEntity = this.findByInnerCode(supply.getInnerCode());
        vendingMachineEntity.setLastSupplyTime(LocalDateTime.now());
        this.updateById(vendingMachineEntity);
        List<ChannelEntity> channelList = channelService.getChannelesByInnerCode(supply.getInnerCode());
        supply.getSupplyData()
                .forEach(
                        c -> {
                            Optional<ChannelEntity> item =
                                    channelList.stream()
                                            .filter(channel -> channel.getChannelCode().equals(c.getChannelId()))
                                            .findFirst();
                            if (item.isPresent()) {
                                ChannelEntity channelEntity = item.get();
                                channelEntity.setCurrentCapacity(channelEntity.getCurrentCapacity() + c.getCapacity());
                                channelEntity.setLastSupplyTime(LocalDateTime.now());
                                channelService.supply(channelEntity);
                            }
                        });
        //更新补货版本号；
        versionService.updateSupplyVersion(supply.getInnerCode());
        notifyGoodsStatus(supply.getInnerCode(),false);

        return true;
    }

    @Override
    public boolean updateStatus(String innerCode, int status) {
        try{
            UpdateWrapper<VendingMachineEntity> uw = new UpdateWrapper<>();
            uw.lambda()
            .eq(VendingMachineEntity::getInnerCode,innerCode)
            .set(VendingMachineEntity::getVmStatus,status);
            this.update(uw);

        }catch (Exception ex){
            log.error("updateStatus error,innerCode is " + innerCode + " status is " + status,ex);
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public boolean vendOutResult(VendoutResp vendoutResp) {
        try{
            String key = "vmService.outResult." + vendoutResp.getVendoutResult().getOrderNo();

            //对结果做校验，防止重复上传(从redis校验)
            Object redisValue = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);

            if(redisValue != null){
                log.info("出货重复上传");
                return false;
            }


            //存入出货流水数据
            VendoutRunningEntity vendoutRunningEntity = new VendoutRunningEntity();
            vendoutRunningEntity.setInnerCode(vendoutResp.getInnerCode());
            vendoutRunningEntity.setOrderNo(vendoutResp.getVendoutResult().getOrderNo());
            vendoutRunningEntity.setStatus(vendoutResp.getVendoutResult().isSuccess());
            vendoutRunningEntity.setPrice(vendoutResp.getVendoutResult().getPrice());
            vendoutRunningEntity.setSkuId(vendoutResp.getVendoutResult().getSkuId());
            vendoutRunningService.save(vendoutRunningEntity);


            //存入redis
            redisTemplate.opsForValue().set(key,key);
            redisTemplate.expire(key,7, TimeUnit.DAYS);

            //减货道库存
            ChannelEntity channel = channelService.getChannelInfo(vendoutResp.getInnerCode(),vendoutResp.getVendoutResult().getChannelId());
            int currentCapacity = channel.getCurrentCapacity() - 1;
            if(currentCapacity < 0) {
                log.info("缺货");
                notifyGoodsStatus(vendoutResp.getInnerCode(),true);

                return true;
            }

            channel.setCurrentCapacity(currentCapacity);
            channelService.updateById(channel);
        }catch (Exception e){
            log.error("update vendout result error.",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return false;
        }

        return true;
    }

    @Override
    public Pager<String> getAllInnerCodes(boolean isRunning, long pageIndex, long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VendingMachineEntity> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);

        QueryWrapper<VendingMachineEntity> qw = new QueryWrapper<>();
        if(isRunning){
            qw.lambda()
                    .select(VendingMachineEntity::getInnerCode)
                    .eq(VendingMachineEntity::getVmStatus,1);
        }else {
            qw.lambda()
                    .select(VendingMachineEntity::getInnerCode)
                    .ne(VendingMachineEntity::getVmStatus,1);
        }
        this.page(page,qw);
        Pager<String> result = new Pager<>();
        result.setCurrentPageRecords(page.getRecords().stream().map(VendingMachineEntity::getInnerCode).collect(Collectors.toList()));
        result.setPageIndex(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotalCount(page.getTotal());

        return result;
    }

    @Override
    public Pager<VendingMachineEntity> query(Long pageIndex, Long pageSize, Integer status,String innerCode) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VendingMachineEntity> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);
        LambdaQueryWrapper<VendingMachineEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(status != null){
            queryWrapper.eq(VendingMachineEntity::getVmStatus,status);
        }
        if(!Strings.isNullOrEmpty(innerCode)){
            queryWrapper.likeLeft(VendingMachineEntity::getInnerCode,innerCode);
        }
        this.page(page,queryWrapper);

        return Pager.build(page);
    }


    @Override
    public Integer getCountByOwnerId(Integer ownerId) {
        LambdaQueryWrapper<VendingMachineEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(VendingMachineEntity::getOwnerId,ownerId);

        return this.count(qw);
    }

    @Override
    public int inventory(int percent, VendingMachineEntity vmEntity) {

        //计算警戒线的值
        int maxCapacity = vmEntity.getType().getChannelMaxCapacity(); //售货机货道最大容量
        int alertValue =  (int)(maxCapacity * (float)percent/100);  //警戒值
        log.info("alertValue"+alertValue);
        //统计缺货货道数量
        QueryWrapper<ChannelEntity> channelQueryWrapper=new QueryWrapper<>();
        channelQueryWrapper.lambda()
                .eq(ChannelEntity::getVmId,vmEntity.getId()  )//售货机Id
                .ne(ChannelEntity::getSkuId, 0L )
                .le(ChannelEntity::getCurrentCapacity,alertValue);

        return   channelService.count(channelQueryWrapper);

    }

    @Override
    public void sendSupplyTask(VendingMachineEntity vmEntity) {

        //查询售货机的货道列表（ skuId!=0 ）
        QueryWrapper<ChannelEntity> channelQueryWrapper=new QueryWrapper<>();
        channelQueryWrapper.lambda()
                .eq(ChannelEntity::getVmId,vmEntity.getId()  )//售货机Id
                .ne(ChannelEntity::getSkuId, 0L );

        //货道列表
        List<ChannelEntity> channelList = channelService.list(channelQueryWrapper);
        //补货列表
        List<SupplyChannel> supplyChannelList = channelList.stream().map(c -> {
            SupplyChannel supplyChannel = new SupplyChannel();
            supplyChannel.setChannelId(c.getChannelCode());//货道编号
            supplyChannel.setCapacity(c.getMaxCapacity() - c.getCurrentCapacity());//补货量
            supplyChannel.setSkuId(c.getSkuId());
            supplyChannel.setSkuName(c.getSku().getSkuName());
            supplyChannel.setSkuImage(c.getSku().getSkuImage());
            return supplyChannel;
        }).collect(Collectors.toList());

        //构建补货协议数据
        SupplyCfg supplyCfg=new SupplyCfg();
        supplyCfg.setInnerCode(vmEntity.getInnerCode());
        supplyCfg.setSupplyData(supplyChannelList);
        supplyCfg.setMsgType("supplyTask");

        try {
            mqttProducer.send(TopicConfig.SUPPLY_TOPIC,2,supplyCfg);
            XxlJobLogger.log("发送补货数据："+ JsonUtil.serialize(supplyCfg));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public Boolean hasCapacity(String innerCode,Long skuId) {
        var qw = new LambdaQueryWrapper<ChannelEntity>();
        qw
                .eq(ChannelEntity::getInnerCode,innerCode)
                .eq(ChannelEntity::getSkuId,skuId)
                .gt(ChannelEntity::getCurrentCapacity,0);

        return channelService.count(qw) > 0;
    }

    @Override
    public Boolean setVMDistance(VMDistance vmDistance) {

        var vmEntity = this.findByInnerCode(vmDistance.getInnerCode());//查询售货机
        if(vmEntity==null){
            throw new LogicException("该设备编号不存在"+vmDistance.getInnerCode());
        }

        //向es中存入
        IndexRequest request=new IndexRequest("vm");
        request.id( vmDistance.getInnerCode() );
        request.source(
            "addr",vmEntity.getNode().getAddr(),
            "innerCode",vmDistance.getInnerCode() ,
            "nodeName",vmEntity.getNode().getName(),
            "typeName",vmEntity.getType().getName(),
            "location",vmDistance.getLat()+","+vmDistance.getLon()
        );

        try {
            eslClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("保存售货机位置信息失败",e);
            return false;
        }

        //向数据库中保存

        vmEntity.setLatitude(vmDistance.getLat());
        vmEntity.setLongitudes(vmDistance.getLon());
        this.updateById(vmEntity);

        return true;
    }

    @Override
    public List<VmInfoDTO> search(VmSearch vmSearch) {

        //1.封装查询条件

        SearchRequest searchRequest=new SearchRequest("vm");
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();

        GeoDistanceQueryBuilder geoDistanceQueryBuilder=new GeoDistanceQueryBuilder("location");
        geoDistanceQueryBuilder.distance(vmSearch.getDistance(), DistanceUnit.DEFAULT);//设置半径
        geoDistanceQueryBuilder.point(vmSearch.getLat(),vmSearch.getLon());//设置圆心

        //排序（由近到远）
        GeoDistanceSortBuilder geoDistanceSortBuilder=new GeoDistanceSortBuilder("location",vmSearch.getLat(),vmSearch.getLon());
        geoDistanceSortBuilder.unit(DistanceUnit.DEFAULT);
        geoDistanceSortBuilder.order(SortOrder.ASC);
        geoDistanceSortBuilder.geoDistance(GeoDistance.ARC );//  GeoDistance.PLANE 快，精准度低

        BoolQueryBuilder boolQueryBuilder=new BoolQueryBuilder();
        boolQueryBuilder.must(geoDistanceQueryBuilder);

        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.sort(geoDistanceSortBuilder);//排序
        searchRequest.source(sourceBuilder);


        //2.封装查询结果

        try {
            SearchResponse searchResponse = eslClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if(hits.getTotalHits().value<=0){
                return Lists.newArrayList();
            }
            List<VmInfoDTO> vmInfoList= Lists.newArrayList();
            Arrays.stream(hits.getHits()).forEach( h->{
                VmInfoDTO vmInfo=null;
                try {
                    vmInfo= JsonUtil.getByJson(h.getSourceAsString(), VmInfoDTO.class);
                    vmInfo.setDistance(  (int)(double)h.getSortValues()[0] );//查询结果距离圆心的长度
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(vmInfo!=null){
                    vmInfoList.add(vmInfo);
                }
            });

            return vmInfoList;
        } catch (IOException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }


    /**
     * 生成售货机InnerCode
     * @param nodeId 点位Id
     * @return
     */
    private String generateInnerCode(long nodeId){
        NodeEntity nodeEntity = nodeService.getById(nodeId);

        StringBuilder sbInnerCode = new StringBuilder(nodeEntity.getArea().getCityCode());

        int count = getCountByArea(nodeEntity.getArea());
        sbInnerCode.append(Strings.padStart(String.valueOf(count+1),5,'0'));

        return sbInnerCode.toString();
    }

    /**
     * 创建货道
     * @param vm
     * @return
     */
    private boolean createChannel(VendingMachineEntity vm){
        VmTypeEntity vmType = vmTypeService.getById(vm.getVmType());

        for(int i = 1; i <= vmType.getVmRow(); i++) {
            for(int j = 1; j <= vmType.getVmCol(); j++) {
                ChannelEntity channel = new ChannelEntity();
                channel.setChannelCode(i+"-"+j);
                channel.setCurrentCapacity(0);
                channel.setInnerCode(vm.getInnerCode());
                channel.setLastSupplyTime(vm.getLastSupplyTime());
                channel.setMaxCapacity(vmType.getChannelMaxCapacity());
                channel.setVmId(vm.getId());
                channelService.save(channel);
            }
        }

        return true;
    }

    /**
     * 获取某一地区下售货机数量
     * @param area
     * @return
     */
    private int getCountByArea(AreaEntity area){
        QueryWrapper<VendingMachineEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(VendingMachineEntity::getCityCode,area.getCityCode())
                .isNotNull(VendingMachineEntity::getInnerCode)
                .ne(VendingMachineEntity::getInnerCode,"");

        return this.count(qw);
    }

    /**
     * 发送缺货告警信息
     * @param innerCode
     * @param isFault true--缺货状态;false--不缺货状态
     */
    private void notifyGoodsStatus(String innerCode,boolean isFault){
        VmStatusContract contract = new VmStatusContract();
        contract.setNeedResp(false);
        contract.setSn(0);
        contract.setInnerCode(innerCode);

        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setStatus(isFault);
        statusInfo.setStatusCode("10003");
        List<StatusInfo> statusInfos = Lists.newArrayList();
        statusInfos.add(statusInfo);
        contract.setStatusInfo(statusInfos);

        try {
            //  发送设备不缺货消息(置设备为不缺货)
            mqttProducer.send(TopicConfig.VM_STATUS_TOPIC,2,contract);
        } catch (JsonProcessingException e) {
            log.error("serialize error.",e);
        }
    }

    /**
     * 生成售货机的clientId
     * @param innerCode
     * @return
     */
    private String generateClientId(String innerCode){
        String clientId = System.currentTimeMillis()+innerCode;

        return org.springframework.util.DigestUtils.md5DigestAsHex(clientId.getBytes());
    }
}
