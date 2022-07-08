package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品
 */
@Data
public class Sku implements Serializable{
    private static final long serialVersionUID = 8779495815557117605L;
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 商品Id
     */
    private String skuName;
    /**
     * 价格
     */
    private int price;
    /**
     * 所属类别Id
     */
    private long classId;
    /**
     * 所属类别名称
     */
    private String className;
    /**
     * 是否折扣
     */
    private boolean discount;
    /**
     * 单位,如500ml
     */
    private String unit;
    /**
     * 排列索引
     */
    private int index;
}
