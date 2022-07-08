package com.lkd.http.controller;
import com.lkd.feignService.VMService;
import com.lkd.viewmodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vm")
public class VMController {

    @Autowired
    private VMService vmService;


    /**
     * 获取售货机商品列表
     * @param innerCode
     * @return
     */
    @GetMapping("/skuList/{innerCode}")
    public List<SkuViewModel> getSkuListByInnercode(@PathVariable String innerCode){
        return vmService.getAllSkuByInnerCode(innerCode);
    }

    /**
     * 扫码获取商品详情，用来后续支付
     * @param innerCode
     * @param skuId
     * @return
     */
    @Deprecated
    @GetMapping("/sku/{innerCode}/{skuId}")
    public SkuInfoViewModel getSku(@PathVariable String innerCode, @PathVariable String skuId){
        SkuViewModel skuViewModel = vmService.getSku(innerCode,skuId);
        SkuInfoViewModel skuInfo = new SkuInfoViewModel();
        BeanUtils.copyProperties(skuViewModel,skuInfo);
        VendingMachineViewModel vmInfo =vmService.getVMInfo(innerCode);
        if(vmInfo != null){
            skuInfo.setAddr(vmInfo.getNodeAddr());
            skuInfo.setInnerCode(innerCode);
        }
        return skuInfo;
    }


    /**
     * 根据售货机编号查询设备
     * @param innerCode
     * @return
     */
    @GetMapping("/innerCode/{innerCode}")
    public VendingMachineViewModel getVm(@PathVariable String innerCode){
        return vmService.getVMInfo(innerCode);
    }


    /**
     * 搜索附近售货机
     * @param vmSearch
     * @return
     */
    @PostMapping("/search")
    public List<VmInfoDTO> search( @RequestBody VmSearch vmSearch ){
        return vmService.search(vmSearch);
    }

}
