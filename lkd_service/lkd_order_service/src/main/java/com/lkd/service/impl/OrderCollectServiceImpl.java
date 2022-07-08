package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.OrderCollectDao;
import com.lkd.entity.OrderCollectEntity;
import com.lkd.service.OrderCollectService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderCollectServiceImpl extends ServiceImpl<OrderCollectDao,OrderCollectEntity> implements OrderCollectService{
    @Override
    public List<OrderCollectEntity> getOwnerCollectByDate(Integer ownerId,LocalDate start,LocalDate end){
        QueryWrapper<OrderCollectEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(OrderCollectEntity::getOwnerId,ownerId)
                .ge(OrderCollectEntity::getDate,start)
                .le(OrderCollectEntity::getDate,end)
                .groupBy(OrderCollectEntity::getNodeName,OrderCollectEntity::getDate)
                .orderByDesc(OrderCollectEntity::getDate);

        return this.list(qw);
    }

//    @Override
//    public List<OrderCollectEntity> getCompanyTrend(int companyId, LocalDate start, LocalDate end) {
//        QueryWrapper<OrderCollectEntity> qw = new QueryWrapper<>();
////        qw.select("company_id","company_name","date","sum(order_total_money) as order_total_money","sum(vendout_total_count) as vendout_total_count");
////        qw.lambda()
////                .eq(OrderCollectEntity::getCompanyId,companyId)
////                .ge(OrderCollectEntity::getDate,start)
////                .le(OrderCollectEntity::getDate,end)
////                .groupBy(OrderCollectEntity::getDate,OrderCollectEntity::getCompanyName,OrderCollectEntity::getCompanyId)
////                .orderByAsc(OrderCollectEntity::getDate);
//
//        return this.list(qw);
//    }

//    @Override
//    public List<OrderCollectEntity> getAreaCollectByData(int areaId, LocalDate start, LocalDate end) {
//        QueryWrapper<OrderCollectEntity> qw = new QueryWrapper<>();
//        qw.select("date","sum(order_total_money) as order_total_money","sum(vendout_total_count) as vendout_total_count");
//        if(areaId > 0) {
//            qw.lambda()
//                    .eq(OrderCollectEntity::getAreaId, areaId)
//                    .ge(OrderCollectEntity::getDate, start)
//                    .le(OrderCollectEntity::getDate, end)
//                    .groupBy(OrderCollectEntity::getDate)
//                    .orderByAsc(OrderCollectEntity::getDate);
//        }else {
//            qw.lambda()
//                    .ge(OrderCollectEntity::getDate, start)
//                    .le(OrderCollectEntity::getDate, end)
//                    .groupBy(OrderCollectEntity::getDate)
//                    .orderByAsc(OrderCollectEntity::getDate);
//        }
//
//
//        List<OrderCollectEntity> result = this.list(qw);
//        Map<LocalDate,OrderCollectEntity> collectMap = result.stream()
//                .collect(Collectors.toMap(OrderCollectEntity::getDate,o->o));
//
//        List<OrderCollectEntity> resultData = Lists.newArrayList();
//        while (true){
//            OrderCollectEntity entity = collectMap.get(start);
//            if(entity == null){
//                entity = new OrderCollectEntity();
//                entity.setVendoutTotalCount(0);
//                entity.setDate(start);
//                entity.setOrderTotalMoney(0);
//            }
//            resultData.add(entity);
//            start = start.plusDays(1);
//            if(start.isAfter(end)) break;
//        }
//
//        return resultData;
//    }
//
//    @Override
//    public List<OrderEntity> getTop15Skus(LocalDateTime start, LocalDateTime end){
//        QueryWrapper<OrderEntity> qw = new QueryWrapper<>();
//
//        qw.select("sku_name","sum(amount) as amount","count(sku_id) as price")
//                .orderByDesc("price")
//                .lambda()
//                .eq(OrderEntity::getStatus,2)
//                .ge(OrderEntity::getCreateTime,start)
//                .le(OrderEntity::getCreateTime,end)
//                .groupBy(OrderEntity::getSkuName)
//        .last("limit 15");
//
//        return orderService.list(qw);
//    }
//
//
//    @Override
//    public OrderCollectEntity getCollectInfo(LocalDate start, LocalDate end) {
//        QueryWrapper<OrderCollectEntity> qw = new QueryWrapper<>();
//        qw.select("sum(order_total_money) as order_total_money","sum(vendout_total_count) as vendout_total_count");
//        qw.lambda()
//                .ge(OrderCollectEntity::getDate,start)
//                .le(OrderCollectEntity::getDate,end);
//
//        return this.getOne(qw);
//    }
}
