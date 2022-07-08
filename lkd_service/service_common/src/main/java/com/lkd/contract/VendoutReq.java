package com.lkd.contract;

import lombok.Data;

/**
 * 出货请求
 */
@Data
public class VendoutReq extends BaseContract{
    public VendoutReq() {
        this.setMsgType("vendoutReq");
    }

    private VendoutReqData vendoutData;
}
