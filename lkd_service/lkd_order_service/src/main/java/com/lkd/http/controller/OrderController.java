package com.lkd.http.controller;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.google.common.base.Strings;
import com.lkd.common.VMSystem;
import com.lkd.service.OrderService;
import com.lkd.utils.DistributedLock;
import com.lkd.viewmodel.BarCharCollect;
import com.lkd.viewmodel.OrderViewModel;
import com.lkd.viewmodel.Pager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    /**
     * 取消订单
     * @param orderNo
     * @return
     */
    @GetMapping("/cancel/{orderNo}")
    public Boolean cancel(@PathVariable String orderNo){
        return orderService.cancel(orderNo);
    }


    /**
     * 搜索订单
     * @param pageIndex
     * @param pageSize
     * @param orderNo
     * @param openId
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping("/search")
    public Pager<OrderViewModel> search(
            @RequestParam(value = "pageIndex",required = false,defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderNo",required = false,defaultValue = "") String orderNo,
            @RequestParam(value = "openId",required = false,defaultValue = "") String openId,
            @RequestParam(value = "startDate",required = false,defaultValue = "") String startDate,
            @RequestParam(value = "endDate",required = false,defaultValue = "") String endDate){
        return orderService.search(pageIndex,pageSize,orderNo,openId,startDate,endDate);
    }


    /**
     * 获取商圈下3个月内销量前十商品
     * @param businessId
     * @return
     */
    @GetMapping("/businessTop10/{businessId}")
    public List<Long> getBusinessTop10Skus(@PathVariable  Integer businessId){
        return  orderService.getTop10Sku(businessId);
    }




}
