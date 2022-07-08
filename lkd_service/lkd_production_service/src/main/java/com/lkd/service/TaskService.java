package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.TaskEntity;
import com.lkd.entity.TaskStatusTypeEntity;
import com.lkd.exception.LogicException;
import com.lkd.http.viewModel.CancelTaskViewModel;

import com.lkd.http.viewModel.TaskReportInfo;
import com.lkd.http.viewModel.TaskViewModel;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.UserWork;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskService extends IService<TaskEntity> {


    /**
     * 创建工单
     * @param taskViewModel
     * @return
     */
    boolean createTask(TaskViewModel taskViewModel) throws LogicException;


    /**
     * 接受工单
     * @param id
     * @return
     */
    boolean accept(Long id);


    /**
     * 取消工单
     * @param id
     * @param cancelVM
     * @return
     */
    boolean cancelTask(Long id, CancelTaskViewModel cancelVM);


    /**
     * 完成工单
     * @param id
     * @return
     */
    boolean completeTask(long id);

    /**
     * 完成工单
     * @param id
     * @return
     */
    boolean completeTask(long id,Double lat,Double lon,String addr);

    /**
     * 获取所有状态类型
     * @return
     */
    List<TaskStatusTypeEntity> getAllStatus();

    /**
     * 通过条件搜索工单列表
     * @param pageIndex
     * @param pageSize
     * @param innerCode
     * @param userId
     * @param taskCode
     * @param isRepair 是否是运维工单
     * @return
     */
    Pager<TaskEntity> search(Long pageIndex,Long pageSize,String innerCode,Integer userId,String taskCode,Integer status,Boolean isRepair,String start,String end);


    /**
     * 获取某一区域工单量最少的人
     * @param regionId 区域id
     * @param isRepair 是否是维修工单
     * @return
     */
    Integer getLeastUser( Integer regionId,  Boolean isRepair  );


    /**
     * 获取工单的统计情况
     * @param start
     * @param end
     * @return
     */
    List<TaskReportInfo> getTaskReportInfo(LocalDateTime start ,LocalDateTime end);


    /**
     * 获取用户工作量详情
     * @param userId
     * @param start
     * @param end
     * @return
     */
    UserWork getUserWork( Integer userId,LocalDateTime start,LocalDateTime end);


    /**
     * 获取排名前10的用户
     * @param start
     * @param end
     * @param isRepair
     * @param regionId
     * @return
     */
    List<UserWork> getUserWorkTop10(LocalDate start,LocalDate end,Boolean isRepair, Long regionId  );


}
