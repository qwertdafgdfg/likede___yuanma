package com.lkd.http.controller;

import com.lkd.entity.OrderEntity;
import com.lkd.service.OrderService;
import com.lkd.service.WXPayService;
import com.lkd.utils.ConvertUtils;
import com.lkd.viewmodel.CreateOrder;
import com.lkd.viewmodel.RequestPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/wxpay")
@Slf4j
public class WxPayController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WXPayService wxPayService;




    /**
     * 微信小程序支付
     * @param requestPay
     * @return
     */
    @PostMapping("/requestPay")
    public String requestPay(@RequestBody RequestPay requestPay){

        CreateOrder createOrder = new CreateOrder();
        BeanUtils.copyProperties( requestPay,createOrder );
        createOrder.setPayType("2");//支付方式微信支付
        OrderEntity orderEntity = orderService.createOrder(createOrder);//创建订单
        return wxPayService.requestPay(orderEntity.getOrderNo());//调用发起支付请求
    }


    /**
     * 微信支付回调接口
     * @param request
     * @return
     */
    @RequestMapping("/payNotify")
    @ResponseBody
    public void payNotify(HttpServletRequest request, HttpServletResponse response){
        try {
            //输入流转换为xml字符串
            String xml = ConvertUtils.convertToString( request.getInputStream() );
            wxPayService.notify(xml);

            //给微信支付一个成功的响应
            response.setContentType("text/xml");
            String data = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            response.getWriter().write(data);

        }catch (Exception e){
            log.error("支付回调处理失败",e);
        }
    }



}
