package com.lkd.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.RoleDao;
import com.lkd.entity.RoleEntity;
import com.lkd.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleDao,RoleEntity> implements RoleService{
}
