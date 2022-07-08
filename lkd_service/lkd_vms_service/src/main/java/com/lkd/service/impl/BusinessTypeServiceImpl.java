package com.lkd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.BusinessTypeDao;
import com.lkd.entity.BusinessTypeEntity;
import com.lkd.service.BusinessTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessTypeServiceImpl extends ServiceImpl<BusinessTypeDao, BusinessTypeEntity> implements BusinessTypeService {
}
