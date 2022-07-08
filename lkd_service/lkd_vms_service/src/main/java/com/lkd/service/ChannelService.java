package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.ChannelEntity;
import com.lkd.http.viewModel.VMChannelConfig;
import com.lkd.viewmodel.Pager;

import java.util.List;
import java.util.Map;

public interface ChannelService extends IService<ChannelEntity> {
    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    List<ChannelEntity> findList(Map searchMap);

    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @param searchMap
     * @return
     */
    Pager<ChannelEntity> findPage(long pageIndex, long pageSize, Map searchMap);

    /**
     * 根据设备编号获取货道
     * @param innerCode
     * @return
     */
    List<ChannelEntity> getChannelesByInnerCode(String innerCode);

    /**
     * 获取货道信息
     * @param innerCode
     * @param channelCode
     * @return
     */
    ChannelEntity getChannelInfo(String innerCode,String channelCode);

    /**
     * 补货
     * @param channel
     * @return
     */
    boolean supply(ChannelEntity channel);

    /**
     * 关联商品
     * @param innerCode
     * @param channelCode
     * @param skuId
     * @return
     */
    boolean mapSku(String innerCode,String channelCode,long skuId,Integer policyId);

    /**
     * 获取售货机中商品的真实售价
     * @param innerCode
     * @param skuId
     * @return
     */
    Integer getRealPrice(String innerCode,Long skuId);

    /**
     * 设置货道
     * @param channelConfig
     * @return
     */
    Boolean mapSku(VMChannelConfig channelConfig);
}
