package com.lkd.feignService;

import com.lkd.feignService.fallback.OrderServiceFallbackFactory;
import com.lkd.viewmodel.OrderViewModel;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.RequestPay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(value = "order-service",fallbackFactory = OrderServiceFallbackFactory.class)
public interface OrderService {

    @PostMapping("/wxpay/requestPay")
    String requestPay(@RequestBody RequestPay requestPay);


    @GetMapping("/order/cancel/{orderNo}")
    Boolean cancel(@PathVariable String orderNo);


    @GetMapping("/order/search")
    public Pager<OrderViewModel> search(
            @RequestParam(value = "pageIndex",required = false,defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
            @RequestParam(value = "orderNo",required = false,defaultValue = "") String orderNo,
            @RequestParam(value = "openId",required = false,defaultValue = "") String openId,
            @RequestParam(value = "startDate",required = false,defaultValue = "") String startDate,
            @RequestParam(value = "endDate",required = false,defaultValue = "") String endDate);


}
