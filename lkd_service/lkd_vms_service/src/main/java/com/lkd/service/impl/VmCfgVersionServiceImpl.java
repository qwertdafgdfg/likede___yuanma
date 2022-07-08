package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.VmCfgVersionDao;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.entity.VmCfgVersionEntity;
import com.lkd.service.VendingMachineService;
import com.lkd.service.VmCfgVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VmCfgVersionServiceImpl extends ServiceImpl<VmCfgVersionDao,VmCfgVersionEntity> implements VmCfgVersionService{
    @Autowired
    private VendingMachineService vmService;

    @Override
    public boolean initVersionCfg(long vmId,String innerCode) {
        VmCfgVersionEntity cfgVersionEntity = new VmCfgVersionEntity();
        cfgVersionEntity.setVmId(vmId);
        cfgVersionEntity.setInnerCode(innerCode);
        cfgVersionEntity.setBasecfgVersion(1L);
        cfgVersionEntity.setChannelCfgVersion(1L);
        cfgVersionEntity.setPriceCfgVersion(1L);
        cfgVersionEntity.setSkuCfgVersion(1L);
        cfgVersionEntity.setSupplyVersion(0L);

        return this.save(cfgVersionEntity);
    }

    @Override
    public VmCfgVersionEntity getVmVersion(String innerCode) {
        QueryWrapper<VmCfgVersionEntity> qw = new QueryWrapper<VmCfgVersionEntity>();
        qw.lambda()
                .eq(VmCfgVersionEntity::getInnerCode,innerCode);

        return this.getOne(qw);
    }

    @Override
    public boolean updateSupplyVersion(String innerCode) {
        VmCfgVersionEntity version = getByInnerCode(innerCode);
        if(version == null){
            VendingMachineEntity vendingMachineEntity = vmService.findByInnerCode(innerCode);
            this.initVersionCfg(vendingMachineEntity.getId(),innerCode);
            return true;
        }

        UpdateWrapper<VmCfgVersionEntity> uw = new UpdateWrapper<>();
        uw.lambda()
                .eq(VmCfgVersionEntity::getInnerCode,innerCode)
                .setSql(true,"supply_version=supply_version+1");

        return this.update(uw);
    }

    @Override
    public boolean updateSkuPriceVersion(String innerCode) {
        VmCfgVersionEntity version = this.getVmVersion(innerCode);
        if(version == null){
            VendingMachineEntity vendingMachineEntity = vmService.findByInnerCode(innerCode);
            this.initVersionCfg(vendingMachineEntity.getId(),innerCode);

            return true;
        }

        UpdateWrapper<VmCfgVersionEntity> uw = new UpdateWrapper<>();
        uw.lambda()
                .eq(VmCfgVersionEntity::getInnerCode,innerCode)
                .setSql(true,"price_cfg_version=price_cfg_version+1");

        return this.update(uw);
    }

    private VmCfgVersionEntity getByInnerCode(String innerCode){
        QueryWrapper<VmCfgVersionEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(VmCfgVersionEntity::getInnerCode,innerCode);

        return this.getOne(qw);
    }

}
