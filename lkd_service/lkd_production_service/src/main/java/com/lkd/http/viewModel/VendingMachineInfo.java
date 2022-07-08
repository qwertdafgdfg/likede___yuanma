package com.lkd.http.viewModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class VendingMachineInfo implements Serializable{
    /**
     * Id
     */
    private long id;

    /**
     * 设备类型(映射字段)
     */
    private int vmType;

    /**
     * 机器编号
     */
    private String innerCode;

    /**
     * 点位Id(映射字段)
     */
    private long nodeId;

    /**
     * 创建时间
     */
    @JsonProperty(value="cTime")
    private LocalDateTime cTime;

    /**
     * 更新时间
     */
    @JsonProperty(value = "uTime")
    private LocalDateTime uTime;

    /**
     * 机器当前状态
     */
    private int vmStatus;

    /**
     * 上次补货时间
     */
    private LocalDateTime lastSupplyTime;

    /**
     * 城市区号
     */
    private String cityCode;

    /**
     * 创建人Id
     */
    private long createUserId;


    /**
     * 创建人姓名
     */
    private String createUserName;
}
