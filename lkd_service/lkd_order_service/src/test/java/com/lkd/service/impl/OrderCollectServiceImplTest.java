package com.lkd.service.impl;


import com.lkd.dao.OrderCollectDao;
import com.lkd.dao.OrderDao;
import com.lkd.entity.OrderCollectEntity;
import com.lkd.entity.OrderEntity;
import com.lkd.service.OrderCollectService;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderCollectServiceImplTest {

    @Autowired
    private OrderCollectService orderCollectService;
    @Autowired
    private OrderCollectDao orderCollectDao;
    @Autowired
    private OrderDao orderDao;

    @Test
    public void getAll() {
        //List<OrderCollectEntity> result = orderCollectService.getAll(LocalDate.now(),LocalDate.now(), DateType.Day);

        Assert.assertTrue(true);
    }

//    @Test
//    public void getCompanyTrend(){
//        List<OrderCollectEntity> result = orderCollectService.getCompanyTrend(1,LocalDate.parse("2019-09-28", DateTimeFormatter.ISO_LOCAL_DATE),LocalDate.parse("2019-10-07", DateTimeFormatter.ISO_LOCAL_DATE));
//        Assert.assertTrue(true);
//    }
//
//    @Test
//    public void getTop15Skus(){
//        List<OrderEntity> result = orderCollectService.getTop15Skus(LocalDateTime.parse("2019-01-01T00:00:00",DateTimeFormatter.ISO_DATE_TIME),
//                LocalDateTime.parse("2019-12-01T00:00:00",DateTimeFormatter.ISO_DATE_TIME));
//
//        Assert.assertTrue(true);
//    }
//
//    @Test
//    public void collect(){
//        OrderCollectEntity result = orderCollectService.getCollectInfo(LocalDate.of(2019,1,1),LocalDate.of(2019,12,31));
//        Assert.assertTrue(true);
//    }

    @Test
    public void generateData(){
        int[] companyIds = new int[]{1,2,3,4,5,6,7,8,9,10,11,12};
        int[] areaIds = new int[]{3,4,3581,3582,3583,3584,3585,3586,3587,3588,3589,3590,3591,3593,3592,3594,3595,354,353,352,351};
        LocalDate start = LocalDate.of(2019,1,1);
        LocalDate end = LocalDate.of(2021,12,31);
        long[] count = new long[]{1000,2131,4253,8903,2123,5642,8797,4324,7897,4352,1234,8793,6346,23423,789,4123,532,7878,533,3123,8779};
        while(true){
            LocalDate finalStart = start;
            Arrays.stream(companyIds).forEach(c->{
                Arrays.stream(areaIds).forEach(a->{
                    OrderCollectEntity entity = new OrderCollectEntity();
//                    entity.setAreaId(a);
//                    entity.setCompanyId(c);
                    //entity.setAreaName(vmService.findById(a).getData().getAreaName());
                    //entity.setCompanyName(companyService.findById(c).getData().getName());

                    List<Count> counts = Arrays.stream(count).mapToObj(cou->{
                        Count co = new Count();
                        co.setAmount(cou);
                        co.setUuid(UUID.randomUUID().toString());

                        return co;
                    })
                            .sorted(Comparator.comparing(Count::getUuid))
                            .collect(Collectors.toList());
                    long vCount = counts.get(0).getAmount();

                    long amount = vCount*3;
//                    entity.setVendoutTotalCount(vCount);
//                    entity.setOrderTotalMoney(amount);
                    entity.setDate(finalStart);
                    orderCollectDao.insert(entity);
                });

            });
            start = start.plusDays(1);
            if(start.isAfter(end)) break;
        }
    }

    @Test
    public void generateOrder(){
        long[] skuIds = new long[]{1,2,3,4,5,6,7,8,9,10,11};
        LocalDate start = LocalDate.of(2021,1,1);
        LocalDate end = LocalDate.of(2021,12,31);
        while (true){
            LocalDate finalStart = start;
            Arrays.stream(skuIds).forEach(skuId->{
                for (int i = 0; i < finalStart.getDayOfMonth()+skuId+10; i++) {
                    OrderEntity orderEntity = new OrderEntity();
                    orderEntity.setAmount(350+(int)skuId*100);
                    orderEntity.setPrice(350+(int)skuId*100);

                    orderEntity.setOrderNo(System.nanoTime()+"");
                    orderEntity.setThirdNo(System.nanoTime()+"");
                    orderEntity.setSkuId(skuId);
                    orderEntity.setStatus(2);
                    orderEntity.setPayStatus(1);
                    orderEntity.setInnerCode("037900004");
                    orderEntity.setPayType("1");

                    orderDao.insert(orderEntity);
                }
            });



            start = start.plusDays(1);
            if(start.isAfter(end)) break;
        }
    }

    @Test
    public void generateCData(){
//        QueryWrapper<OrderCollectEntity> wrapper = new QueryWrapper<>();
//        LocalDate start = LocalDate.of(2020,3,1);
//        LocalDate end = LocalDate.of(2020,3,31);
//        wrapper.lambda().ge(OrderCollectEntity::getDate,start).le(OrderCollectEntity::getDate,end);
//        orderCollectDao.selectList(wrapper).forEach(d->{
//            for (int i=1;i<1000;i++){
//                OrderCollectEntity entity = new OrderCollectEntity();
//                entity.setAreaId(d.getAreaId());
//                entity.setAreaName(d.getAreaName());
//                entity.setBill(Math.abs(d.getBill()));
//                entity.setCompanyId(d.getCompanyId());
//                entity.setCompanyName(d.getCompanyName());
//                entity.setOrderTotalMoney(Math.abs(d.getOrderTotalMoney()));
//                entity.setVendoutTotalCount(Math.abs(d.getVendoutTotalCount()));
//                entity.setDate(d.getDate().plusDays(i));
//                orderCollectDao.insert(entity);
//            }
//
//        });
    }

    @Data
    class Count{
        private long count;
        private String uuid;
        private long amount;
    }
}
