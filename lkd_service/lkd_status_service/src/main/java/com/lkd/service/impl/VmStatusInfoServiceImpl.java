package com.lkd.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.lkd.config.RedisDefinetion;
import com.lkd.dao.VmStatusInfoDao;
import com.lkd.entity.StatusTypeEntity;
import com.lkd.entity.VmStatusInfoEntity;
import com.lkd.feignService.VMService;
import com.lkd.http.viewModel.StatusVm;
import com.lkd.http.viewModel.VmStatusVM;
import com.lkd.service.StatusTypeService;
import com.lkd.service.VmStatusInfoService;
import com.lkd.viewmodel.Pager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VmStatusInfoServiceImpl extends ServiceImpl<VmStatusInfoDao,VmStatusInfoEntity> implements VmStatusInfoService{
    private final StatusTypeService statusTypeService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final VMService vmService;


    @Override
    public Pager<VmStatusVM> getAll(long pageIndex, long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VmStatusInfoEntity> pageEntity =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);
        QueryWrapper<VmStatusInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("1",1)
                .lambda()
                .orderByDesc(VmStatusInfoEntity::getUtime);
        this.page(pageEntity,wrapper);

        Pager<VmStatusVM> result = new Pager<>();
        result.setPageIndex(pageEntity.getCurrent());
        result.setPageSize(pageEntity.getSize());
        result.setTotalCount(pageEntity.getTotal());
        List<VmStatusVM> vmStatusList = Lists.newArrayList();
        pageEntity.getRecords().forEach(s ->{
            VmStatusVM vm = this.getVMStatus(s.getInnerCode());
            List<StatusVm> statusVmList = vm.getStatuses()
                    .stream()
                    .filter(status->!status.getStatusCode().equals(s.getStatusCode()))
                    .collect(Collectors.toList());
            vm.getStatuses().clear();
            StatusVm statusVm = new StatusVm();
            statusVm.setStatusCode(s.getStatusCode());
            statusVm.setStatus(s.getStatus());
            statusVm.setDesc(statusTypeService.getByCode(s.getStatusCode()));
            statusVmList.add(statusVm);

            vm.setStatuses(statusVmList);
            vmStatusList.add(vm);
        });
        result.setCurrentPageRecords(vmStatusList);

        return result;
    }

    @Override
    public void setVmStatus(String innerCode, String statusCode, boolean status) throws IOException {
        //redisTemplate.opsForList().leftPush(RedisDefinetion.VM_STATUS_LIST, innerCode);
        String redisKey = RedisDefinetion.STATUS_VM_PREFIX + innerCode;
        redisTemplate.opsForHash().put(redisKey, statusCode, status);
        redisTemplate.opsForHash().put(redisKey,
                "time",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

//        String userName = getRepairerUserName(innerCode);
//        if(Strings.isNullOrEmpty(userName)) return;
//        redisTemplate.opsForHash().put(redisKey,
//                "userName",
//                userName);
//            //从售货机服务获取售货机信息
//            String vmJsonInfo = vmService.getVm(innerCode);
//            String addr = JsonUtil.getNodeByName("data",vmJsonInfo).findPath("node").findPath("addr").asText();
//            String vmType = JsonUtil.getNodeByName("data",vmJsonInfo).findPath("type").findPath("name").asText();
//            redisTemplate.opsForHash().put(redisKey,"vmTypeDesc",vmType);
//            redisTemplate.opsForHash().put(redisKey,"addr",addr);

        this.setVmInfo(innerCode,redisKey);


        QueryWrapper<VmStatusInfoEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(VmStatusInfoEntity::getInnerCode,innerCode)
                .eq(VmStatusInfoEntity::getStatusCode,statusCode);
        VmStatusInfoEntity vmStatusInfoEntity = this.getOne(qw);
        boolean exists = false;
        if(vmStatusInfoEntity != null){
            exists = true;
        }else {
            vmStatusInfoEntity = new VmStatusInfoEntity();
        }

        vmStatusInfoEntity.setInnerCode(innerCode);
        vmStatusInfoEntity.setStatus(status);
        vmStatusInfoEntity.setStatusCode(statusCode);
        vmStatusInfoEntity.setUtime(LocalDateTime.now());
//        vmStatusInfoEntity.setOperaterName(userName);
        if(!exists){
            this.save(vmStatusInfoEntity);
        }else {
            this.updateById(vmStatusInfoEntity);
        }
    }

    @Override
    public VmStatusVM getVMStatus(String innerCode) {
        VmStatusVM vmStatus = generateVMStatus(innerCode);
        if (vmStatus == null) return null;

        String redisKey = RedisDefinetion.STATUS_VM_PREFIX + innerCode;
        this.setVmInfo(innerCode,redisKey);

        List<StatusVm> statusList = Lists.newArrayList();
        List<StatusTypeEntity> statusTypeEntities = statusTypeService.list();
        statusTypeEntities.forEach(s -> {
            StatusVm statusVm = new StatusVm();
            statusVm.setStatusCode(s.getStatusCode());
            statusVm.setDesc(s.getDescr());
            if (!redisTemplate.opsForHash().hasKey(redisKey, s.getStatusCode())) {
                statusVm.setStatus(false);
            } else {
                boolean status = (boolean) redisTemplate.opsForHash().get(redisKey,
                        s.getStatusCode());
                statusVm.setStatus(status);
            }
            statusList.add(statusVm);
        });
        vmStatus.setStatuses(statusList);

        return vmStatus;
    }

    @Override
    public boolean isOnline(String innerCode) {
        VmStatusVM vmStatus = getVMStatus(innerCode);

        return vmStatus.getStatuses().stream().filter(s->"10001".equals(s.getStatusCode())).findFirst().get().isStatus();
    }

    @Override
    public Pager<VmStatusVM> getAllTrouble(long pageIndex, long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<VmStatusInfoEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);


        LambdaQueryWrapper<VmStatusInfoEntity> lqw = new LambdaQueryWrapper<>();
        lqw.select(VmStatusInfoEntity::getInnerCode,VmStatusInfoEntity::getStatusCode)
                .eq(true,VmStatusInfoEntity::getStatus,false)
                .orderByDesc(VmStatusInfoEntity::getUtime);

        this.page(page,lqw);

        Pager<VmStatusVM> result = new Pager<>();
        result.setPageIndex(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setTotalCount(page.getTotal());

        List<VmStatusVM> vmStatusVMS = Lists.newArrayList();
        page.getRecords().forEach(s->{
            vmStatusVMS.add(this.getVMStatus(s.getInnerCode()));
        });
        result.setCurrentPageRecords(vmStatusVMS);

        return result;
    }

    private void setVmInfo(String innerCode,String redisKey){
        //从售货机服务获取售货机信息
        var vmInfo = vmService.getVMInfo(innerCode);
        String addr = vmInfo.getNodeAddr();
        String vmType = vmInfo.getVmTypeName();
        redisTemplate.opsForHash().put(redisKey,"vmTypeDesc",vmType);
        redisTemplate.opsForHash().put(redisKey,"addr",addr);
    }

    /**
     * 根据redis hash构建设备状态对象
     * @param innerCode
     * @return
     */
    private VmStatusVM generateVMStatus(String innerCode){
        VmStatusVM vmStatus = new VmStatusVM();
        vmStatus.setInnerCode(innerCode);

        String redisKey = RedisDefinetion.STATUS_VM_PREFIX + innerCode;
        String userName = (String) redisTemplate.opsForHash().get(redisKey,"userName");
        String time = (String) redisTemplate.opsForHash().get(redisKey,"time");
        String addr = (String) redisTemplate.opsForHash().get(redisKey,"addr");
        String vmType = (String) redisTemplate.opsForHash().get(redisKey,"vmTypeDesc");
        vmStatus.setVmTypeSesc(vmType);
        vmStatus.setAddress(addr);
        vmStatus.setTime(time);
        vmStatus.setOperaterName(userName);

        return vmStatus;
    }

//    private String getRepairerUserName(String innerCode) throws IOException {
//        String jsonData = userService.getRepairer(innerCode);
//        ArrayNode dataNode = (ArrayNode)JsonUtil.getNodeByName("data",jsonData);
//        if(dataNode == null || dataNode.size() <= 0) return null;
//
//        return dataNode.get(0).findPath("userName").asText();
//    }
}
