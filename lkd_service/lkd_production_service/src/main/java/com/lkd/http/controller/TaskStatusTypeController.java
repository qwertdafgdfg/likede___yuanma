package com.lkd.http.controller;
import com.lkd.entity.TaskStatusTypeEntity;
import com.lkd.service.TaskStatusTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taskStatusType")
public class TaskStatusTypeController {

    @Autowired
    private TaskStatusTypeService taskStatusTypeService;

    /**
     * 根据statusId查询
     * @param statusId
     * @return 实体
     */
    @GetMapping("/{statusId}")
    public TaskStatusTypeEntity findById(@PathVariable Integer statusId){
        return taskStatusTypeService.getById( statusId );
    }

    /**
     * 新增
     * @param taskStatusType
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody TaskStatusTypeEntity taskStatusType){
        return taskStatusTypeService.save(taskStatusType);
    }

    /**
     * 修改
     * @param statusId
     * @param taskStatusType
     * @return 是否成功
     */
    @PutMapping("/{statusId}")
    public boolean update(@PathVariable Integer statusId,@RequestBody TaskStatusTypeEntity taskStatusType){
        taskStatusType.setStatusId( statusId );

        return taskStatusTypeService.updateById(taskStatusType);
    }

    /**
     * 删除
     * @param statusId
     * @return 是否成功
     */
    @DeleteMapping("/{statusId}")
    public  boolean delete(@PathVariable Integer statusId){
        return taskStatusTypeService.removeById(statusId);
    }

    /**
     * 获取所有状态类型
     * @return
     */
    @GetMapping("/list")
    public List<TaskStatusTypeEntity> getAll(){
        return taskStatusTypeService.list();
    }
}
