package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品配置
 */
@Data
public class SkuCfg extends BaseContract implements Serializable{
    private static final long serialVersionUID = -5924574533595856451L;
    /**
     * 版本号
     */
    private long versionId;
    private List<Sku> skus;
    public SkuCfg() {
        this.setMsgType("skuCfgResp");
    }
}
