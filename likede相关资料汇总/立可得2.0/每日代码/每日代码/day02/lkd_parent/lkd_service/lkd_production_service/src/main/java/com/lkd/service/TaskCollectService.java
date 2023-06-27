package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.TaskCollectEntity;
import com.lkd.http.viewModel.TaskReportInfo;

import java.time.LocalDate;
import java.util.List;

public interface TaskCollectService extends IService<TaskCollectEntity>{
    /**
     * 获取工单报表
     * @param start
     * @param end
     * @return
     */
    List<TaskCollectEntity> getTaskReport(LocalDate start, LocalDate end);
}
