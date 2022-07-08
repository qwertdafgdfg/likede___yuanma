package com.lkd.client.emq.msg;

import lombok.Data;

@Data
public class VendoutReq extends BaseData {

    private VendoutReqData vendoutData;
}
