package com.lkd.service.impl;

import com.google.common.collect.Maps;
import com.lkd.redis.RedisUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VMInfoServiceImplTest {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private RedisUtils redisUtils;
    @Test
    public void search() {
        redisUtils.set("test","test",60);
    }

    @Test
    public void addData(){
        Map<String,Object> vmInfoMap = Maps.newHashMap();
        vmInfoMap.put("addr","金燕龙4层茶水间");
        vmInfoMap.put("innerCode","01000001");
        vmInfoMap.put("nodeName","测试点位");
        vmInfoMap.put("location","40.066269,116.350478");

        IndexRequest request = new IndexRequest("vm");
        request.source(vmInfoMap);
        request.id("01000001");
        try {
            client.index(request, RequestOptions.DEFAULT);

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}