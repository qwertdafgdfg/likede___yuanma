package com.lkd.client.emq.msg;

import lombok.Data;

@Data
public class SkuPriceData {
    private String skuId;
    private Integer price;  //原价(以分为单位)
    private Integer realPrice; //真实售价(以分为单位)
    private boolean discount;  //是否折扣

}
