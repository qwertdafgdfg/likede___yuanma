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
 
        });

    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    
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
        } catch (MqttException e) {
            log.info("connectComplete",e);
        }
    }
}
