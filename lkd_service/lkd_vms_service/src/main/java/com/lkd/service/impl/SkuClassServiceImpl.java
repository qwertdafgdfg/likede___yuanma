package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.SkuClassDao;
import com.lkd.entity.SkuClassEntity;
import com.lkd.service.SkuClassService;
import com.lkd.viewmodel.Pager;
import org.elasticsearch.common.Strings;
import org.springframework.stereotype.Service;

@Service
public class SkuClassServiceImpl extends ServiceImpl<SkuClassDao,SkuClassEntity> implements SkuClassService{
    @Override
    public Pager<SkuClassEntity> findPage(long pageIndex, long pageSize, String className) {
        var page = new Page<SkuClassEntity>(pageIndex,pageSize);
        if(Strings.isNullOrEmpty(className)){
            this.page(page);
        }else {
            var qw = new LambdaQueryWrapper<SkuClassEntity>();
            qw.like(SkuClassEntity::getClassName,className);

            this.page(page,qw);
        }

        return Pager.build(page);
    }
}
