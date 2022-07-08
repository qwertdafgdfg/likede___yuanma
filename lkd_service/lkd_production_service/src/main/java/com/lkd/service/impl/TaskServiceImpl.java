package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lkd.common.VMSystem;
import com.lkd.config.TopicConfig;
import com.lkd.contract.SupplyCfg;
import com.lkd.contract.SupplyChannel;
import com.lkd.contract.TaskCompleteContract;
import com.lkd.dao.TaskDao;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.TaskDetailsEntity;
import com.lkd.entity.TaskEntity;
import com.lkd.entity.TaskStatusTypeEntity;
import com.lkd.exception.LogicException;
import com.lkd.feignService.UserService;
import com.lkd.feignService.VMService;
import com.lkd.http.viewModel.CancelTaskViewModel;
import com.lkd.http.viewModel.TaskReportInfo;
import com.lkd.http.viewModel.TaskViewModel;
import com.lkd.service.TaskDetailsService;
import com.lkd.service.TaskService;
import com.lkd.service.TaskStatusTypeService;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.UserViewModel;
import com.lkd.viewmodel.UserWork;
import com.lkd.viewmodel.VendingMachineViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskDao,TaskEntity> implements TaskService{
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private TaskDetailsService taskDetailsService;

    @Autowired
    private VMService vmService;

    @Autowired
    private TaskStatusTypeService statusTypeService;


    @Autowired
    private MqttProducer mqttProducer;

    @Autowired
    private UserService userService;


    @Override
    @Transactional(rollbackFor = {Exception.class},noRollbackFor = {LogicException.class})
    public boolean createTask(TaskViewModel taskViewModel) throws LogicException {
        checkCreateTask(taskViewModel.getInnerCode(),taskViewModel.getProductType());//验证
        if(hasTask(taskViewModel.getInnerCode(),taskViewModel.getProductType())) {
            throw new LogicException("该机器有未完成的同类型工单");
        }
        //新增工单表记录
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskCode(generateTaskCode());//工单编号
        BeanUtils.copyProperties(taskViewModel,taskEntity);//复制属性
        taskEntity.setTaskStatus(VMSystem.TASK_STATUS_CREATE);
        taskEntity.setProductTypeId(taskViewModel.getProductType());

        VendingMachineViewModel vm = vmService.getVMInfo(taskViewModel.getInnerCode());
        taskEntity.setAddr(vm.getNodeAddr());
        taskEntity.setRegionId(  vm.getRegionId() );

        UserViewModel user = userService.getUser(taskViewModel.getAssignorId());
        taskEntity.setUserName( user.getUserName()  );//存储工单执行人名称

        this.save(taskEntity);

        //如果是补货工单，向 工单明细表插入记录
        if(taskEntity.getProductTypeId() == VMSystem.TASK_TYPE_SUPPLY){
            taskViewModel.getDetails().forEach(d->{
                TaskDetailsEntity detailsEntity = new TaskDetailsEntity();
                BeanUtils.copyProperties( d,detailsEntity );
                detailsEntity.setTaskId(taskEntity.getTaskId());
                taskDetailsService.save(detailsEntity);
            });
        }

        //工单量分值+1
        updateTaskZSet(taskEntity,1);
        return true;
    }

    @Override
    public boolean accept(Long id) {
        TaskEntity task = this.getById(id);  //查询工单
        if(task.getTaskStatus()!= VMSystem.TASK_STATUS_CREATE ){
            throw new LogicException("工单状态不是待处理");
        }
        task.setTaskStatus( VMSystem.TASK_STATUS_PROGRESS );//修改工单状态为进行
        return this.updateById(task);
    }

    @Override
    public boolean cancelTask(Long id, CancelTaskViewModel cancelVM) {
        TaskEntity task = this.getById(id);  //查询工单
        if(task.getTaskStatus()== VMSystem.TASK_STATUS_FINISH  || task.getTaskStatus()== VMSystem.TASK_STATUS_CANCEL ){
            throw new LogicException("工单已经结束");
        }
        task.setTaskStatus( VMSystem.TASK_STATUS_CANCEL  );
        task.setDesc(cancelVM.getDesc());
        //工单量分值-1
        updateTaskZSet(task,-1);
        return this.updateById(task);
    }


    @Override
    @Transactional
    public boolean completeTask(long id) {
        return  completeTask(id,0d,0d,"");
    }

    @Override
    public boolean completeTask(long id, Double lat, Double lon, String addr) {
        TaskEntity taskEntity = this.getById(id);
        if(taskEntity.getTaskStatus()== VMSystem.TASK_STATUS_FINISH  || taskEntity.getTaskStatus()== VMSystem.TASK_STATUS_CANCEL ){
            throw new LogicException("工单已经结束");
        }
        taskEntity.setTaskStatus(VMSystem.TASK_STATUS_FINISH);
        taskEntity.setAddr(addr);
        this.updateById(taskEntity);

        //如果是补货工单
        if(taskEntity.getProductTypeId()==VMSystem.TASK_TYPE_SUPPLY){
            noticeVMServiceSupply(taskEntity);
        }

        //如果是投放工单或撤机工单
        if(taskEntity.getProductTypeId()==VMSystem.TASK_TYPE_DEPLOY
                || taskEntity.getProductTypeId()==VMSystem.TASK_TYPE_REVOKE){
            noticeVMServiceStatus(taskEntity,lat,lon);
        }

        return true;
    }


    @Override
    public List<TaskStatusTypeEntity> getAllStatus() {
        QueryWrapper<TaskStatusTypeEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .ge(TaskStatusTypeEntity::getStatusId,VMSystem.TASK_STATUS_CREATE);

        return statusTypeService.list(qw);
    }

    @Override
    public Pager<TaskEntity> search(Long pageIndex, Long pageSize, String innerCode, Integer userId, String taskCode,Integer status,Boolean isRepair,String start,String end) {
        Page<TaskEntity> page = new Page<>(pageIndex,pageSize);
        LambdaQueryWrapper<TaskEntity> qw = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(innerCode)){
            qw.eq(TaskEntity::getInnerCode,innerCode);
        }
        if(userId != null && userId > 0){
            qw.eq(TaskEntity::getAssignorId,userId);
        }
        if(!Strings.isNullOrEmpty(taskCode)){
            qw.like(TaskEntity::getTaskCode,taskCode);
        }
        if(status != null && status > 0){
            qw.eq(TaskEntity::getTaskStatus,status);
        }
        if(isRepair != null){
            if(isRepair){
                qw.ne(TaskEntity::getProductTypeId,VMSystem.TASK_TYPE_SUPPLY);
            }else {
                qw.eq(TaskEntity::getProductTypeId,VMSystem.TASK_TYPE_SUPPLY);
            }
        }
        if(!Strings.isNullOrEmpty(start) && !Strings.isNullOrEmpty(end)){
            qw
                    .ge(TaskEntity::getCreateTime,LocalDate.parse(start,DateTimeFormatter.ISO_LOCAL_DATE))
                    .le(TaskEntity::getCreateTime,LocalDate.parse(end,DateTimeFormatter.ISO_LOCAL_DATE));
        }
        //根据最后更新时间倒序排序
        qw.orderByDesc(TaskEntity::getUpdateTime);

        return Pager.build(this.page(page,qw));
    }

    @Override
    public Integer getLeastUser(Integer regionId, Boolean isRepair) {

        String roleCode="1002";
        if(isRepair){ //如果是维修工单
            roleCode="1003";
        }

        String key= VMSystem.REGION_TASK_KEY_PREF
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                +"."+ regionId+"."+roleCode;

        Set<Object> set = redisTemplate.opsForZSet().range(key, 0, 1);
        if(set==null || set.isEmpty()){
            throw  new LogicException("该区域暂时没有相关人员");
        }
        return (Integer) set.stream().collect( Collectors.toList()).get(0);
    }

    @Override
    public List<TaskReportInfo> getTaskReportInfo(LocalDateTime start, LocalDateTime end) {
        //运营工单总数
        var supplyTotalFuture= CompletableFuture.supplyAsync( ()-> this.taskCount(start,end,false,null ));
        //运维工单总数
        var repairTotalFuture= CompletableFuture.supplyAsync( ()-> this.taskCount(start,end,true,null ));
        //完成的运营工单数
        var completedSupplyFuture= CompletableFuture.supplyAsync( ()-> this.taskCount(start,end,false,VMSystem.TASK_STATUS_FINISH ));
        //完成的运维工单数
        var completedRepairFuture= CompletableFuture.supplyAsync( ()-> this.taskCount(start,end,true,VMSystem.TASK_STATUS_FINISH ));
        //拒绝的运营工单数
        var cancelSupplyFuture= CompletableFuture.supplyAsync( ()-> this.taskCount(start,end,false,VMSystem.TASK_STATUS_CANCEL ));
        //拒绝的运维工单数
        var cancelRepairFuture= CompletableFuture.supplyAsync( ()-> this.taskCount(start,end,true,VMSystem.TASK_STATUS_CANCEL ));
        //运营人员数量
        var operatorCountFuture= CompletableFuture.supplyAsync( ()-> userService.getOperatorCount() );
        //运营人员数量
        var repairerCountFuture= CompletableFuture.supplyAsync( ()-> userService.getRepairerCount() );

        //并行处理
        CompletableFuture.allOf(supplyTotalFuture,
                repairTotalFuture,
                completedSupplyFuture,
                completedRepairFuture,
                cancelSupplyFuture,
                cancelRepairFuture,
                operatorCountFuture,
                repairerCountFuture).join();

        List<TaskReportInfo> result=Lists.newArrayList();
        var supplyTaskInfo=new TaskReportInfo();//运营
        var repairTaskInfo=new TaskReportInfo();//运维

        try {
            //运营
            supplyTaskInfo.setTotal(supplyTotalFuture.get() );
            supplyTaskInfo.setCancelTotal(cancelSupplyFuture.get() );
            supplyTaskInfo.setCompletedTotal( completedSupplyFuture.get() );
            supplyTaskInfo.setRepair(false);
            supplyTaskInfo.setWorkerCount( operatorCountFuture.get() );
            result.add(supplyTaskInfo);

            //运维
            repairTaskInfo.setTotal(repairTotalFuture.get());
            repairTaskInfo.setCancelTotal( cancelRepairFuture.get() );
            repairTaskInfo.setCompletedTotal( completedRepairFuture.get() );
            repairTaskInfo.setRepair(true);
            repairTaskInfo.setWorkerCount( repairerCountFuture.get() );

            result.add(repairTaskInfo);

        } catch (Exception e) {
            e.printStackTrace();
            log.error( "构建工单统计数据失败",e );
        }

        return result;
    }

    @Override
    public UserWork getUserWork(Integer userId, LocalDateTime start, LocalDateTime end) {

        var userWork=new UserWork();
        userWork.setUserId(userId);

        //获取用户完成工单数
        var workCountFuture= CompletableFuture.supplyAsync( ()-> this.getCountByUserId(userId,VMSystem.TASK_STATUS_FINISH,start,end))
                .whenComplete( (r,e)->{
                    if(e!=null){
                        userWork.setWorkCount(0);
                        log.error("user work error",e);
                    }else{
                        userWork.setWorkCount(r);
                    }
                });
        //获取用户总工单数
        var totalFuture= CompletableFuture.supplyAsync( ()-> this.getCountByUserId(userId,null,start,end))
                .whenComplete( (r,e)->{
                    if(e!=null){
                        userWork.setTotal(0);
                        log.error("user work error",e);
                    }else{
                        userWork.setTotal(r);
                    }
                });
        //获取用户拒绝工单数
        var cancelCountFuture= CompletableFuture.supplyAsync( ()-> this.getCountByUserId(userId,VMSystem.TASK_STATUS_CANCEL,start,end))
                .whenComplete( (r,e)->{
                    if(e!=null){
                        userWork.setCancelCount(0);
                        log.error("user work error",e);
                    }else{
                        userWork.setCancelCount(r);
                    }
                });
        //获取用户进行中工单数
        var progressTotalFuture= CompletableFuture.supplyAsync( ()-> this.getCountByUserId(userId,VMSystem.TASK_STATUS_PROGRESS,start,end))
                .whenComplete( (r,e)->{
                    if(e!=null){
                        userWork.setProgressTotal(0);
                        log.error("user work error",e);
                    }else{
                        userWork.setProgressTotal(r);
                    }
                });

        //并发执行
        CompletableFuture.allOf(workCountFuture, totalFuture,cancelCountFuture, progressTotalFuture).join();

        return userWork;
    }

    @Override
    public List<UserWork> getUserWorkTop10(LocalDate start, LocalDate end, Boolean isRepair, Long regionId) {

        var qw=new  QueryWrapper<TaskEntity>();

        qw.select("count(user_id) as user_id ,user_name"  )
                .lambda()
                .ge( TaskEntity::getUpdateTime ,start  )
                .le( TaskEntity::getUpdateTime,end)
                .eq( TaskEntity::getTaskStatus ,VMSystem.TASK_STATUS_FINISH )
                .groupBy( TaskEntity::getUserName )
                .orderByDesc( TaskEntity::getUserId )//按工单数倒序排序
                .last("limit 10" );
        if(regionId!=null && regionId>0){
            qw.lambda().eq(TaskEntity::getRegionId,regionId) ;
        }
        if(isRepair){
            qw.lambda().ne( TaskEntity::getProductTypeId,VMSystem.TASK_TYPE_SUPPLY  );
        }else{
            qw.lambda().eq( TaskEntity::getProductTypeId,VMSystem.TASK_TYPE_SUPPLY  );
        }
        var result= this.list(qw)
                .stream().map( t->{
                    var userWork=new UserWork();
                    userWork.setUserName(t.getUserName());
                    userWork.setWorkCount(t.getUserId());
                    return userWork;

        } ).collect( Collectors.toList() );

        return result;
    }


    /**
     * 根据用户id、工单状态查询工单数
     * @param userId
     * @param taskStatus
     * @param start
     * @param end
     * @return
     */
    private Integer getCountByUserId(Integer userId,Integer taskStatus,LocalDateTime start, LocalDateTime end){

        var qw=new LambdaQueryWrapper<TaskEntity>();
        qw.ge( TaskEntity::getUpdateTime,start )
            .le(TaskEntity::getUpdateTime,end );

        if(taskStatus!=null){
            qw.eq(TaskEntity::getTaskStatus ,taskStatus);//状态
        }
        if(userId!=null){
            qw.eq(TaskEntity::getAssignorId,userId );
        }
        return this.count(qw);
    }



    /**
     * 统计工单数量
     * @param start
     * @param end
     * @param repair
     * @param taskStatus
     * @return
     */
    private int taskCount( LocalDateTime start,LocalDateTime end, Boolean repair, Integer taskStatus){

        LambdaQueryWrapper<TaskEntity> qw=new LambdaQueryWrapper<>();
        qw.ge( TaskEntity::getUpdateTime, start )
                .le( TaskEntity::getUpdateTime ,end);//时间段
        if(taskStatus!=null){
            qw.eq( TaskEntity::getTaskStatus ,taskStatus ) ;//工单状态
        }
        if(repair ){//如果是运维
            qw.ne( TaskEntity::getProductTypeId ,VMSystem.TASK_TYPE_SUPPLY  );
        }else {
            qw.eq( TaskEntity::getProductTypeId ,VMSystem.TASK_TYPE_SUPPLY  );
        }
        return this.count(qw);

    }



    /**
     * 同一台设备下是否存在未完成的工单
     * @param innerCode
     * @param productionType
     * @return
     */
    private boolean hasTask(String innerCode,int productionType){
        QueryWrapper<TaskEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .select(TaskEntity::getTaskId)
                .eq(TaskEntity::getInnerCode,innerCode)
                .eq(TaskEntity::getProductTypeId,productionType)
                .le(TaskEntity::getTaskStatus,VMSystem.TASK_STATUS_PROGRESS);

        return this.count(qw) > 0;
    }

    /**
     * 运维工单封装与下发
     * @param taskEntity
     */
    private void noticeVMServiceStatus(TaskEntity taskEntity,Double lat,Double lon){
        //向消息队列发送消息，通知售货机更改状态
        //封装协议
        TaskCompleteContract taskCompleteContract=new TaskCompleteContract();
        taskCompleteContract.setInnerCode(taskEntity.getInnerCode());//售货机编号
        taskCompleteContract.setTaskType( taskEntity.getProductTypeId() );//工单类型
        taskCompleteContract.setLat(lat);//纬度
        taskCompleteContract.setLon(lon);//经度
        //发送到emq
        try {
            mqttProducer.send( TopicConfig.COMPLETED_TASK_TOPIC,2, taskCompleteContract );
        } catch (Exception e) {
            log.error("发送工单完成协议出错");
            throw new LogicException("发送工单完成协议出错");
        }
    }


    /**
     * 补货协议封装与下发
     * @param taskEntity
     */
    private void noticeVMServiceSupply(TaskEntity taskEntity){

        //协议内容封装
        //1.根据工单id查询工单明细表
        QueryWrapper<TaskDetailsEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(TaskDetailsEntity::getTaskId,taskEntity.getTaskId());
        List<TaskDetailsEntity> details = taskDetailsService.list(qw);
        //2.构建协议内容
        SupplyCfg supplyCfg = new SupplyCfg();
        supplyCfg.setInnerCode(taskEntity.getInnerCode());//售货机编号
        List<SupplyChannel> supplyChannels = Lists.newArrayList();//补货数据
        //从工单明细表提取数据加到补货数据中
        details.forEach(d->{
            SupplyChannel channel = new SupplyChannel();
            channel.setChannelId(d.getChannelCode());
            channel.setCapacity(d.getExpectCapacity());
            supplyChannels.add(channel);
        });
        supplyCfg.setSupplyData(supplyChannels);

        //2.下发补货协议
        //发送到emq
        try {
            mqttProducer.send( TopicConfig.COMPLETED_TASK_TOPIC,2, supplyCfg );
        } catch (Exception e) {
            log.error("发送工单完成协议出错");
            throw new LogicException("发送工单完成协议出错");
        }

    }

    /**
     * 创建工单校验
     * @param innerCode 设备编号
     * @param productType
     * @throws LogicException
     */
    private void checkCreateTask(String innerCode,int productType) throws LogicException {
        VendingMachineViewModel vmInfo = vmService.getVMInfo(innerCode);//根据设备编号查询设备
        if(vmInfo == null) throw new LogicException("设备校验失败");
        //如果是投放工单，状态为运营
        if(productType == VMSystem.TASK_TYPE_DEPLOY  && vmInfo.getVmStatus() == VMSystem.VM_STATUS_RUNNING){
            throw new LogicException("该设备已在运营");
        }

        //如果是补货工单，状态不是运营状态
        if(productType == VMSystem.TASK_TYPE_SUPPLY  && vmInfo.getVmStatus() != VMSystem.VM_STATUS_RUNNING){
            throw new LogicException("该设备不在运营状态");
        }

        //如果是撤机工单，状态不是运营状态
        if(productType == VMSystem.TASK_TYPE_REVOKE  && vmInfo.getVmStatus() != VMSystem.VM_STATUS_RUNNING){
            throw new LogicException("该设备不在运营状态");
        }
    }


    /**
     * 生成工单编号
     * @return
     */
    private String generateTaskCode(){
        //日期+序号
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));  //日期字符串
        String key= "lkd.task.code."+date; //redis key
        Object obj = redisTemplate.opsForValue().get(key);
        if(obj==null){
            redisTemplate.opsForValue().set(key,1L, Duration.ofDays(1) );
            return date+"0001";
        }
        return date+  Strings.padStart( redisTemplate.opsForValue().increment(key,1).toString(),4,'0');
    }


    /**
     * 更新工单统计量列表
     * @param taskEntity
     * @param score
     */
    private void updateTaskZSet(TaskEntity taskEntity,int score){
        String roleCode="1003";
        if(taskEntity.getProductTypeId().intValue()==2 ){ //运营工单
            roleCode="1002";
        }
        String key= VMSystem.REGION_TASK_KEY_PREF
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                +"."+ taskEntity.getRegionId()+"."+roleCode;
        redisTemplate.opsForZSet().incrementScore(key, taskEntity.getAssignorId(),score);

    }


}
