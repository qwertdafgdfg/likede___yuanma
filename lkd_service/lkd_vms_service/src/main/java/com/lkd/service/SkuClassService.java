package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.SkuClassEntity;
import com.lkd.viewmodel.Pager;

public interface SkuClassService extends IService<SkuClassEntity> {
    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @param className
     * @return
     */
    Pager<SkuClassEntity> findPage(long pageIndex, long pageSize, String className);
}
