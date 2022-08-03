package com.lkd.http.controller;

import com.lkd.entity.TaskDetailsEntity;
import com.lkd.service.TaskDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taskDetails")
public class TaskDetailsController {

    @Autowired
    @Lazy
    private TaskDetailsService taskDetailsService;


    /**
     * 根据detailsId查询
     * @param taskId
     * @return 实体
     */
    @GetMapping("/{taskId}")
    public List<TaskDetailsEntity> findById(@PathVariable String taskId){
        return taskDetailsService.getByTaskId(Long.valueOf(taskId));
    }

    /**
     * 新增
     * @param taskDetails
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody TaskDetailsEntity taskDetails){
        return taskDetailsService.save(taskDetails);
    }

    /**
     * 修改
     * @param detailsId
     * @param taskDetails
     * @return 是否成功
     */
    @PutMapping("/{detailsId}")
    public boolean update(@PathVariable Long detailsId,@RequestBody TaskDetailsEntity taskDetails){
        taskDetails.setDetailsId( detailsId );

        return taskDetailsService.updateById(taskDetails);
    }

    /**
     * 删除
     * @param detailsId
     * @return 是否成功
     */
    @DeleteMapping("/{detailsId}")
    public  boolean delete(@PathVariable Long detailsId){
        return taskDetailsService.removeById( detailsId );
    }
}
