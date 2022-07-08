package com.lkd.client.emq.msg;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseData implements Serializable{
    /**
     * 消息类型
     */
    private String msgType;
    /**
     * sn码 唯一标识
     */
    private long sn;
    /**
     * 售货机编码
     */
    private String innerCode;
    /**
     * 是否需要回传确认
     */
    private boolean needResp;

    private Integer versionId;
}
