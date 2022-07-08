package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

/**
 * 补货货道数据
 */
@Data
public class SupplyChannel implements Serializable{
    /**
     * 货道编号
     */
    private String channelId;
    /**
     * 补货容量
     */
    private int capacity;
    /**
     * 商品Id
     */
    private Long skuId;
    /**
     * 商品名称
     */
    private String skuName;
    /**
     * 商品图片
     */
    private String skuImage;
}
