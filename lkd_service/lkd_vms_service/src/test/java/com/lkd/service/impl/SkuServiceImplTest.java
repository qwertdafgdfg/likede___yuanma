package com.lkd.service.impl;

import com.lkd.emq.MqttProducer;
import com.lkd.entity.SkuEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.SkuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SkuServiceImplTest{

    @Autowired
    private SkuService skuService;

    @Autowired
    private MqttProducer mqttProducer;
    @Test
    public void add() throws LogicException {
        SkuEntity skuEntity = new SkuEntity();
        skuEntity.setClassId(1);
        skuEntity.setCapacity(5);
        skuEntity.setPrice(100);
        skuEntity.setSkuName("test111");
        skuEntity.setUnit("100g");
//        skuService.add(skuEntity);
    }

    @Test
    public void sendMsg(){
        mqttProducer.send("vm/aa",2,"ttttttt");
    }
}