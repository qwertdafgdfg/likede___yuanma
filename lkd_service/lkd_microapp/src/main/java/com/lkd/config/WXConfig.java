package com.lkd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("wxpay")
@Data
public class WXConfig {
    private String appId;
    private String appSecret;
}
