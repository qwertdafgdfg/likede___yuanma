package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.VmTypeEntity;
import com.lkd.viewmodel.Pager;

public interface VmTypeService extends IService<VmTypeEntity> {
    /**
     * 删除设备
     * @param id
     * @return
     */
    Boolean delete(Integer id);

    /**
     *
     * @param name
     * @return
     */
    Pager<VmTypeEntity> search(long pageIndex,long pageSize,String name);
}
