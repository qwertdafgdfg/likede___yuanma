package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class BaseContract extends AbstractContract implements Serializable{
    /**
     * 协议通信匹配码
     */
    private long sn;
    /**
     * InnerCode售货机编号
     */
    private String innerCode;
    /**
     * 是否需要回馈
     */
    private boolean needResp;
}
