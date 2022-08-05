package com.lkd.emq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqttProducer {

    @Value("${mqtt.producer.defaultQos}")
    private int defaultProducerQos;
    @Value("${mqtt.producer.defaultRetained}")
    private boolean defaultRetained;
    @Value("${mqtt.producer.defaultTopic}")
    private String defaultTopic;

    @Autowired
    @Lazy
    private MqttClient mqttClient;

    public void send(String payload) {
        this.send(defaultTopic, payload);
    }

    /**
     *功能描述
     * @author liYuan
     * @date 2022/8/3 15:14
      * @param topic       主题的名字。需要被订阅才能接受到信息。
     * @param payload
     * @return void
     */
    public void send(String topic, String payload) {
        this.send(topic, defaultProducerQos, payload);
    }

    public void send(String topic, int qos, String payload) {
        this.send(topic, qos, defaultRetained, payload);
    }

    public void send(String topic, int qos, boolean retained, String payload) {
        try {
            mqttClient.publish(topic, payload.getBytes(), qos, retained);
        } catch (MqttException e) {
            log.error("publish msg error.",e);
        }
    }

    public <T extends Object> void send(String topic, int qos, T msg) throws JsonProcessingException {
        String payload = JsonUtil.serialize(msg);
        this.send(topic,qos,payload);
    }
}
