package com.lkd;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.HandlerExceptionResolver;

@EnableDiscoveryClient
@EnableCaching
@EnableConfigurationProperties
@EnableFeignClients
@EnableTransactionManagement
@SpringBootApplication
public class StatusServiceApplication {
    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }
    public static void main(String[] args) {
        Sentry.init("https://38ddc9b80c144435bd5feeccf4d93078@sentry.itheima.net/14");
        SpringApplication.run( StatusServiceApplication.class, args);
    }
}
