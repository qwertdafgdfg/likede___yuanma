package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.SkuClassEntity;
import com.lkd.entity.SkuEntity;
import com.lkd.exception.LogicException;
import com.lkd.viewmodel.Pager;

import java.util.List;

public interface SkuService extends IService<SkuEntity> {
    /**
     * 修改
     * @param skuEntity
     * @return
     */
    boolean update(SkuEntity skuEntity) throws LogicException;

    /**
     * 删除
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @param skuName
     * @return
     */
    Pager<SkuEntity> findPage(long pageIndex, long pageSize, Integer classId, String skuName);

    /**
     * 获取所有商品类别
     * @return
     */
    List<SkuClassEntity> getAllClass();

}
