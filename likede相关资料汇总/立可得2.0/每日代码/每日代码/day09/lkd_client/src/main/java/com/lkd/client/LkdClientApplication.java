package com.lkd.client;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.lkd.client.mapper")
@EnableAsync
public class LkdClientApplication{

    public static void main(String[] args) {
        SpringApplication.run(LkdClientApplication.class, args);
    }
}
