package com.lkd.business;


import com.lkd.annotations.ProcessType;
import com.lkd.common.VMSystem;
import com.lkd.contract.VmStatusContract;
import com.lkd.exception.LogicException;
import com.lkd.feignService.VMService;
import com.lkd.http.viewModel.TaskViewModel;
import com.lkd.service.TaskService;
import com.lkd.utils.JsonUtil;
import com.lkd.viewmodel.VendingMachineViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 状态协议处理
 */
@Component
@ProcessType("vmStatus")
@Slf4j
public class VMStatusHandler  implements MsgHandler{


    @Autowired
    private TaskService taskService;

    @Autowired
    private VMService vmService;

    @Override
    public void process(String jsonMsg) throws IOException {
        //解析报文
        VmStatusContract vmStatusContract = JsonUtil.getByJson(jsonMsg, VmStatusContract.class);
        if( vmStatusContract==null || vmStatusContract.getStatusInfo()==null || vmStatusContract.getStatusInfo().size()<=0  ) return;

        //判断报文中，状态如果有一个是false  创建维修工单
        if( vmStatusContract.getStatusInfo().stream().anyMatch( s-> s.isStatus()==false  ) ){
            try {
                //根据设备编号查询设备
                VendingMachineViewModel vmInfo = vmService.getVMInfo(vmStatusContract.getInnerCode());
                //获得同区域内工单最少的人
                Integer assignorId = taskService.getLeastUser(vmInfo.getRegionId().intValue(), true);

                TaskViewModel task=new TaskViewModel();
                task.setAssignorId(assignorId);//执行人id
                task.setDesc("自动维修工单");
                task.setInnerCode( vmStatusContract.getInnerCode() );//设备编号
                task.setProductType(VMSystem.TASK_TYPE_REPAIR);//维修工单
                task.setCreateType(0);//自动创建

                taskService.createTask(task);//创建工单
            } catch (Exception e) {
                e.printStackTrace();
                log.error("创建自动维修工单失败  msg is："+jsonMsg);
            }
        }
    }
}
