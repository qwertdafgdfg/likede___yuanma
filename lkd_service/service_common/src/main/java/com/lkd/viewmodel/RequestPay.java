package com.lkd.viewmodel;

import lombok.Data;

@Data
public class RequestPay {

    /**
     * 售货机编号
     */
    private String innerCode;

    /**
     * 小程序端JsCode
     */
    private String jsCode;

    /**
     * openId
     */
    private String openId;

    /**
     * 商品Id
     */
    private String skuId;


}
