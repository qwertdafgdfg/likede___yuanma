package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegionViewModel implements Serializable {
    private Long regionId;
    /**
     * 区域名称
     */
    private String regionName;
    /**
     * 区域下点位数
     */
    private Integer nodeCount;
}
