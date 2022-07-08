package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class OrderResp{
    /**
     * 售货机编号
     */
    private String innerCode;
    /**
     * 支付平台订单号
     */
    private String orderNo;
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 第三方平台订单号
     */
    private String thirdNO;
    /**
     * 价格
     */
    private int price;
    /**
     * 扣款额
     */
    private int amount;
    /**
     * 支付是否成功
     */
    private boolean success;
    /**
     * 消息
     */
    private String msg;
}
