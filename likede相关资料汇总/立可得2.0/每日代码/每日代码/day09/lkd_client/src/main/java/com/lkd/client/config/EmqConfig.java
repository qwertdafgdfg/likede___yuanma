package com.lkd.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("emq")
public class EmqConfig {
    private String mqttServerUrl;
    private String mqttPassword;
    private String innerCode;
    private String publisTopicPrefix;
    private String clientId;
    private String subscribeTopic;

}
