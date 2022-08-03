package com.lkd.http.controller;

import com.lkd.entity.SkuClassEntity;
import com.lkd.service.SkuClassService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skuClass")
public class SkuClassController {

    @Autowired
    @Lazy
    private SkuClassService skuClassService;

    /**
     * 获取所有商品类别
     * @return
     */
    @GetMapping("/all")
    public List<SkuClassEntity> getAll(){
        return skuClassService.list();
    }

    /**
     * 类型搜索
     * @param pageIndex
     * @param pageSize
     * @param skuName
     * @return
     */
    @GetMapping("/search")
    public Pager<SkuClassEntity> search(
            @RequestParam(value = "pageIndex",required = false,defaultValue = "1") long pageIndex,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") long pageSize,
            @RequestParam(value = "className",required = false,defaultValue = "") String skuName){
        return skuClassService.findPage(pageIndex,pageSize,skuName);
    }

    /**
     * 根据classId查询
     * @param classId
     * @return 实体
     */
    @GetMapping("/{classId}")
    public SkuClassEntity findById(@PathVariable Integer classId){
        return skuClassService.getById( classId );
    }

    /**
     * 修改
     * @param classId
     * @param skuClass
     * @return 是否成功
     */
    @PutMapping("/{classId}")
    public boolean update(@PathVariable Integer classId,@RequestBody SkuClassEntity skuClass){
        skuClass.setClassId( classId );
        return skuClassService.updateById(skuClass);
    }

    @PostMapping
    public boolean add(@RequestBody SkuClassEntity skuClass){
        return skuClassService.save(skuClass);
    }
}
