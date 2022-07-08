package com.lkd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 需要排除拦截的url配置
 */
@Configuration
@ConfigurationProperties("skipauth")
public class GatewayConfig{
    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    private String[] urls;
}
