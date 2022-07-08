package com.lkd.http.controller;
import com.lkd.entity.VmPolicyEntity;
import com.lkd.service.VmPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Deprecated
@RestController
@RequestMapping("/vmPolicy")
public class VmPolicyController {

    @Autowired
    private VmPolicyService vmPolicyService;

    /**
     * 根据id查询
     * @param id
     * @return 实体
     */
    @GetMapping("/{id}")
    public VmPolicyEntity findById(@PathVariable Long id){
        return vmPolicyService.getById( id );
    }

    /**
     * 新增
     * @param vmPolicy
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody VmPolicyEntity vmPolicy){
        return vmPolicyService.save( vmPolicy );
    }

    /**
     * 修改
     * @param id
     * @param vmPolicy
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody VmPolicyEntity vmPolicy){
        vmPolicy.setId( id );
        return vmPolicyService.updateById( vmPolicy );
    }
}
