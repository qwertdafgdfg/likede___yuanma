package com.lkd.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lkd.common.VMSystem;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.feignService.TaskService;
import com.lkd.service.VendingMachineService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SupplyJob {

    @Autowired
    private VendingMachineService vmService;

    @Autowired
    private TaskService taskService;

    /**
     * 扫描所有运营状态的售货机
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("supplyJobHandler")
    public ReturnT<String> supplyJobHandler(String param) throws  Exception{

        //获取分片总数和当前分片索引
        ShardingUtil.ShardingVO shardingVo = ShardingUtil.getShardingVo();
        int numbers = shardingVo.getTotal();  //分片总数
        int index = shardingVo.getIndex(); //当前分片索引
        log.info("分片参数  当前分片索引{}   总分片数{}",index,numbers);
        XxlJobLogger.log( "分片参数  当前分片索引"+index+"  总分片数"+numbers);

        Integer percent = taskService.getSupplyAlertValue();
        //查询所有运营状态的售货机
        QueryWrapper<VendingMachineEntity> qw=new QueryWrapper<>();
        qw.lambda()
                .eq(VendingMachineEntity::getVmStatus, VMSystem.VM_STATUS_RUNNING)
                .apply("mod(id,"+numbers+" ) = "+index);  //对id取模
        List<VendingMachineEntity> vmList = vmService.list(qw);
        //扫描售货机
        vmList.forEach( vm->{
            XxlJobLogger.log("扫描售货机"+vm.getInnerCode());
            int count = vmService.inventory(percent, vm); //缺货货道数量
            if(count>0){
                XxlJobLogger.log("售货机"+vm.getInnerCode()+"缺货");
                //发送补货消息
                vmService.sendSupplyTask(vm);
            }
        });
        return ReturnT.SUCCESS;
    }


}
