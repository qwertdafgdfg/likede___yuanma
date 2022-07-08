package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class TaskDetailsViewModel{
    /**
     * 货道编号
     */
    private String channelCode;
    /**
     * 期望补货数量
     */
    private int expectCapacity;
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
