package com.lkd.service.impl;


//import com.github.wxpay.sdk.WXPayRequest;
//import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.WXPayRequest;
import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.WxPaySdkConfig;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.lkd.common.VMSystem;
import com.lkd.conf.WXConfig;
import com.lkd.entity.OrderEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.OrderService;
import com.lkd.service.WXPayService;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class WXPayServiceImpl implements WXPayService {
    @Autowired
    private WXConfig wxConfig;

    @Autowired
    private WxPaySdkConfig wxPaySdkConfig;

    @Autowired
    private OrderService orderService;

    @Override
    public String requestPay(String orderNo) {
        OrderEntity orderEntity = orderService.getByOrderNo(orderNo);
        try{
            String nonce_str = WXPayUtil.generateNonceStr();
            //1.封装请求参数
            Map<String,String> map= Maps.newHashMap();
            map.put("appid",wxPaySdkConfig.getAppID());//公众账号ID
            map.put("mch_id",wxPaySdkConfig.getMchID());//商户号
            map.put("nonce_str", nonce_str);//随机字符串
            map.put("body",orderEntity.getSkuName());//商品描述
            map.put("out_trade_no",orderNo);//订单号
            map.put("total_fee",orderEntity.getAmount()+"");//金额
            map.put("spbill_create_ip","127.0.0.1");//终端IP
            map.put("notify_url",wxConfig.getNotifyUrl());//回调地址
            map.put("trade_type","JSAPI");//交易类型
            map.put("openid",orderEntity.getOpenId());//openId
            String xmlParam  = WXPayUtil.generateSignedXml(map, wxPaySdkConfig.getKey());
            System.out.println("参数："+xmlParam);
            //2.发送请求
            WXPayRequest wxPayRequest=new WXPayRequest(wxPaySdkConfig);
            String xmlResult = wxPayRequest.requestWithCert("/pay/unifiedorder", null, xmlParam, false);
            //3.解析返回结果
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            //返回状态码
            String return_code = mapResult.get("return_code");
            //返回给移动端需要的参数
            Map<String, String> response = Maps.newHashMap();
            if(return_code.equals("SUCCESS")){
                // 业务结果
                String prepay_id = mapResult.get("prepay_id");//返回的预付单信息
                if(Strings.isNullOrEmpty(prepay_id)){
                    log.error("prepay_id is null","当前订单可能已经被支付");
                    throw new LogicException("当前订单可能已经被支付");
                }
                response.put("appId",wxConfig.getAppId());
                response.put("package", "prepay_id=" + prepay_id);
                response.put("signType","MD5");
                response.put("nonceStr", WXPayUtil.generateNonceStr());

                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");//要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误

                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String sign = WXPayUtil.generateSignature(response,wxConfig.getPartnerKey());
                response.put("paySign", sign);
                response.put("appId","");
                response.put("orderNo",orderNo);
                return JsonUtil.serialize(response);
            }else {
                log.error("调用微信统一下单接口失败"+JsonUtil.serialize(mapResult));
                return null;
            }
        }catch (Exception ex){
            log.error("调用微信统一下单接口失败",ex);
            return "";
        }
    }

    @Override
    public void notify(String notifyResult) throws Exception {
        //解析
        Map<String, String> map = WXPayUtil.xmlToMap( notifyResult );
        //验签
        boolean signatureValid = WXPayUtil.isSignatureValid(map, wxConfig.getPartnerKey());

        if(signatureValid){
            if("SUCCESS".equals(map.get("result_code"))){
                String orderNo = map.get("out_trade_no");
                OrderEntity orderEntity = orderService.getByOrderNo(orderNo);
                orderEntity.setStatus(VMSystem.ORDER_STATUS_PAYED);
                orderEntity.setPayStatus(VMSystem.PAY_STATUS_PAYED);
                orderService.updateById(orderEntity);
                //支付完成通知出货
                orderService.payComplete(orderNo);
            }else {
                log.error("支付回调出错:"+notifyResult);
            }
        }else {
            log.error("支付回调验签失败:"+notifyResult);
        }
    }

}