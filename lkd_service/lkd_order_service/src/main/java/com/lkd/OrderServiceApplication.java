package com.lkd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDiscoveryClient
@EnableCaching
@EnableConfigurationProperties
@EnableFeignClients
@EnableTransactionManagement
@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.lkd","com.github.wxpay.sdk"})
//@EnableScheduling
//@EnableAsync
//@MapperScan("com.lkd.dao")
public class OrderServiceApplication {
//    @Bean
//    public HandlerExceptionResolver sentryExceptionResolver() {
//        return new io.sentry.spring.SentryExceptionResolver();
//    }
    public static void main(String[] args) {
//        Sentry.init("https://38ddc9b80c144435bd5feeccf4d93078@sentry.itheima.net/14");
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}