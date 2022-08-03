package com.lkd.http.controller;
import com.lkd.entity.RoleEntity;
import com.lkd.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
//@RequiredArgsConstructor
public class RoleController {
    @Autowired
    @Lazy
    private RoleService roleService;

    /**
     * 条件查询
     * @return 列表
     */
    @GetMapping
    public  List<RoleEntity> findList(){
        return roleService.list();
    }
}
