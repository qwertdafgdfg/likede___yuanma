package com.lkd.business.msgHandler;

import com.google.common.base.Strings;
import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.common.VMSystem;
import com.lkd.contract.TaskCompleteContract;
import com.lkd.service.VendingMachineService;
import com.lkd.utils.JsonUtil;
import com.lkd.viewmodel.VMDistance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 工单完成处理
 */
@Component
@ProcessType("taskCompleted")
public class TaskCompletedMsgHandler implements MsgHandler {

    @Autowired
    private VendingMachineService vmService;//售货机服务


    @Override
    public void process(String jsonMsg) throws IOException {
        TaskCompleteContract taskCompleteContract= JsonUtil.getByJson(jsonMsg, TaskCompleteContract.class );
        if(taskCompleteContract==null || Strings.isNullOrEmpty(taskCompleteContract.getInnerCode())  ) return;

        //如果是投放工单，将售货机修改为运营状态
        if( taskCompleteContract.getTaskType()== VMSystem.TASK_TYPE_DEPLOY){
            vmService.updateStatus(  taskCompleteContract.getInnerCode(), VMSystem.VM_STATUS_RUNNING   );
            //todo :保存设备的坐标（数据库+es）
            var vmDistance=new VMDistance();
            BeanUtils.copyProperties( taskCompleteContract,vmDistance );
            vmService.setVMDistance(vmDistance );
        }

        //如果是撤机工单，将售货机修改为撤机状态
        if( taskCompleteContract.getTaskType()== VMSystem.TASK_TYPE_REVOKE){
            vmService.updateStatus(  taskCompleteContract.getInnerCode(), VMSystem.VM_STATUS_REVOKE  );
        }
    }



}
