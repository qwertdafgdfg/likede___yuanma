package com.lkd.client.emq.msg;

import lombok.Data;

@Data
public class VendoutReqData {

    private String skuId;
    private String requestTime;
    private String orderNo;
    private Integer timeout;
    private Integer payPrice;
    private Integer payType;
    private boolean success;
}
