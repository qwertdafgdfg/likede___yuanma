package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.PolicyEntity;
import com.lkd.entity.VmPolicyEntity;
import com.lkd.viewmodel.Pager;

import java.util.List;

public interface PolicyService extends IService<PolicyEntity> {
    /**
     * 获取售货机的策略
     * @param innerCode
     * @return
     */
    VmPolicyEntity getPolicyByInnerCode(String innerCode);

    /**
     * 给售货机应用策略
     * @param innerCode
     * @param policyId
     * @return
     */
    boolean applyPolicy(List<String> innerCode,int policyId);

    /**
     * 取消策略
     * @param innerCode
     * @param policyId
     * @return
     */
    boolean cancelPolicy(String innerCode, int policyId);

    /**
     * 搜索
     * @param policyName
     * @return
     */
    Pager<PolicyEntity> search(String policyName, long pageIndex, long pageSize);

    /**
     * 删除策略
     * @param policyId
     * @return
     */
    Boolean delete(Integer policyId);
}
