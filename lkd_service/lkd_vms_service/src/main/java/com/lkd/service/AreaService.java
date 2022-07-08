package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.AreaEntity;

import java.util.List;

public interface AreaService extends IService<AreaEntity> {
    /**
     * 获取所有根节点
     * @return
     */
    List<AreaEntity> getAllRootAreaList();

    /**
     * 获取所有一级子节点
     * @param parentId
     * @return
     */
    List<AreaEntity> getAllChildren(int parentId);
}
