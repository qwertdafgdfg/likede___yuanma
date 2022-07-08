package com.lkd.http.controller;

import com.lkd.entity.VmTypeEntity;
import com.lkd.service.VmTypeService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vmType")
public class VmTypeController {

    @Autowired
    private VmTypeService vmTypeService;

    /**
     * 根据typeId查询
     * @param typeId
     * @return 实体
     */
    @GetMapping("/{typeId}")
    public VmTypeEntity findById(@PathVariable Integer typeId){
        return vmTypeService.getById(typeId);
    }

    /**
     * 新增
     * @param vmType
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody VmTypeEntity vmType){
        return vmTypeService.save(vmType);
    }

    /**
     * 修改
     * @param typeId
     * @param vmType
     * @return 是否成功
     */
    @PutMapping("/{typeId}")
    public boolean update(@PathVariable Integer typeId,@RequestBody VmTypeEntity vmType){
        vmType.setTypeId( typeId );
        return vmTypeService.updateById(vmType);
    }

    /**
     * 删除
     * @param typeId
     * @return 是否成功
     */
    @DeleteMapping("/{typeId}")
    public  boolean delete(@PathVariable Integer typeId){
        return vmTypeService.delete(typeId);
    }

    /**
     * 获取所有售货机型号
     * @return
     */
    @GetMapping("/all")
    public List<VmTypeEntity> getAll(){
        return vmTypeService.list();
    }

    /**
     * 根据名称搜索
     * @param pageIndex
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/search")
    public Pager<VmTypeEntity> search(
            @RequestParam(value = "pageIndex",required = false,defaultValue = "1") long pageIndex,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") long pageSize,
            @RequestParam(value = "name",required = false,defaultValue = "") String name){
        return vmTypeService.search(pageIndex,pageSize,name);
    }
}
