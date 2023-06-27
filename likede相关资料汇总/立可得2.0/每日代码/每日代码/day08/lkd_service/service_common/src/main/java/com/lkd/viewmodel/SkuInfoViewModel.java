package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;

@Data
@Deprecated
public class SkuInfoViewModel extends SkuViewModel implements Serializable {

    /**
     * 点位地址
     */
    private String addr;

    /**
     * 设备编号
     */
    private String innerCode;
}
