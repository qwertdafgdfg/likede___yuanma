package com.lkd.business.msgHandler;

import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.business.VmCfgService;
import com.lkd.config.TopicConfig;
import com.lkd.contract.ChannelCfg;
import com.lkd.contract.SkuCfg;
import com.lkd.contract.SkuPriceCfg;
import com.lkd.contract.VersionCfg;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.VmCfgVersionEntity;
import com.lkd.service.VmCfgVersionService;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 售货机版本配置处理
 */
@Component
@ProcessType(value = "versionCfg")
@Slf4j
public class VersionCfgMsgHandler implements MsgHandler{
    @Autowired
    @Lazy
    private VmCfgVersionService versionService;
    @Autowired
    @Lazy
    private VmCfgService vmCfgService;
    @Autowired
    @Lazy
    private MqttProducer mqttProducer;

    @Override
    public void process(String jsonMsg) throws IOException {
        try{
            VersionCfg versionCfg = JsonUtil.getByJson(jsonMsg, VersionCfg.class);
            if(versionCfg.getData() == null) return;
            VmCfgVersionEntity versionInfo = versionService.getVmVersion(versionCfg.getInnerCode());
            if(versionInfo == null) return;

            if(versionCfg.getData().getSkucfgVersion() < versionInfo.getSkuCfgVersion()){
                //下发商品配置和价格配置
                SkuCfg skuCfg = vmCfgService.getSkuCfg(versionCfg.getInnerCode());
                mqttProducer.send(TopicConfig.TO_VM_TOPIC+versionCfg.getInnerCode(),2,skuCfg);
            }

            if(versionCfg.getData().getSkuPriceCfg() < versionInfo.getPriceCfgVersion()){
                SkuPriceCfg skuPriceCfg = vmCfgService.getSkuPriceCfg(versionCfg.getInnerCode());
                mqttProducer.send(TopicConfig.TO_VM_TOPIC+versionCfg.getInnerCode(),2,skuPriceCfg);
            }

            if(versionCfg.getData().getChannelCfg() < versionInfo.getChannelCfgVersion()){
                ChannelCfg channelCfg = vmCfgService.getChannelCfg(versionCfg.getInnerCode());
                mqttProducer.send(TopicConfig.TO_VM_TOPIC+versionCfg.getInnerCode(),2,channelCfg);
            }
        }catch (Exception e){
            log.error("process versionCfg error. vm request msg is: " + jsonMsg,e);
        }

    }
}

