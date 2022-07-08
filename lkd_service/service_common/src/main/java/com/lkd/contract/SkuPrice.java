package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品价格
 */
@Data
public class SkuPrice implements Serializable{
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 价格
     */
    private int price;
    /**
     * 真实价格
     */
    private int realPrice;
    /**
     * 是否折扣
     */
    private boolean discount;
}
