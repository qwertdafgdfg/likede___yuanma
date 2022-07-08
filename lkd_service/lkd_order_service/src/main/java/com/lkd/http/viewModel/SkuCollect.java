package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class SkuCollect{
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 商品名称
     */
    private String skuName;
    /**
     * 销售数量
     */
    private int count;
    /**
     * 销售额
     */
    private int amount;
}
