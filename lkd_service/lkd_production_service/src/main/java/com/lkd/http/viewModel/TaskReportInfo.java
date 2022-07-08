package com.lkd.http.viewModel;

import lombok.Data;

/**
 * 工单基本统计情况
 */
@Data
public class TaskReportInfo {

    private Integer total;//工单总数

    private Integer completedTotal;//完成数

    private Integer cancelTotal;//拒绝数

    private Integer progressTotal;// 进行数

    private Integer workerCount;//工作人数

    private boolean repair;//是否是运维

    private String date;//日期


}
