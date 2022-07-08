package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.lkd.dao.RegionDao;
import com.lkd.entity.NodeEntity;
import com.lkd.entity.RegionEntity;
import com.lkd.exception.LogicException;
import com.lkd.http.viewModel.RegionReq;
import com.lkd.service.NodeService;
import com.lkd.service.RegionService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionDao, RegionEntity> implements RegionService {
    @Autowired
    private NodeService nodeService;

    @Override
    public Pager<RegionEntity> search(Long pageIndex, Long pageSize, String name) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RegionEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);

        LambdaQueryWrapper<RegionEntity> wrapper = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(name)){
            wrapper.like(RegionEntity::getName,name);
        }
        this.page(page,wrapper);

        return Pager.build(page);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Boolean add(RegionReq req) {
        RegionEntity regionEntity = new RegionEntity();
        regionEntity.setName(req.getRegionName());
        regionEntity.setRemark(req.getRemark());

        return this.save(regionEntity);
    }

    @Override
    public RegionEntity findById(Long regionId) {
        RegionEntity regionEntity = this.getById(regionId);
        var qw = new LambdaQueryWrapper<NodeEntity>();
        qw.eq(NodeEntity::getRegionId,regionId);
        regionEntity.setNodeList(nodeService.list(qw));

        return regionEntity;
    }

    @Override
    public Boolean delete(Long regionId) {
        var qw = new LambdaQueryWrapper<NodeEntity>();
        qw.eq(NodeEntity::getRegionId,regionId);
        int count = nodeService.count(qw);
        if(count>0){
            throw new LogicException("区域下存在点位，不能删除");
        }

        return this.delete(regionId);
    }
}
