package com.lkd.client.emq;

import com.lkd.client.config.EmqConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmqClient {
    @Autowired
    private EmqConfig emqConfig;

    private MqttClient mqttClient;

    /**
     * 连接mqtt borker
     */
    public void connect(MqttCallback callback){
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(emqConfig.getMqttServerUrl(),emqConfig.getClientId(),persistence);
        } catch (MqttException e) {
            log.error("mqtt creat error",e);
        }
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setAutomaticReconnect(false);
        connOpts.setUserName("monitor");
        connOpts.setPassword(emqConfig.getMqttPassword().toCharArray());
        connOpts.setCleanSession(false);
        connOpts.setAutomaticReconnect(true);
        try {
            mqttClient.setCallback(callback);
            mqttClient.connect(connOpts);
        } catch (MqttException e) {
            log.error("mqtt creat error",e);
        }
    }


    /**
     * 订阅及回调方法
     * @param topicFilter
     * @throws MqttException
     */
    public void subscribe(String topicFilter,MqttCallback callBack) throws MqttException {
        log.info("subscribe----------- {}",topicFilter);
        mqttClient.subscribe(topicFilter,2);
        mqttClient.setCallback(callBack);
    }

    /**
     * 发布消息
     * @param msgType
     * @param msg
     */
    @Async
    public void publish(String msgType,String msg){
        try {
            MqttMessage mqttMessage = new MqttMessage(msg.getBytes());
            mqttMessage.setQos(0);
            mqttMessage.setRetained(false);
            log.info("publish topic {}",emqConfig.getPublisTopicPrefix()+msgType);
            mqttClient.getTopic(emqConfig.getPublisTopicPrefix()+msgType).publish(mqttMessage);
        } catch (MqttException e) {
            log.error("mqtt publish msg error",e);
        }
    }
}
