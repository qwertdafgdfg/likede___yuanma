package com.lkd.contract;

import lombok.Data;

import java.util.List;

/**
 * 补货
 */
@Data
public class SupplyCfg extends BaseContract{
    private static final long serialVersionUID = -204540876123335095L;
    /**
     * 补货版本号
     */
    private long versionId;
    /**
     * 补货数据
     */
    private List<SupplyChannel> supplyData;

    public SupplyCfg() {
        this.setMsgType("supplyResp");
    }
}
