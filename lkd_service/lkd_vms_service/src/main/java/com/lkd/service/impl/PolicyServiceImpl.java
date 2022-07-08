package com.lkd.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lkd.business.VmCfgService;
import com.lkd.config.TopicConfig;
import com.lkd.contract.SkuPrice;
import com.lkd.contract.SkuPriceCfg;
import com.lkd.dao.PolicyDao;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.PolicyEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.entity.VmPolicyEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.*;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.SkuViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PolicyServiceImpl extends ServiceImpl<PolicyDao,PolicyEntity> implements PolicyService{
    @Autowired
    private VendingMachineService vmService;

    @Autowired
    private VmPolicyService vmPolicyService;

    @Autowired
    private VmCfgVersionService versionService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private MqttProducer mqttProducer;

    @Autowired
    private VmCfgService vmCfgService;

    @Override
    public VmPolicyEntity getPolicyByInnerCode(String innerCode) {
        QueryWrapper<VmPolicyEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(VmPolicyEntity::getInnerCode,innerCode);

        return vmPolicyService.getOne(qw);
    }

    /**
     * 将策略应用到售货机
     * @param innerCodeList
     * @param policyId
     * @return
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean applyPolicy(List<String> innerCodeList, int policyId) {
        innerCodeList.forEach(code->{
            VendingMachineEntity vm = vmService.findByInnerCode(code);
            if(vm == null)
                throw new LogicException("售货机不存在");
            VmPolicyEntity vmToPolicyEntity = new VmPolicyEntity();
            PolicyEntity policyEntity = this.getById(policyId);
            if(policyEntity == null)
                throw new LogicException("策略不存在");
            //更新售货机货道价格
            channelService
                    .getChannelesByInnerCode(vm.getInnerCode())
                    .stream()
                    .filter(c->c.getSku() != null)
                    .forEach(c->{
                        channelService.mapSku(vm.getInnerCode(),c.getChannelCode(),c.getSkuId(),policyId);
                    });

            //更新售货机策略配置
            vmToPolicyEntity.setDiscount(policyEntity.getDiscount());
            vmToPolicyEntity.setInnerCode(code);
            vmToPolicyEntity.setPolicyName(policyEntity.getPolicyName());
            vmToPolicyEntity.setVmId(vm.getId());
            vmToPolicyEntity.setPolicyId(policyEntity.getPolicyId());

            QueryWrapper<VmPolicyEntity> wrapper = new QueryWrapper<>();
            wrapper.lambda()
                    .eq(VmPolicyEntity::getInnerCode,code);
            vmPolicyService.remove(wrapper);
            vmPolicyService.save(vmToPolicyEntity);
            versionService.updateSkuPriceVersion(code);
            //应用完策略之后，将价格的变动通知到售货机
            notifyPriceToVm(code);
        });

        return true;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean cancelPolicy(String innerCode, int policyId) {
        UpdateWrapper<VmPolicyEntity> uw = new UpdateWrapper<>();
        uw.lambda()
                .eq(VmPolicyEntity::getInnerCode,innerCode)
                .eq(VmPolicyEntity::getPolicyId,policyId);
        vmPolicyService.remove(uw);
        versionService.updateSkuPriceVersion(innerCode);
        //取消策略之后，将价格的变动通知到售货机
        notifyPriceToVm(innerCode);

        return true;
    }

    @Override
    public Pager<PolicyEntity> search(String policyName, long pageIndex, long pageSize) {
        LambdaQueryWrapper<PolicyEntity> qw = new LambdaQueryWrapper<>();
        if(!Strings.isNullOrEmpty(policyName))
            qw.like(PolicyEntity::getPolicyName,policyName);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PolicyEntity> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex,pageSize);

        this.page(page,qw);

        return Pager.build(page);
    }

    @Override
    public Boolean delete(Integer policyId) {
        var qw = new LambdaQueryWrapper<VmPolicyEntity>();
        qw.eq(VmPolicyEntity::getPolicyId,policyId);
        int count = vmPolicyService.count(qw);
        if(count > 0){
            throw new LogicException("该策略正在使用");
        }

        return this.removeById(policyId);
    }

    /**
     * 商品价格变更信息通知到售货机
     * @param innerCode
     */
    private void notifyPriceToVm(String innerCode){
        SkuPriceCfg skuPriceCfg = vmCfgService.getSkuPriceCfg(innerCode);
        skuPriceCfg.setVersionId(System.nanoTime());
        skuPriceCfg.setSn(System.nanoTime());
        List<SkuViewModel> skuList = vmService.getSkuList(innerCode);

        List<SkuPrice> skuPrices = Lists.newArrayList();
        skuList.stream()
                .forEach(sku->{
                    SkuPrice skuPrice = new SkuPrice();
                    skuPrice.setDiscount(sku.isDiscount());
                    skuPrice.setPrice(sku.getPrice());
                    skuPrice.setRealPrice(channelService.getRealPrice(innerCode,sku.getSkuId()));
                    skuPrice.setSkuId(sku.getSkuId());
                    skuPrices.add(skuPrice);
                });
        skuPriceCfg.setSkuPrice(skuPrices);

        try {
            mqttProducer.send(TopicConfig.TO_VM_TOPIC+innerCode,2,skuPriceCfg);
        } catch (JsonProcessingException e) {
            log.error("serialize pricecfg error,inner code:"+innerCode,e);
        }
    }
}
