package com.lkd.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Strings;
import com.lkd.annotations.ProcessType;
import com.lkd.common.VMSystem;
import com.lkd.contract.server.OrderCheck;
import com.lkd.entity.OrderEntity;
import com.lkd.service.OrderService;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@ProcessType(value = "orderCheck")
@Slf4j
public class OrderCheckHandler implements MsgHandler{
    @Autowired
    private OrderService orderService;

    @Override
    public void process(String jsonMsg) throws IOException {
        log.info("超时订单处理");
        OrderCheck orderCheck = JsonUtil.getByJson(jsonMsg, OrderCheck.class);
        if(orderCheck == null || Strings.isNullOrEmpty(orderCheck.getOrderNo())) return;

        QueryWrapper<OrderEntity> qw = new QueryWrapper<>();
        qw
                .lambda()
                .eq(OrderEntity::getOrderNo,orderCheck.getOrderNo())
                .eq(OrderEntity::getStatus,VMSystem.ORDER_STATUS_CREATE);

        OrderEntity orderEntity = orderService.getOne(qw);
        if(orderEntity == null || orderEntity.getStatus() != VMSystem.ORDER_STATUS_CREATE) return;

        UpdateWrapper<OrderEntity> uw = new UpdateWrapper<>();
        uw
                .lambda()
                .eq(OrderEntity::getOrderNo,orderCheck.getOrderNo())
                .set(OrderEntity::getStatus, VMSystem.ORDER_STATUS_INVALID);
        orderService.update(uw);
    }

}