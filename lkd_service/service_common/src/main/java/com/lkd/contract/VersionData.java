package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

@Data
public class VersionData implements Serializable{
    /**
     * 商品配置版本
     */
    private long skucfgVersion;
    /**
     * 货道配置版本
     */
    private long channelCfg;
    /**
     * 商品价格版本
     */
    private long skuPriceCfg;
}
