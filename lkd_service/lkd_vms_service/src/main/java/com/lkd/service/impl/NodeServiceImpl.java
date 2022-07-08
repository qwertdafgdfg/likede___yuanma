package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.lkd.dao.NodeDao;
import com.lkd.entity.NodeEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.NodeService;
import com.lkd.service.VendingMachineService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NodeServiceImpl  extends ServiceImpl<NodeDao, NodeEntity> implements NodeService{
    @Autowired
    private  VendingMachineService vmService;

    @Override
    public boolean add(NodeEntity node) throws LogicException {
        return this.save(node);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean update(NodeEntity nodeEntity) throws LogicException {
        //同时更新点位下所有售货机相关信息
        this.getVmList(nodeEntity.getId())
                .forEach(vm-> {
                    UpdateWrapper<VendingMachineEntity> uw = new UpdateWrapper<>();
                    uw
                            .lambda()
                            .eq(VendingMachineEntity::getNodeId,nodeEntity.getId())
                            .set(VendingMachineEntity::getOwnerId,nodeEntity.getOwnerId())
                            .set(VendingMachineEntity::getOwnerName,nodeEntity.getOwnerName())
                            .set(VendingMachineEntity::getRegionId,nodeEntity.getRegionId())
                            .set(VendingMachineEntity::getBusinessId,nodeEntity.getBusinessId());
                    vmService.update(uw);
                });

       return this.updateById(nodeEntity);
    }

    @Override
    public boolean delete(Long id) {
        LambdaQueryWrapper<VendingMachineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(VendingMachineEntity::getNodeId,id);
        Integer count = vmService.count(queryWrapper);
        if(count>0){
            throw new LogicException("点位下有售货机，不可删除");
        }

        return this.removeById(id);
    }

    @Override
    public Pager<NodeEntity> search(String name, String regionId, long pageIndex, long pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<NodeEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);
        LambdaQueryWrapper<NodeEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(name)){
            queryWrapper.like(NodeEntity::getName,name);
        }
        if(!Strings.isNullOrEmpty(regionId)){
            Long regionIdLong = Long.valueOf(regionId);
            queryWrapper.eq(NodeEntity::getRegionId,regionIdLong);
        }

        return Pager.build(this.page(page,queryWrapper));
    }

    @Override
    public List<VendingMachineEntity> getVmList(long id) {
        QueryWrapper<VendingMachineEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(VendingMachineEntity::getNodeId,id);

        return vmService.list(qw);
    }

    @Override
    public Integer getCountByOwner(Integer ownerId) {
        LambdaQueryWrapper<NodeEntity> qw = new LambdaQueryWrapper<>();
        qw.eq(NodeEntity::getOwnerId,ownerId);

        return this.count(qw);
    }
}
