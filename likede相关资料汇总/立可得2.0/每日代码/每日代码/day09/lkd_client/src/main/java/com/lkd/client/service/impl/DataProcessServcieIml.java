package com.lkd.client.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lkd.client.config.EmqConfig;
import com.lkd.client.emq.EmqClient;
import com.lkd.client.emq.msg.*;
import com.lkd.client.mapper.ChannelMapper;
import com.lkd.client.mapper.SkuMapper;
import com.lkd.client.mapper.VendoutOrderMapper;
import com.lkd.client.mapper.VersionMapper;
import com.lkd.client.pojo.Channel;
import com.lkd.client.pojo.Sku;
import com.lkd.client.pojo.VendoutOrder;
import com.lkd.client.pojo.Version;
import com.lkd.client.service.DataProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional //所有方法支持事物回滚
public class DataProcessServcieIml implements DataProcessService {

    @Autowired
    private ChannelMapper channelMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private VendoutOrderMapper vendoutOrderMapper;

    @Autowired
    private EmqClient emqClient;

    @Autowired
    private EmqConfig config;


    @Override
    public void syncSkus(SkuResp skuReq) {
        if(skuReq.getSkus()!=null&&!skuReq.getSkus().isEmpty()){
            skuMapper.deleteAllSkus();
            skuReq.getSkus().forEach(skuReqData -> {
                Sku sku=new Sku();
                BeanUtils.copyProperties(skuReqData,sku);
                skuMapper.insert(sku);
            });
            Version version= versionMapper.selectById(1);
            version.setSkuVersion(skuReq.getVersionId());
            versionMapper.updateById(version);
        }
    }

    @Override
    public void syncChannel(ChannelResp channelResp) {
        if(channelResp.getChannels()!=null&&!channelResp.getChannels().isEmpty()) {
            channelMapper.deleteAllChannels();
            channelResp.getChannels().forEach(channelRespData -> {
                Channel channel1=new Channel();
                BeanUtils.copyProperties(channelRespData,channel1);
                channelMapper.insert(channel1);
            });
            Version version = versionMapper.selectById(1);
            version.setChannelVersion(channelResp.getVersionId());
            versionMapper.updateById(version);
        }
    }

    @Override
    public void syncSkuPrices(SkuPriceResp skuPriceResp) {
        if(skuPriceResp.getSkuPrice()!=null&&!skuPriceResp.getSkuPrice().isEmpty()){
            skuPriceResp.getSkuPrice().forEach(skuPriceData -> {
                UpdateWrapper<Sku> ew = new UpdateWrapper<>();
                ew.lambda().set(Sku::getRealPrice,skuPriceData.getRealPrice())
                        .set(Sku::getPrice,skuPriceData.getPrice())
                        .set(Sku::getDiscount, skuPriceData.isDiscount())
                        .eq(Sku::getSkuId,skuPriceData.getSkuId());
                this.skuMapper.update(null, ew);
            });
            Version version = versionMapper.selectById(1);
            version.setSkuPriceVersion(skuPriceResp.getVersionId());
            versionMapper.updateById(version);
        }

    }

    @Override
    public void vendoutReq(VendoutReq vendoutResp) {
        if(vendoutResp.getVendoutData()!=null){
            VendoutOrder vendoutOrder=new VendoutOrder();
            String skuId= vendoutResp.getVendoutData().getSkuId();
            vendoutOrder.setSkuId(skuId);
            BeanUtils.copyProperties(vendoutResp.getVendoutData(),vendoutOrder);
            vendoutOrder.setOutTime(LocalDateTime.now());
            try {
                //硬件出完货 本地数据库修改库存记录
                QueryWrapper<Channel> channelQueryWrapper=new QueryWrapper<Channel>();
                //查询还存在该商品货道信息，并且按照容量降序
                channelQueryWrapper.lambda().eq(Channel::getSkuId,skuId).ge(Channel::getCapacity,1).
                        orderByDesc(Channel::getCapacity);
                List<Channel> channelList= channelMapper.selectList(channelQueryWrapper);
                if(channelList==null||channelList.isEmpty()){
                    //货道没有商品，出货失败
                    vendoutOrder.setResultCode(1); //货道为空
                    vendoutOrder.setSuccess(false);
                    return;
                }
                //出货前先把货道容量-1
                Channel channel= channelList.get(0);
                channel.setCapacity(channel.getCapacity()-1);
                channelMapper.updateById(channel);

                //等待三秒模拟硬件出货
                TimeUnit.SECONDS.sleep(3);

                log.info("vendoutOrder {} ",vendoutOrder);
                //出货成功
                vendoutOrder.setResultCode(0);
                vendoutOrder.setChannelId(channel.getChannelId());
                vendoutOrder.setSuccess(true);

            } catch (Exception e) {
                log.info("出货失败",e);
                vendoutOrder.setResultCode(2);  //硬件错误
                vendoutOrder.setSuccess(false);
                vendoutOrderMapper.insert(vendoutOrder);
            }
        }
    }

    @Override
    public void vendoutResp(String orderNo) {
        VendoutOrder vendoutOrder= vendoutOrderMapper.selectById(orderNo);
        if(vendoutOrder!=null){
            VendoutResult vendoutResult=new VendoutResult();
            vendoutResult.setInnerCode(config.getInnerCode());
            vendoutResult.setSn(System.nanoTime());
            vendoutResult.setNeedResp(true);
            if(vendoutOrder.getResultCode()==0){
                vendoutOrder.setSuccess(true);
            }
            vendoutResult.setVendoutResult(vendoutOrder);
            emqClient.publish(vendoutResult.getMsgType(), JSON.toJSONString(vendoutResult));
        }
    }

    @Override
    public void vendoutComplete(String orderNo) {
        log.info("-------------vendoutComplete-------------{}",orderNo);
        vendoutOrderMapper.deleteById(orderNo);
    }

    @Override
    public void checkVendoutOrder() {
        List<VendoutOrder> vendoutOrderList= vendoutOrderMapper.selectList(null);
        for (VendoutOrder vendoutOrder : vendoutOrderList) {
            VendoutResult vendoutResult=new VendoutResult();
            vendoutResult.setInnerCode(config.getInnerCode());
            vendoutResult.setSn(System.nanoTime());
            vendoutResult.setNeedResp(true);
            if(vendoutOrder.getResultCode()==0){
                vendoutOrder.setSuccess(true);
            }
            vendoutResult.setVendoutResult(vendoutOrder);
            emqClient.publish(vendoutResult.getMsgType(), JSON.toJSONString(vendoutResult));
        }
    }

    @Override
    public void supplyReq(SupplyReq supplyReq) {
           if(supplyReq.getSupplyData()!=null&&!supplyReq.getSupplyData().isEmpty()){
               supplyReq.getSupplyData().forEach(channelRespData -> {
                   Channel channel1=new Channel();
                   BeanUtils.copyProperties(channelRespData,channel1);
                   channelMapper.updateById(channel1);
               });
               Version version = versionMapper.selectById(1);
               version.setChannelVersion(supplyReq.getVersionId());
               versionMapper.updateById(version);
           }
    }

}
