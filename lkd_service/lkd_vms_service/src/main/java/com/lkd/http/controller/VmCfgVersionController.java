package com.lkd.http.controller;
import com.lkd.entity.VmCfgVersionEntity;
import com.lkd.service.VmCfgVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vmCfgVersion")
public class VmCfgVersionController {

    @Autowired
    @Lazy
    private VmCfgVersionService vmCfgVersionService;

    /**
     * 根据versionId查询
     * @param versionId
     * @return 实体
     */
    @GetMapping("/{versionId}")
    public VmCfgVersionEntity findById(@PathVariable Long versionId){
        return vmCfgVersionService.getById( versionId );
    }

    /**
     * 新增
     * @param vmCfgVersion
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody VmCfgVersionEntity vmCfgVersion){
        return vmCfgVersionService.save( vmCfgVersion );
    }

    /**
     * 修改
     * @param versionId
     * @param vmCfgVersion
     * @return 是否成功
     */
    @PutMapping("/{versionId}")
    public boolean update(@PathVariable Long versionId,@RequestBody VmCfgVersionEntity vmCfgVersion){
        vmCfgVersion.setVersionId( versionId );

        return vmCfgVersionService.updateById( vmCfgVersion );
    }
}
