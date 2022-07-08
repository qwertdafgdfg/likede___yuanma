package com.lkd.http.controller;
import com.lkd.http.viewModel.VmStatusVM;
import com.lkd.service.VmStatusInfoService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/status")
public class VmStatusInfoController {

    @Autowired
    private VmStatusInfoService vmStatusInfoService;

    /**
     * 获取所有设备状态
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/all/{pageIndex}/{pageSize}")
    public Pager<VmStatusVM> getAll(@PathVariable("pageIndex") long pageIndex,
                                    @PathVariable("pageSize") long pageSize){
        return vmStatusInfoService.getAll(pageIndex,pageSize);
    }

    /**
     * 获取所有故障状态设备
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping("/trouble/{pageIndex}/{pageSize}")
    public Pager<VmStatusVM> getAllTrouble(@PathVariable("pageIndex") long pageIndex,
                                           @PathVariable("pageSize") long pageSize){
        return vmStatusInfoService.getAllTrouble(pageIndex,pageSize);
    }

    /**
     * 根据设备编号获取设备状态详情
     * @param innerCode
     * @return
     */
    @GetMapping("/vm/{innerCode}")
    public VmStatusVM getByInnerCode(@PathVariable("innerCode") String innerCode){
        return vmStatusInfoService.getVMStatus(innerCode);
    }

    /**
     * 获取设备状态是否正常
     * @param innerCode
     * @return true正常；fasle异常
     */
    @GetMapping("/vmStatus/{innerCode}")
    public Boolean getVMStatus(@PathVariable("innerCode") String innerCode){
        var status = vmStatusInfoService.getVMStatus(innerCode);

        if(status == null || status.getStatuses() == null ||status.getStatuses().size() <= 0) return false;

        return status.getStatuses().stream().anyMatch(s->s.isStatus() == false)?false:true;
    }
}
