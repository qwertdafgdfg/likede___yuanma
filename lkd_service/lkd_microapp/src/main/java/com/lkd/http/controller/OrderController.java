package com.lkd.http.controller;

import com.google.common.base.Strings;
import com.lkd.common.VMSystem;
import com.lkd.config.ConsulConfig;
import com.lkd.exception.LogicException;
import com.lkd.feignService.OrderService;
import com.lkd.feignService.VMService;
import com.lkd.service.WxService;
import com.lkd.utils.DistributedLock;
import com.lkd.viewmodel.OrderViewModel;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.RequestPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {


    @Autowired
    private WxService wxService;

    @Autowired
    private VMService vmService;

    @Autowired
    private OrderService orderService;

    /**
     * 获取openId
     * @param jsCode
     * @return
     */
    @GetMapping("/openid/{jsCode}")
    public String getOpenid(@PathVariable String jsCode){
        return wxService.getOpenId(jsCode);
    }



    @Autowired
    private ConsulConfig consulConfig;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 小程序请求支付
     * @param requestPay
     * @return
     */
    @PostMapping("/requestPay")
    public String requestPay(@RequestBody RequestPay requestPay){
        if(!vmService.hasCapacity(requestPay.getInnerCode()
                ,Long.valueOf(requestPay.getSkuId()))){
            throw new LogicException("该商品已售空");
        }
        if(Strings.isNullOrEmpty(requestPay.getOpenId())){
            requestPay.setOpenId( wxService.getOpenId(requestPay.getJsCode()) );
        }

        //分布式锁，机器同一时间只能处理一次出货
        DistributedLock lock = new DistributedLock(
                consulConfig.getConsulRegisterHost(),
                consulConfig.getConsulRegisterPort());
        DistributedLock.LockContext lockContext = lock.getLock(requestPay.getInnerCode(),60);
        if(!lockContext.isGetLock()){
            throw new LogicException("机器出货中请稍后再试");
        }
        //存入redis后是为了取消订单时释放锁
        redisTemplate.boundValueOps(VMSystem.VM_LOCK_KEY_PREF+requestPay.getInnerCode())
                .set(lockContext.getSession(), Duration.ofSeconds(60));

        String responseData = orderService.requestPay(requestPay);
        if(Strings.isNullOrEmpty(responseData)){
            throw new LogicException("微信支付接口调用失败");
        }
        return responseData;
    }



    /**
     * 取消订单
     * @param innerCode
     */
    @GetMapping("/cancelPay/{innerCode}/{orderNo}")
    public void cancel(@PathVariable String innerCode,@PathVariable String orderNo){
        DistributedLock lock = new DistributedLock(
                consulConfig.getConsulRegisterHost(),
                consulConfig.getConsulRegisterPort());
        String sessionId = redisTemplate.boundValueOps(VMSystem.VM_LOCK_KEY_PREF + innerCode).get();
        if(Strings.isNullOrEmpty(sessionId)) return;
        try {
            lock.releaseLock(sessionId);
            orderService.cancel(orderNo);
        }catch (Exception ex){
            log.error("取消订单出错",ex);
        }
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


}
