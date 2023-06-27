package com.lkd.contract;

import lombok.Data;

@Data
public class VendoutReqData{
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 出货请求时间
     */
    private String requestTime;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 出货请求要求超时时间(单位秒)
     */
    private int timeout;
    /**
     * 支付价格
     */
    private int payPrice;
    /**
     * 支付方式
     */
    private int payType;
}
