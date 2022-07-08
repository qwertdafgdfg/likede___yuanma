package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.JobDao;
import com.lkd.entity.JobEntity;
import com.lkd.service.JobService;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImp extends ServiceImpl<JobDao,JobEntity> implements JobService{
    @Override
    public boolean setJob(int alertValue) {
        QueryWrapper<JobEntity> qw = new QueryWrapper<>();
        qw.eq("1",1);
        qw.last("LIMIT 1");

        JobEntity jobEntity = this.getOne(qw);
        if(jobEntity == null){
            JobEntity entity = new JobEntity();
            entity.setAlertValue(alertValue);

            return this.save(entity);
        }
        jobEntity.setAlertValue(alertValue);

        return this.updateById(jobEntity);
    }

    @Override
    public JobEntity getAlertValue() {
        QueryWrapper<JobEntity> qw = new QueryWrapper<>();
        qw.eq("1",1);

        JobEntity jobEntity = this.getOne(qw);

        return jobEntity;
    }
}
