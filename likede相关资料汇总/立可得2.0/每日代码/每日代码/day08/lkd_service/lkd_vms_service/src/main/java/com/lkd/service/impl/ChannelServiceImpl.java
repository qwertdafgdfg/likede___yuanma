package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.ChannelDao;
import com.lkd.entity.ChannelEntity;
import com.lkd.entity.PolicyEntity;
import com.lkd.entity.SkuEntity;
import com.lkd.entity.VmPolicyEntity;
import com.lkd.exception.LogicException;
import com.lkd.http.viewModel.VMChannelConfig;
import com.lkd.service.ChannelService;
import com.lkd.service.PolicyService;
import com.lkd.service.SkuService;
import com.lkd.viewmodel.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelDao,ChannelEntity> implements ChannelService{
    @Autowired
    private PolicyService policyService;
    @Autowired
    private SkuService skuService;


    @Override
    public Pager<ChannelEntity> findPage(long pageIndex, long pageSize, Map searchMap) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ChannelEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);

        QueryWrapper queryWrapper = createQueryWrapper( searchMap );
        this.page(page,queryWrapper);

        Pager<ChannelEntity> pageResult = new Pager<>();
        pageResult.setCurrentPageRecords(page.getRecords());
        pageResult.setPageIndex(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setTotalCount(page.getTotal());
        return pageResult;
    }

    @Override
    public List<ChannelEntity> getChannelesByInnerCode(String innerCode) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChannelEntity::getInnerCode,innerCode);

        return this.list(queryWrapper);
    }

    @Override
    public ChannelEntity getChannelInfo(String innerCode, String channelCode) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(ChannelEntity::getInnerCode,innerCode)
                .eq(ChannelEntity::getChannelCode,channelCode);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean supply(ChannelEntity channel) {
        channel.setLastSupplyTime(LocalDateTime.now());

        return this.updateById(channel);
    }

    @Override
    public boolean mapSku(String innerCode, String channelCode, long skuId,Integer policyId) {
        ChannelEntity channel = this.getChannelInfo(innerCode,channelCode);
        if(channel == null){
            throw new LogicException("该货道不存在");
        }
        channel.setSkuId(skuId);
        if(policyId >= 0){
            int realPrice = this.calRealPrice(skuId,policyId);
            channel.setPrice(realPrice);
        }else {
            int realPrice = skuService.getById(skuId).getPrice();
            channel.setPrice(realPrice);
        }
        return this.updateById(channel);
    }

    @Override
    public Integer getRealPrice(String innerCode, Long skuId) {
        LambdaQueryWrapper<ChannelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(ChannelEntity::getInnerCode,innerCode)
                .eq(ChannelEntity::getSkuId,skuId);
        List<ChannelEntity> channelList = this.list(queryWrapper);
        if(channelList == null || channelList.size() <=0){
            return 0;
        }

        return channelList.stream().findFirst().get().getPrice();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Boolean mapSku(VMChannelConfig channelConfig) {
        VmPolicyEntity vmPolicyEntity = policyService.getPolicyByInnerCode(channelConfig.getInnerCode());
        Integer policyId = 0;
        if(vmPolicyEntity != null){
            policyId = vmPolicyEntity.getPolicyId();
            Integer finalPolicyId = policyId;
            channelConfig.getChannelList().forEach(c-> this.mapSku(channelConfig.getInnerCode(),c.getChannelCode(),Long.valueOf(c.getSkuId()), finalPolicyId));
        }else {
            channelConfig.getChannelList().forEach(c-> this.mapSku(channelConfig.getInnerCode(),c.getChannelCode(),Long.valueOf(c.getSkuId()), 0));
        }
        return true;
    }

    @Override
    public List<ChannelEntity> findList(Map searchMap) {
        QueryWrapper queryWrapper = createQueryWrapper( searchMap );

        return this.list(queryWrapper);
    }

    /**
     * 条件构建
     * @param searchMap
     * @return
     */
    private QueryWrapper createQueryWrapper(Map searchMap){
        QueryWrapper queryWrapper=new QueryWrapper(  );
        if(searchMap!=null){
            queryWrapper.allEq(searchMap);
        }
        return queryWrapper;
    }

    /**
     * 计算真实售价
     * @param skuId
     * @return
     */
    private Integer calRealPrice(Long skuId,Integer policyId){
        PolicyEntity policy = policyService.getById(policyId);
        SkuEntity skuEntity = skuService.getById(skuId);
        if(skuEntity == null) return 0;

        if(policy != null) {

            BigDecimal price = new BigDecimal(skuEntity.getPrice() * policy.getDiscount());

            return price.divide(new BigDecimal(100),2,RoundingMode.HALF_UP).intValue();
        }

        return skuEntity.getPrice();
    }

}
