package com.lkd.contract;

import lombok.Data;

/**
 * 出货结果响应
 */
@Data
public class VendoutResp extends BaseContract{
    public VendoutResp() {
        this.setMsgType("vendoutResp");
    }

    private VendoutResultData vendoutResult;
}
