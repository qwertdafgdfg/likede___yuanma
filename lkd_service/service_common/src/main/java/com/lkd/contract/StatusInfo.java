package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;

/**
 * 状态信息
 */
@Data
public class StatusInfo implements Serializable{
    /**
     * 状态码
     */
    private String statusCode;
    /**
     * 是否正常
     */
    private boolean status;
}
