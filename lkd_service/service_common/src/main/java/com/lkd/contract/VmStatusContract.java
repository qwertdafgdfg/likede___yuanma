package com.lkd.contract;

import lombok.Data;

import java.util.List;

/**
 * 售货机状态
 */
@Data
public class VmStatusContract extends BaseContract{
    private List<StatusInfo> statusInfo;

    public VmStatusContract() {
        this.setMsgType("vmStatus");
    }
}
