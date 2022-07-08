package com.lkd.emq;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
@Component
@Data
@Slf4j
public class MqttConfig {
    @Value("${mqtt.client.username}")
    private String username;
    @Value("${mqtt.client.password}")
    private String password;
    @Value("${mqtt.client.serverURI}")
    private String serverURI;
    @Value("${mqtt.client.clientId}")
    private String clientId;
    @Value("${mqtt.client.keepAliveInterval}")
    private int keepAliveInterval;
    @Value("${mqtt.client.connectionTimeout}")
    private int connectionTimeout;


    @Autowired
    private MqttCallback mqttCallback;

    @Bean
    public MqttClient mqttClient() {
        try {
            MqttClient client = new MqttClient(serverURI, clientId, mqttClientPersistence());
            client.setManualAcks(true); //设置手动消息接收确认
            mqttCallback.setMqttClient(client);
            client.setCallback(mqttCallback);
            client.connect(mqttConnectOptions());
            return client;
        } catch (MqttException e) {
            log.error("emq connect error",e);
            return null;
        }
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);//是否自动重新连接
        options.setCleanSession(true);//是否清除之前的连接信息
        options.setConnectionTimeout(connectionTimeout);//连接超时时间
        options.setKeepAliveInterval(keepAliveInterval);//心跳
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);//设置mqtt版本

        return options;
    }

    public MqttClientPersistence mqttClientPersistence() {
        return new MemoryPersistence();
    }

}
