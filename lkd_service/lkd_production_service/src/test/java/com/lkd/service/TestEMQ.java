package com.lkd.service;
import com.lkd.emq.MqttProducer;
import com.lkd.utils.SegmentationCollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestEMQ {
    @Autowired
    private MqttProducer mqttProducer;

    @Test
    public void publish(){
        mqttProducer.send("lkdtest","{'name':'abc'}");
    }

}
