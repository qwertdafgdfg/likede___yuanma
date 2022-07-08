package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.JobEntity;

public interface JobService extends IService<JobEntity> {
    /**
     * 设置缺货阈值和自动补货工单生成时间
     * @param alertValue
     * @return
     */
    boolean setJob(int alertValue);

    /**
     * 获取缺货阈值和工单生成时间配置
     * @return
     */
    JobEntity getAlertValue();
}