package com.lkd.http.controller;

import com.lkd.entity.PartnerEntity;
import com.lkd.feignService.VMService;
import com.lkd.http.viewModel.PartnerReq;
import com.lkd.http.viewModel.PartnerUpdatePwdReq;
import com.lkd.service.PartnerService;
import com.lkd.utils.BCrypt;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.PartnerViewModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {
    private final PartnerService partnerService;
    private final VMService vmService;

    /**
     * 新增
     * @param partnerReq
     * @return
     */
    @PostMapping
    public Boolean add(@RequestBody PartnerReq partnerReq){
        PartnerEntity partnerEntity = new PartnerEntity();
        BeanUtils.copyProperties(partnerReq,partnerEntity);
        partnerEntity.setPassword(BCrypt.hashpw("123456",BCrypt.gensalt()));

        return partnerService.save(partnerEntity);
    }

    /**
     * 获取合作商名称
     * @param id
     * @return
     */
    @GetMapping("/name")
    public String getPartnerName(@PathVariable Integer id){
        return partnerService.getById(id).getName();
    }

    /**
     * 更新
     * @param id
     * @param partnerReq
     * @return
     */
    @PutMapping("/{id}")
    public Boolean update(@PathVariable Integer id, @RequestBody PartnerReq partnerReq){
        return partnerService.modify(id,partnerReq);
    }

    /**
     * 删除合作商
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id){
        return partnerService.delete(id);
    }

    /**
     * 根据合作商Id获取详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public PartnerViewModel getById(@PathVariable Integer id){
        PartnerEntity partnerEntity = partnerService.getById(id);
        if(partnerEntity == null)
            return null;

        PartnerViewModel vo = new PartnerViewModel();
        BeanUtils.copyProperties(partnerEntity,vo);
        vo.setVmCount(vmService.getVmCountByOwnerId(id));

        return vo;
    }

    /**
     * 查询合作商
     * @param pageIndex
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/search")
    public Pager<PartnerEntity> search(
            @RequestParam(value = "pageIndex",required = false,defaultValue = "1") Long pageIndex,
            @RequestParam(value = "pageSize",required = false,defaultValue = "1") Long pageSize,
            @RequestParam(value = "name",required = false,defaultValue = "") String name){
        return partnerService.search(pageIndex,pageSize,name);
    }

    /**
     * 重置密码
     * @param id
     */
    @PutMapping("/resetPwd/{id}")
    public void resetPwd(@PathVariable Integer id){
        partnerService.resetPwd(id);
    }

    /**
     * 更新密码
     * @param req
     * @return
     */
    @PutMapping("/updatePwd/{id}")
    public Boolean modifyPwd(@PathVariable Integer id,@RequestBody PartnerUpdatePwdReq req){
        return partnerService.updatePwd(id,req);
    }
}
