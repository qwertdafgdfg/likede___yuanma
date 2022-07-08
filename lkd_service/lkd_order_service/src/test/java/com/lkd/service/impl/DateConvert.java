package com.lkd.service.impl;

import com.google.common.io.BaseEncoding;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
public class DateConvert {
    @Test
    public void convert(){
        LocalDateTime dateTime = LocalDateTime.parse("2020-10-12T15:51:44.000Z", DateTimeFormatter.ISO_DATE_TIME);
        System.out.println(dateTime);
    }

    @Test
    public void base64Decode(){
        String aa = "aaaa";

        String encodeStr = BaseEncoding.base64().encode(aa.getBytes());

        String base64Str = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJsa2QiLCJpYXQiOjE2MDY4NzkwMTksInVzZXJOYW1lIjoi5rWL6K-V5ZCI5L2c5ZWGIiwibW9iaWxlIjoiMTM4MDAwMDAwMDAiLCJ1c2VySWQiOjEsInJvbGVDb2RlIjpudWxsLCJyZWdpb25JZCI6bnVsbCwibG9naW5UeXBlIjoxLCJleHAiOjE2MDY4NzkwMTl9.Z52nMtjBcCkI0wiL-ritOgHk3d1JA9V9CLJ0S7Ig7po";
        String bodyData = base64Str.split("\\.")[1];
        String bodyStr = new String(BaseEncoding.base64().decode(bodyData), StandardCharsets.UTF_8);

        System.out.println(bodyStr);
    }
}
