package com.lkd.contract.server;

import com.lkd.contract.AbstractContract;
import com.lkd.contract.SupplyChannel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SupplyTask extends AbstractContract implements Serializable {
    /**
     * 补货数据
     */
    private List<SupplyChannel> supplyData;

    /**
     * 售货机编号
     */
    private String innerCode;

    public SupplyTask() {
        this.setMsgType("supplyTask");
    }
}
