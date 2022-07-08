package com.lkd.client.emq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.lkd.client.config.EmqConfig;
import com.lkd.client.config.MsgType;
import com.lkd.client.emq.msg.*;
import com.lkd.client.mapper.VersionMapper;
import com.lkd.client.pojo.Version;
import com.lkd.client.service.DataProcessService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * emq回调类
 */
@Component
@Slf4j
public class EmqMsgCallback implements MqttCallbackExtended {
    @Autowired
    private EmqClient emqClient;

    @Autowired
    private  EmqConfig config;

    @Autowired
    private  DataProcessService dataProcessService;

    @Autowired
    VersionMapper versionMapper;

    @Override
    public void connectionLost(Throwable throwable) {
        log.info("emq connect lost",throwable);
        try {
           TimeUnit.SECONDS.sleep(10);
           //连接中断，从新建立连接
           emqClient.connect(this);
           //订阅服务器通知topic
            emqClient.subscribe(config.getSubscribeTopic(),this);
        } catch (Exception e) {
          log.info("订阅失败",e);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        CompletableFuture.runAsync(()->{
            System.out.println(topic);
            String payload = new String(mqttMessage.getPayload());

            String msgType = (String) JSONPath.read(payload, "$.msgType");
            log.info("************msgType {} payload {}*************", msgType,payload);
            //商品同步
            if(MsgType.skuCfg.getType().equalsIgnoreCase(msgType)){
                SkuResp skuResp= JSON.parseObject(payload, SkuResp.class);
                log.info("skuCfgResp {}", skuResp);
                dataProcessService.syncSkus(skuResp);
            }
            //货架同步
            if(MsgType.channelCfg.getType().equalsIgnoreCase(msgType)){
                ChannelResp channelResp= JSON.parseObject(payload, ChannelResp.class);
                log.info("channelCfg {}",channelResp);
                dataProcessService.syncChannel(channelResp);
            }
            //商品价格变动通知
            if(MsgType.skuPrice.getType().equalsIgnoreCase(msgType)){
                SkuPriceResp skuPriceResp= JSON.parseObject(payload, SkuPriceResp.class);
                log.info("skuPriceResp {}",skuPriceResp);
                dataProcessService.syncSkuPrices(skuPriceResp);
            }
            //出货通知
            if(MsgType.vendoutReq.getType().equalsIgnoreCase(msgType)){
                VendoutReq vendoutResp= JSON.parseObject(payload, VendoutReq.class);
                log.info("出货通知 vendoutReq {}",vendoutResp);
                try {
                    dataProcessService.vendoutReq(vendoutResp);
                }catch (Exception ex){
                    log.info("出货异常",ex);
                } finally {
                    //上报服务器
                    dataProcessService.vendoutResp(vendoutResp.getVendoutData().getOrderNo());
                }
            }
            //补货通知
            if(MsgType.supplyReq.getType().equalsIgnoreCase(msgType)){
                SupplyReq supplyReq= JSON.parseObject(payload, SupplyReq.class);
                dataProcessService.supplyReq(supplyReq);
            }
        });

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        CompletableFuture.runAsync(()->{
            try {
                MqttMessage mqttMessage= iMqttDeliveryToken.getMessage();
                String payload = new String(mqttMessage.getPayload());
                String msgType = (String) JSONPath.read(payload, "$.msgType");
                log.info("-------------deliveryComplete-------------{},{}",msgType,payload);
                if(MsgType.vendoutResp.getType().equalsIgnoreCase(msgType)){
                    String orderNo = (String) JSONPath.read(payload, "$.vendoutResult.orderNo");
                    dataProcessService.vendoutComplete(orderNo);
                }
            } catch (Exception e) {
                log.info("deliveryComplete",e);
            }
        });
    }

    @Override
    public void connectComplete(boolean connectComplete, String s) {
        if(!connectComplete){
            return;
        }
        //订阅服务器topic,及回调处理方法
        try {
            log.info("connectComplete subscribe {} ",config.getSubscribeTopic());
            emqClient.subscribe(config.getSubscribeTopic(),this);

            Version verson= versionMapper.selectById(1);
            //发布同步消息
            VersionReq versionReq=new VersionReq();
            versionReq.setInnerCode(config.getInnerCode());
            versionReq.setSn(System.nanoTime());
            versionReq.getData().setChannelCfg(verson.getChannelVersion());
            versionReq.getData().setSkucfgVersion(verson.getSkuVersion());
            String msg=JSON.toJSONString(versionReq);
            log.info("msg"+msg);
            emqClient.publish(MsgType.versionCfg.getType(), JSON.toJSONString(versionReq));
            //检查是否未同步的出货信息
            dataProcessService.checkVendoutOrder();
        } catch (MqttException e) {
            log.info("connectComplete",e);
        }
    }
}
