package com.lkd.http.controller;

import com.lkd.entity.BusinessTypeEntity;
import com.lkd.service.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/businessType")
public class BusinessTypeController {
    @Autowired
    @Lazy
    private BusinessTypeService businessTypeService;

    /**
     * 获取所有商圈
     * @return List<BusinessTypeEntity>
     */
    @GetMapping()
    public List<BusinessTypeEntity> getAll(){
        return businessTypeService.list();
    }
}
