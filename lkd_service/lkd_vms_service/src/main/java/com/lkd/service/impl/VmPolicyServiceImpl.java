package com.lkd.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.VmPolicyDao;
import com.lkd.entity.VmPolicyEntity;
import com.lkd.service.VmPolicyService;
import org.springframework.stereotype.Service;

@Service
public class VmPolicyServiceImpl extends ServiceImpl<VmPolicyDao,VmPolicyEntity> implements VmPolicyService{
}
