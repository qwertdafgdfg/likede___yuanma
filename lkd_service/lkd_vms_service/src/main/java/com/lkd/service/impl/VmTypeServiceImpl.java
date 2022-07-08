package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.VmTypeDao;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.entity.VmTypeEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.VendingMachineService;
import com.lkd.service.VmTypeService;
import com.lkd.viewmodel.Pager;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VmTypeServiceImpl extends ServiceImpl<VmTypeDao,VmTypeEntity> implements VmTypeService{
    @Autowired
    private VendingMachineService vmService;

    @Override
    public Boolean delete(Integer id) {
        LambdaQueryWrapper<VendingMachineEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(VendingMachineEntity::getVmType,id);
        if(vmService.count(qw) > 0){
            throw new LogicException("该售货机类型在使用");
        }

        return this.removeById(id);
    }

    @Override
    public Pager<VmTypeEntity> search(long pageIndex,long pageSize,String name) {
        var page = new Page<VmTypeEntity>(pageIndex,pageSize);
        var qw = new LambdaQueryWrapper<VmTypeEntity>();
        if(!Strings.isNullOrEmpty(name)){
            qw.like(VmTypeEntity::getName,name);
        }
        this.page(page,qw);

        return Pager.build(page);
    }
}
