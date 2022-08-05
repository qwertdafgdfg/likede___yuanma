package com.lkd.business.msgHandler;

import com.google.common.base.Strings;
import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.common.VMSystem;
import com.lkd.contract.TaskCompleteContract;
import com.lkd.service.VendingMachineService;
import com.lkd.utils.JsonUtil;
import com.lkd.viewmodel.VMDistance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 工单完成处理
 */
@Component
@ProcessType("taskCompleted")
@Slf4j
public class TaskCompletedMsgHandler implements MsgHandler {

    @Autowired
    @Lazy
    private VendingMachineService vmService;//售货机服务


    @Override
    public void process(String jsonMsg) throws IOException {
        System.out.println("");
        //{"innerCode":"01000002","msgType":"taskCompleted",  "taskType":4}
        log.info("--------------因为我这个服务订阅了某个topis接受到的消息为：---------------"+jsonMsg);
        TaskCompleteContract taskCompleteContract= JsonUtil.getByJson(jsonMsg, TaskCompleteContract.class );
        if(taskCompleteContract==null || Strings.isNullOrEmpty(taskCompleteContract.getInnerCode())  ) return;

        //如果是投放工单，将售货机修改为运营状态
        if( taskCompleteContract.getTaskType()== VMSystem.TASK_TYPE_DEPLOY){
            vmService.updateStatus(  taskCompleteContract.getInnerCode(), VMSystem.VM_STATUS_RUNNING   );
            //todo :保存设备的坐标（数据库+es）
            var vmDistance=new VMDistance();
            log.info(taskCompleteContract+"-----"+vmDistance);
            BeanUtils.copyProperties( taskCompleteContract,vmDistance );
            vmService.setVMDistance(vmDistance );
        }

        //如果是撤机工单，将售货机修改为撤机状态
        if( taskCompleteContract.getTaskType()== VMSystem.TASK_TYPE_REVOKE){
            vmService.updateStatus(  taskCompleteContract.getInnerCode(), VMSystem.VM_STATUS_REVOKE  );
        }
    }



}
