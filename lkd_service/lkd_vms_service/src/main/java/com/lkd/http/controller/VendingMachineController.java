package com.lkd.http.controller;

import com.lkd.entity.SkuEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.entity.VmTypeEntity;
import com.lkd.http.viewModel.CreateVMReq;
import com.lkd.http.viewModel.PolicyReq;
import com.lkd.viewmodel.*;
import com.lkd.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vm")
public class VendingMachineController {

    @Autowired
    private VendingMachineService vendingMachineService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private VmTypeService vmTypeService;

    @Autowired
    private RegionService regionService;
    @Autowired
    private BusinessTypeService businessTypeService;


    /**
     * 获取售货机信息
     * @param innerCode
     * @return
     */
    @GetMapping("/vmInfo/{innerCode}")
    public VendingMachineEntity getByInnerCode(@PathVariable("innerCode") String innerCode){
        return vendingMachineService.findByInnerCode(innerCode);
    }

    /**
     * 根据id查询
     * @param id
     * @return 实体
     */
    @GetMapping("/{id}")
    public VendingMachineEntity findById(@PathVariable Long id){
        return vendingMachineService.getById(id);
    }

    /**
     * 新增
     * @param req
     * @return 是否成功
     */
    @PostMapping
    public boolean add(@RequestBody CreateVMReq req){
        return vendingMachineService.add( req );
    }

    /**
     * 修改点位
     * @param id
     * @param nodeId
     * @return 是否成功
     */
    @PutMapping("/{id}/{nodeId}")
    public boolean update(@PathVariable String id,@PathVariable String nodeId){
        return vendingMachineService.update(Long.valueOf(id),Long.valueOf(nodeId));
    }

    /**
     * 获取售货机商品列表
     * @param innerCode
     * @return
     */
    @GetMapping("/skuList/{innerCode}")
    public List<SkuViewModel> getAllSkuByInnerCode(@PathVariable String innerCode){
        return vendingMachineService.getSkuList(innerCode);
    }

    /**
     * 获取设备里的商品详情
     * @param innerCode
     * @param skuId
     * @return
     */
    @GetMapping("/sku/{innerCode}/{skuId}")
    @ResponseBody
    public SkuViewModel getSkuInfo(@PathVariable String innerCode, @PathVariable String skuId){
        SkuViewModel skuViewModel = new SkuViewModel();
        SkuEntity skuEntity = vendingMachineService.getSku(innerCode,Long.valueOf(skuId));
        if(skuEntity == null) return null;
        skuViewModel.setSkuName(skuEntity.getSkuName());
        skuViewModel.setSkuId(skuEntity.getSkuId());
        skuViewModel.setRealPrice(channelService.getRealPrice(innerCode,Long.valueOf(skuId)));
        skuViewModel.setPrice(skuEntity.getPrice());
        skuViewModel.setUnit(skuEntity.getUnit());
        skuViewModel.setImage(skuEntity.getSkuImage());
        skuViewModel.setDiscount(skuEntity.isDiscount());
        skuViewModel.setCapacity(skuEntity.getCapacity());

        return skuViewModel;
    }

    /**
     * 根据机器编号获取设备详情
     * @param innerCode
     * @return
     */
    @GetMapping("/info/{innerCode}")
    public VendingMachineViewModel getVMInfo(@PathVariable String innerCode){
        VendingMachineEntity entity = vendingMachineService.findByInnerCode(innerCode);

        VendingMachineViewModel viewModel = new VendingMachineViewModel();
        viewModel.setAreaId(entity.getAreaId());
        viewModel.setInnerCode(entity.getInnerCode());
        viewModel.setStatus(entity.getVmStatus());
        viewModel.setVmId(entity.getId());
        viewModel.setOwnerId(entity.getOwnerId());
        viewModel.setNodeId(entity.getNodeId());
        viewModel.setNodeAddr(entity.getNode().getAddr());
        viewModel.setNodeName(entity.getNode().getName());
        viewModel.setRegionId(entity.getRegionId());
        viewModel.setVmStatus(entity.getVmStatus());
        viewModel.setRegionName(regionService.getById(entity.getRegionId()).getName());
        viewModel.setBusinessName(businessTypeService.getById(entity.getBusinessId()).getName());
        viewModel.setBusinessId(entity.getBusinessId());
        viewModel.setVmTypeName(entity.getType().getName());

        return viewModel;
    }

