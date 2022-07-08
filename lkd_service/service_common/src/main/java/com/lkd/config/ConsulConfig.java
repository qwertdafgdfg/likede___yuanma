package com.lkd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulConfig {
    @Value("${spring.cloud.consul.host}")
    private String consulRegisterHost;

    @Value("${spring.cloud.consul.port}")
    private int consulRegisterPort;

    public String getConsulRegisterHost(){
        return consulRegisterHost;
    }

    public int getConsulRegisterPort(){
        return consulRegisterPort;
    }

}
