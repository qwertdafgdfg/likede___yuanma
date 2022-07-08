package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.RegionEntity;
import com.lkd.http.viewModel.RegionReq;
import com.lkd.viewmodel.Pager;

public interface RegionService extends IService<RegionEntity> {
    /**
     * 分页搜索
     * @param pageIndex
     * @param pageSize
     * @param name
     * @return
     */
    Pager<RegionEntity> search(Long pageIndex, Long pageSize, String name);

    /**
     * 添加区域
     * @param req
     * @return
     */
    Boolean add(RegionReq req);

    /**
     * 获取区域详情
     * @param regionId
     * @return
     */
    RegionEntity findById(Long regionId);

    /**
     * 删除区域
     * @param regionId
     * @return
     */
    Boolean delete(Long regionId);
}
