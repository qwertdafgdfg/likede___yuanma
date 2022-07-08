package com.lkd.client.emq.msg;

import lombok.Data;

/**
 *  商品基础信息交换协议
 */
@Data
public class SkuRespData {
    private String skuId;
    private String skuName;
    private String image;
    private Integer price;  //原价(以分为单位)
    private Integer realPrice; //真实售价(以分为单位)
    private String classId; //商品类别Id
    private String className; //类别名称
    private Boolean discount; //是否打折
    private String unit; //商品净含量
    private Integer index;  //商品排序索引
}
