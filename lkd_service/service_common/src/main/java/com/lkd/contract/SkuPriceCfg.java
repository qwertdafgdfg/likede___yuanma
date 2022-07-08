package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 价格配置
 */
@Data
public class SkuPriceCfg extends BaseContract implements Serializable{
    private long versionId;
    private List<SkuPrice> skuPrice;

    public SkuPriceCfg() {
        this.setMsgType("skuPrice");
    }
}
