package com.lkd.http.controller;
import com.lkd.entity.TaskTypeEntity;
import com.lkd.service.TaskTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taskType")
public class TaskTypeController {

    @Autowired
    @Lazy
    private TaskTypeService taskTypeService;

    /**
     * 条件查询
     * @return 列表
     */
    @GetMapping("/list")
    public  List<TaskTypeEntity> findList(){
        return taskTypeService.list();
    }

    /**
     * 根据typeId查询
     * @param typeId
     * @return 实体
     */
    @GetMapping("/{typeId}")
    public TaskTypeEntity findById(@PathVariable Integer typeId){
        return taskTypeService.getById(typeId);
    }

    /**
     * 新增
     * @param taskType
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody TaskTypeEntity taskType){
        return taskTypeService.save(taskType);
    }

    /**
     * 修改
     * @param typeId
     * @param taskType
     * @return 是否成功
     */
    @PutMapping("/{typeId}")
    public boolean update(@PathVariable Integer typeId,@RequestBody TaskTypeEntity taskType){
        taskType.setTypeId( typeId );

        return taskTypeService.updateById(taskType);
    }

    /**
     * 删除
     * @param typeId
     * @return 是否成功
     */
    @DeleteMapping("/{typeId}")
    public  boolean delete(@PathVariable Integer typeId){
        return taskTypeService.removeById(typeId);
    }
}
