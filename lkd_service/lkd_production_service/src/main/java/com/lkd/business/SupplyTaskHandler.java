package com.lkd.business;

import com.lkd.annotations.ProcessType;
import com.lkd.common.VMSystem;
import com.lkd.contract.SupplyCfg;
import com.lkd.exception.LogicException;
import com.lkd.feignService.VMService;
import com.lkd.http.viewModel.TaskDetailsViewModel;
import com.lkd.http.viewModel.TaskViewModel;
import com.lkd.service.TaskService;
import com.lkd.utils.JsonUtil;
import com.lkd.viewmodel.VendingMachineViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
@ProcessType("supplyTask")
@Slf4j
public class SupplyTaskHandler implements MsgHandler {

    @Autowired
    private VMService vmService;

    @Autowired
    @Lazy
    private TaskService taskService;

    @Override
    public void process(String jsonMsg) throws IOException {

        try {
            //1.解析协议内容
            SupplyCfg supplyCfg = JsonUtil.getByJson(jsonMsg, SupplyCfg.class);
            if(supplyCfg==null) return;

            //2.找出被指派人
            VendingMachineViewModel vm = vmService.getVMInfo(supplyCfg.getInnerCode());
            Integer userId = taskService.getLeastUser(vm.getRegionId().intValue(), false);
            //3.创建补货工单

            TaskViewModel taskViewModel=new TaskViewModel();
            taskViewModel.setAssignorId(userId);
            taskViewModel.setCreateType(0);//创建类型
            taskViewModel.setProductType(VMSystem.TASK_TYPE_SUPPLY);
            taskViewModel.setInnerCode(supplyCfg.getInnerCode());
            taskViewModel.setDesc("自动补货工单");

            taskViewModel.setDetails( supplyCfg.getSupplyData().stream().map(c->{
                TaskDetailsViewModel taskDetailsViewModel=new TaskDetailsViewModel();
                taskDetailsViewModel.setChannelCode( c.getChannelId() );
                taskDetailsViewModel.setExpectCapacity( c.getCapacity() );
                taskDetailsViewModel.setSkuId(c.getSkuId());
                taskDetailsViewModel.setSkuName(c.getSkuName());
                taskDetailsViewModel.setSkuImage(c.getSkuImage());
                return taskDetailsViewModel;
            } ).collect(Collectors.toList()) );  //补货详情

            taskService.createTask(taskViewModel);
        } catch (Exception e) {
            e.printStackTrace();
            log.error( "创建自动补货工单出错"+e.getMessage() );
        }
    }
}
