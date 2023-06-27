package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.StatusTypeEntity;

public interface StatusTypeService extends IService<StatusTypeEntity> {
    String getByCode(String code);
}
