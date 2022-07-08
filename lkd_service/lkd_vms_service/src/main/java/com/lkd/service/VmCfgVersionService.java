package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.VmCfgVersionEntity;

public interface VmCfgVersionService extends IService<VmCfgVersionEntity> {
    /**
     * 初始化售货机版本配置信息
     * @param vmId
     * @param innerCode
     * @return
     */
    boolean initVersionCfg(long vmId,String innerCode);

    /**
     * 获取售货机版本信息
     * @param innerCode
     * @return
     */
    VmCfgVersionEntity getVmVersion(String innerCode);

    /**
     * 更新补货版本号
     * @param innerCode
     * @return
     */
    boolean updateSupplyVersion(String innerCode);

    /**
     * 更新售货机价格变化版本号
     * @param innerCode
     * @return
     */
    boolean updateSkuPriceVersion(String innerCode);
}
