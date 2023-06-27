package com.lkd.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.lkd.dao.SkuDao;
import com.lkd.entity.ChannelEntity;
import com.lkd.entity.SkuClassEntity;
import com.lkd.entity.SkuEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.ChannelService;
import com.lkd.service.SkuClassService;
import com.lkd.service.SkuService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuDao,SkuEntity> implements SkuService{
    @Autowired
    private SkuClassService skuClassService;
    @Autowired
    private ChannelService channelService;



    @Override
    public boolean update(SkuEntity skuEntity) throws LogicException {
        UpdateWrapper<SkuEntity> uw = new UpdateWrapper<>();
        uw.lambda()
                .set(SkuEntity::getClassId,skuEntity.getClassId())
                .set(SkuEntity::getSkuName,skuEntity.getSkuName())
                .set(SkuEntity::getUnit,skuEntity.getUnit())
                .set(SkuEntity::getSkuImage,skuEntity.getSkuImage())
                .set(SkuEntity::getPrice,skuEntity.getPrice())
                .eq(SkuEntity::getSkuId,skuEntity.getSkuId());

        return this.update(uw);
    }

    @Override
    public boolean delete(Long id) {
        QueryWrapper<ChannelEntity> qw = new QueryWrapper<>();
        qw
                .lambda()
                .eq(ChannelEntity::getSkuId,id);
        if(channelService.count(qw) > 0){
            throw new LogicException("该商品正在使用中");
        }

        return this.removeById(id);
    }

    @Override
    public Pager<SkuEntity> findPage(long pageIndex, long pageSize, Integer classId, String skuName) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SkuEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);

        LambdaQueryWrapper<SkuEntity> qw = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(skuName)){
            qw.like(SkuEntity::getSkuName,skuName);
        }
        if(classId !=null && classId >0){
            qw.eq(SkuEntity::getClassId,classId);
        }
        this.page(page,qw);

        return Pager.build(page);
    }


    @Override
    public List<SkuClassEntity> getAllClass() {
        return skuClassService.list();
    }
}
