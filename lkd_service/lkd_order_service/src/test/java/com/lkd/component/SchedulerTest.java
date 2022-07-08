//package com.lkd.component;
//
//import com.lkd.redis.OrderRedisConfig;import com.lkd.redis.RedisUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class SchedulerTest{
//    @Autowired
//    private Scheduler scheduler;
//    @Autowired
//    private RedisUtils redisUtil;
//    @Test
//    public void generateCompanyListToCache() {
//        scheduler.generateCompanyListToCache();
//    }
//
//    @Test
//    public void collectCompanyDayData() {
//        scheduler.collectCompanyDayData();
//    }
//
//    @Test
//    public void testRedis(){
//        String key = OrderRedisConfig.COMPANY_LIST_PREFIX + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
//        int i = 2;
//        redisUtil.lPush(key,i);
//    }
//}