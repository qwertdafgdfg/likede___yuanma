package com.lkd.service.impl;

import com.lkd.config.ConsulConfig;
import com.lkd.utils.DistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DistributedLockTest {

    @Autowired
    private ConsulConfig consulConfig;


    @Test
    public  void testGetLock(){
        DistributedLock distributedLock=
                new DistributedLock(consulConfig.getConsulRegisterHost(),consulConfig.getConsulRegisterPort());

        DistributedLock.LockContext lockContext = distributedLock.getLock("abc", 120);

        System.out.println(lockContext.getSession());
        System.out.println(lockContext.isGetLock());


    }

    @Test
    public  void testReleaseLock(){
        DistributedLock distributedLock=
                new DistributedLock(consulConfig.getConsulRegisterHost(),consulConfig.getConsulRegisterPort());

        distributedLock.releaseLock("1ad9ce6d-b078-1082-3a9b-f3bb48b4a4ea");


    }



}
