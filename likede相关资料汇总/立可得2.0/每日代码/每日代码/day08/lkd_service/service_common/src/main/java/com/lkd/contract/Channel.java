package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

@Data
public class Channel implements Serializable{
    /**
     * 商品Id
     */
    private long skuId;
    /**
     * 余量
     */
    private int capacity;
    /**
     * 货道编号
     */
    private String channelId;
}
