package com.lkd.business.contract;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayComplete implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单金额
     */
    private Integer amount;
    /**
     * 售货机编号
     */
    private String innerCode;
    /**
     * 商品Id
     */
    private Long skuId;
    /**
     * 支付方式
     */
    private int payType;
}
