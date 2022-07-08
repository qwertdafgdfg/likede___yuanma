package com.lkd.http.controller;

import com.lkd.entity.StatusTypeEntity;
import com.lkd.service.StatusTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statusType")
public class StatusTypeController {

    @Autowired
    private StatusTypeService statusTypeService;

    /**
     * 获取所有状态类型
     * @return
     */
    @GetMapping("/list")
    public List<StatusTypeEntity> getAllTypes(){
        return statusTypeService.list();
    }

    /**
     * 根据id查询
     * @param id
     * @return 实体
     */
    @GetMapping("/{id}")
    public StatusTypeEntity findById(@PathVariable Integer id){
        return statusTypeService.getById( id );
    }
}
