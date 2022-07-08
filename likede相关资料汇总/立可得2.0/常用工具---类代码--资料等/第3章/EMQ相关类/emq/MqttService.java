package com.lkd.emq;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttService {
    void processMessage(String topic, MqttMessage message);
}
