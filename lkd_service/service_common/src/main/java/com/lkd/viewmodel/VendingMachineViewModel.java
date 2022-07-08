package com.lkd.viewmodel;

import lombok.Data;

@Data
public class VendingMachineViewModel{
    /**
     * 售货机Id
     */
    private long vmId;
    /**
     * 售货机编码
     */
    private String innerCode;

    /**
     * 区域Id
     */
    private Long regionId;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 合作商Id
     */
    private Integer ownerId;
    /**
     * 区域Id
     */
    private int areaId;
    /**
     * 售货机状态 0:未投放;1-运营;3-撤机
     */
    private int status;

    /**
     * 点位Id
     */
    private long nodeId;

    /**
     * 点位名称
     */
    private String nodeName;

    /**
     * 点位地址
     */
    private String nodeAddr;

    /**
     * 售货机状态
     */
    private Integer vmStatus;

    /**
     * 所属商圈Id
     */
    private Integer businessId;

    /**
     * 所属商圈名称
     */
    private String businessName;
    /**
     * 设备类型名
     */
    private String vmTypeName;
}
