package com.lkd.http.controller;
import com.lkd.entity.VendoutRunningEntity;
import com.lkd.service.VendoutRunningService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vendoutRunning")
public class VendoutRunningController {

    @Autowired
    private VendoutRunningService vendoutRunningService;

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return 列表
     */
    @GetMapping
    public  List<VendoutRunningEntity> findList(@RequestParam Map searchMap){
        return vendoutRunningService.findList( searchMap );
    }

    /**
     * 根据id查询
     * @param id
     * @return 实体
     */
    @GetMapping("/{id}")
    public VendoutRunningEntity findById(@PathVariable Long id){
        return vendoutRunningService.getById(id);
    }

    /**
     * 新增
     * @param vendoutRunning
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody VendoutRunningEntity vendoutRunning){
        return vendoutRunningService.save(vendoutRunning);
    }

    /**
     * 修改
     * @param id
     * @param vendoutRunning
     * @return 是否成功
     */
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id,@RequestBody VendoutRunningEntity vendoutRunning){
        vendoutRunning.setId( id );
        return vendoutRunningService.updateById(vendoutRunning);
    }

    /**
     * 删除
     * @param id
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public  boolean delete(@PathVariable Long id){
        return vendoutRunningService.removeById(id);
    }

    /**
     * 分页查询
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @param searchMap 条件
     * @return 分页结果
     */
    @GetMapping("/page/{pageIndex}/{pageSize}")
    public Pager<VendoutRunningEntity> findPage(@PathVariable long pageIndex, @PathVariable long pageSize, @RequestParam Map searchMap){
        return vendoutRunningService.findPage( pageIndex,pageSize,searchMap );
    }
}
