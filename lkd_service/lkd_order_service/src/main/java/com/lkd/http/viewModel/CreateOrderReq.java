package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class CreateOrderReq{
    /**
     * 售货机编号
     */
    private String innerCode;
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 第三方订单号
     */
    private String thirdNO;
    /**
     * 商品价格
     */
    private int price;
    /**
     * 扣款数额
     */
    private int amount;
    /**
     * 支付平台订单号
     */
    private String orderNo;
    /**
     * 支付类型
     */
    private String payType;
}
