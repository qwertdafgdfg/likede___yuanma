package com.lkd.contract;

import lombok.Data;

/**
 * 完成工单协议
 */
@Data
public class TaskCompleteContract extends BaseContract{
    public TaskCompleteContract() {
        this.setMsgType("taskCompleted");
    }

    private int taskType;//工单类型

    private Double lat;//纬度

    private Double lon;//经度

}
