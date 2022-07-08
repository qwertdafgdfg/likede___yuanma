package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TaskViewModel implements Serializable{
    /**
     * 工单名称
     */
    @Deprecated
    private String taskName;
    /**
     * 工单类型
     */
    private int createType;
    /**
     * 关联设备编号
     */
    private String innerCode;


    /**
     * 用户创建人id
     */
    private Integer userId;


    /**
     * 用户名称
     */
    private String userName;


    /**
     * 任务执行人Id
     */
    private Integer assignorId;

    /**
     * 工单类型
     */
    private int productType;
    /**
     * 描述信息
     */
    private String desc;
    /**
     * 期望完成时间
     */
    @Deprecated
    private String expect;
    /**
     * 工单详情(只有补货工单才涉及)
     */
    private List<TaskDetailsViewModel> details;
}
