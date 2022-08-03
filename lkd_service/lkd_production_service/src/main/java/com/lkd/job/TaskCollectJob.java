package com.lkd.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lkd.common.VMSystem;
import com.lkd.entity.TaskCollectEntity;
import com.lkd.entity.TaskEntity;
import com.lkd.service.TaskCollectService;
import com.lkd.service.TaskService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskCollectJob {


    @Autowired
    @Lazy
    private TaskService taskService;

    @Autowired
    @Lazy
    private TaskCollectService taskCollectService;

    /**
     * 每日工单数据汇总
     * @param param
     * @return
     */
    @XxlJob("taskCollectJobHandler")
    public ReturnT<String> collectTask(String param){

        var taskCollectEntity=new TaskCollectEntity();

        LocalDate start = LocalDate.now().plusDays(-1);

        taskCollectEntity.setProgressCount( this.count(start, VMSystem.TASK_STATUS_PROGRESS) );//进行中
        taskCollectEntity.setCancelCount( this.count(start, VMSystem.TASK_STATUS_CANCEL)  );//取消
        taskCollectEntity.setFinishCount(this.count(start, VMSystem.TASK_STATUS_FINISH)   );//完成
        taskCollectEntity.setCollectDate(start);
        clearData(start);//先清理汇总表数据
        taskCollectService.save(taskCollectEntity);
        cleanTask();//无效工单处理
        return ReturnT.SUCCESS;

    }

    /**
     * 清理某天数据
     * @param start
     */
    private void clearData(LocalDate start){
        var qw=new LambdaQueryWrapper<TaskCollectEntity>();
        qw.eq( TaskCollectEntity::getCollectDate,start );
        taskCollectService.remove(qw);
    }


    /**
     * 无效工单处理
     */
    private void cleanTask(){
        var um=new  UpdateWrapper<TaskEntity>();

        um.lambda().lt( TaskEntity::getUpdateTime ,LocalDate.now()  )
                .and( w->w.eq(TaskEntity::getTaskStatus,VMSystem.TASK_STATUS_CREATE   )
                        .or().eq(TaskEntity::getTaskStatus,VMSystem.TASK_STATUS_PROGRESS  )    )
                .set( TaskEntity::getTaskStatus,VMSystem.TASK_STATUS_CANCEL )
                .set( TaskEntity::getDesc,"工单超时");

        taskService.update(um);

    }


    /**
     * 按时间和状态进行统计
     * @param start
     * @param taskStatus
     * @return
     */
    private int count(LocalDate start,Integer taskStatus){
        var qw=new LambdaQueryWrapper<TaskEntity>();

        qw.ge( TaskEntity::getUpdateTime ,start )
                .lt( TaskEntity::getUpdateTime,start.plusDays(1) )
                .eq( TaskEntity::getTaskStatus,taskStatus);

        return taskService.count(qw);
    }


}
