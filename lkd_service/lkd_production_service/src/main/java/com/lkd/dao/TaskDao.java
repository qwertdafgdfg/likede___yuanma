package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkd.entity.TaskEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TaskDao extends BaseMapper<TaskEntity> {
    @Select("select * from tb_task order by create_time desc")
    @Results(id = "taskMap",value = {
            @Result(column = "task_id",property = "taskId"),
            @Result(column = "product_type_id",property = "productTypeId"),
            @Result(column = "product_type_id",property = "taskType",one = @One(select = "com.lkd.dao.TaskTypeDao.selectById")),
            @Result(column = "task_status",property = "taskStatus"),
            @Result(column = "task_status",property = "taskStatusTypeEntity",one = @One(select = "com.lkd.dao.TaskStatusTypeDao.selectById"))
    })
    Page<TaskEntity> getTaskListByDate(Page<TaskEntity> page);

//    @Select("select * from tb_task where user_id=#{userId} order by ctime desc")
//    @ResultMap(value = "taskMap")
//    Page<TaskEntity> getTaskListByUserId(Page<TaskEntity> page, @Param("userId") int userId);
//
//    @Select("select * from tb_task where inner_code=#{innerCode} and task_status>0 order by ctime desc")
//    @ResultMap(value = "taskMap")
//    Page<TaskEntity> getTaskListByInnerCode(Page<TaskEntity> page,@Param("innerCode") String innerCode);
//
//    @Select("select * from tb_task where task_status=#{status} order by ctime desc")
//    @ResultMap(value = "taskMap")
//    Page<TaskEntity> getTaskListByStatus(Page<TaskEntity> page,@Param("status") int status);
//
//    @Select("select * from tb_task where inner_code=#{innerCode} and task_status=#{status} order by ctime desc")
//    @ResultMap(value = "taskMap")
//    Page<TaskEntity> getTaskListByInnerCodeAndStatus(Page<TaskEntity> page,@Param("innerCode") String innerCode,@Param("status") int status);

//    @Select("select * from tb_task ${ew.getCustomSqlSegment} order by ctime desc")
//    @ResultMap(value = "taskMap")
//    Page<TaskEntity> query(Page<TaskEntity> page,  @Param(Constants.WRAPPER) LambdaQueryWrapper wrapper);
}
