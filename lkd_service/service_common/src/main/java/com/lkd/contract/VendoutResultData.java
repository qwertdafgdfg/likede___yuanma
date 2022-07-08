package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

@Data
public class VendoutResultData implements Serializable{
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 支付类型
     */
    private int payType;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 货道编号
     */
    private String channelId;
    /**
     * 出货完成时间
     */
    private String outTime;
    /**
     * 状态码(预留)
     */
    private int resultCode;
    /**
     * 商品价格
     */
    private int price;
}
