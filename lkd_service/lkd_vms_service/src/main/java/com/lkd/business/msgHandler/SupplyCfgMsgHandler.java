package com.lkd.business.msgHandler;

import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.business.VmCfgService;
import com.lkd.config.TopicConfig;
import com.lkd.contract.ChannelCfg;
import com.lkd.contract.SkuCfg;
import com.lkd.contract.SkuPriceCfg;
import com.lkd.contract.SupplyCfg;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.VmCfgVersionEntity;
import com.lkd.service.VendingMachineService;
import com.lkd.service.VmCfgVersionService;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 补货消息处理
 */
@Component
@ProcessType(value = "supplyResp")
@Slf4j
public class SupplyCfgMsgHandler implements MsgHandler {
    @Autowired
    private VendingMachineService vmService;
    @Autowired
    private VmCfgVersionService versionService;

    @Autowired
    private VmCfgService vmCfgService;

    @Autowired
    private MqttProducer  mqttProducer;

    @Override
    public void process(String jsonMsg) throws IOException {
        //解析补货协议
        SupplyCfg supplyCfg = JsonUtil.getByJson(jsonMsg, SupplyCfg.class);
        //更新售货机库存
        vmService.supply(supplyCfg);

        String innerCode = supplyCfg.getInnerCode();//获取售货机编号
        VmCfgVersionEntity vsersion = versionService.getVmVersion(innerCode);//获取版本

        //主题
        String topic = TopicConfig.TO_VM_TOPIC + innerCode;

        //下发商品配置
        SkuCfg skuCfg = vmCfgService.getSkuCfg(innerCode);
        skuCfg.setSn(System.nanoTime());//纳秒
        skuCfg.setVersionId(vsersion.getSkuCfgVersion());
        //将商品配置发送到售货机
        mqttProducer.send(topic,2,skuCfg);


        //下发价格配置
        SkuPriceCfg skuPriceCfg = vmCfgService.getSkuPriceCfg(innerCode);
        skuPriceCfg.setSn(System.nanoTime());
        skuPriceCfg.setVersionId(vsersion.getPriceCfgVersion());
        //将价格配置发送到售货机
        mqttProducer.send(topic,2,skuPriceCfg);

        //下发货道配置
        ChannelCfg channelCfg = vmCfgService.getChannelCfg(innerCode);
        channelCfg.setSn(System.nanoTime());
        channelCfg.setVersionId(vsersion.getChannelCfgVersion());
        //将货道配置发送到售货机
        mqttProducer.send(topic,2,channelCfg);

        //下发补货信息
        supplyCfg.setVersionId(vsersion.getSupplyVersion());
        supplyCfg.setNeedResp(true);
        supplyCfg.setSn(System.nanoTime());
        supplyCfg.setVersionId(vsersion.getSupplyVersion());
        //将补货信息发送到售货机
        mqttProducer.send(topic,2,supplyCfg);

    }
}