    /**
     * 给设备应用策略
     * @param policyReq
     * @return
     */
    @PutMapping("/applyPolicy")
    public boolean applyPolicy(@RequestBody PolicyReq policyReq){
        return policyService.applyPolicy(policyReq.getInnerCodeList(),policyReq.getPolicyId());
    }

    /**
     * 取消设备上的策略
     * @param innerCode
     * @param policyId
     * @return
     */
    @PutMapping("/cancelPolicy/{innerCode}/{policyId}")
    public boolean cancelPolicy(@PathVariable String innerCode, @PathVariable int policyId){
        return policyService.cancelPolicy(innerCode,policyId);
    }

    /**
     * 更新机器所在点位
     * @param innerCode
     * @param nodeId
     * @return
     */
    @GetMapping("/update/{innerCode}/{nodeId}")
    public boolean updateNode(@PathVariable String innerCode, @PathVariable long nodeId){
        VendingMachineEntity vendingMachineEntity = vendingMachineService.findByInnerCode(innerCode);
        vendingMachineEntity.setNodeId(nodeId);

        return vendingMachineService.updateById(vendingMachineEntity);
    }

    @GetMapping("/allTypes")
    public List<VmTypeEntity> getAllType(){
        return vmTypeService.list();
    }

    /**
     * 获取在运行的机器列表
     * @param isRunning
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/allByStatus/{isRunning}/{pageIndex}/{pageSize}")
    public Pager<String> getAllByStatus(@PathVariable boolean isRunning,
                                        @PathVariable long pageIndex,
                                        @PathVariable long pageSize){
        return vendingMachineService.getAllInnerCodes(isRunning,pageIndex,pageSize);
    }

    /**
     * 查询设备
     * @param pageIndex
     * @param pageSize
     * @param status
     * @return
     */
    @GetMapping("/search")
    public Pager<VendingMachineEntity> query(
            @RequestParam(value = "pageIndex",required = false,defaultValue = "1") Long pageIndex,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") long pageSize,
            @RequestParam(value = "status",defaultValue = "",required = false) Integer status,
            @RequestParam(value = "innerCode",defaultValue = "",required = false) String innerCode){
        return vendingMachineService.query(pageIndex,pageSize,status,innerCode);
    }

    /**
     * 获取代理商下所有设备数量
     * @param ownerId
     * @return
     */
    @GetMapping("/countByOwner/{ownerId}")
    public Integer getCountByOwner(@PathVariable Integer ownerId){
        return vendingMachineService.getCountByOwnerId(ownerId);
    }



//    /**
//     * 盘点库存
//     * @param percent 百分比
//     */
//    @GetMapping("/inventory/{percent}")
//    public void inventory(@PathVariable int percent){
//        vendingMachineService.inventory(percent);
//    }


    /**
     * 售货机商品是否还有库存
     * @param innerCode
     * @param skuId
     * @return
     */
    @GetMapping("/hasCapacity/{innerCode}/{skuId}")
    public Boolean hasCapacity(@PathVariable String innerCode,@PathVariable Long skuId){
        return vendingMachineService.hasCapacity(innerCode,skuId);
    }


    /**
     * 搜索附近售货机
     * @param vmSearch
     * @return
     */
    @PostMapping("/search")
    public List<VmInfoDTO> search(@RequestBody VmSearch vmSearch ){
        return vendingMachineService.search(vmSearch);
    }



}
