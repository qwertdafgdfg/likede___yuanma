package com.lkd.business.msgHandler;

import com.google.common.collect.Lists;
import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.config.TopicConfig;
import com.lkd.contract.Channel;
import com.lkd.contract.ChannelCfg;
import com.lkd.emq.MqttProducer;
import com.lkd.entity.ChannelEntity;
import com.lkd.entity.VmCfgVersionEntity;
import com.lkd.service.VendingMachineService;
import com.lkd.service.VmCfgVersionService;
import com.lkd.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 处理货道请求
 */
@Component
@ProcessType(value = "channelCfgReq")
public class ChannelCfgMsgHandler implements MsgHandler{
    @Autowired
    @Lazy
    private VmCfgVersionService versionService;
    @Autowired
    @Lazy
    private VendingMachineService vmService;
    @Autowired
    @Lazy
    private MqttProducer mqttProducer;
    @Override
    public void process(String jsonMsg) throws IOException {
        String innerCode = JsonUtil.getNodeByName("vmId",jsonMsg).asText();
        long sn = JsonUtil.getNodeByName("sn",jsonMsg).asLong();
        VmCfgVersionEntity version = versionService.getVmVersion(innerCode);
        long versionId = 0L;
        if(version != null){
            versionId = version.getSkuCfgVersion();
        }
        ChannelCfg cfg = new ChannelCfg();
        cfg.setInnerCode(innerCode);
        cfg.setSn(sn);
        cfg.setVersionId(versionId);
        List<ChannelEntity> channelEntityList = vmService.getAllChannel(innerCode);
        List<Channel> channels = Lists.newArrayList();
        channelEntityList.forEach(c->{
            Channel channelContract =
                    new Channel();
            channelContract.setSkuId(c.getSkuId());
            channelContract.setChannelId(c.getChannelCode());
            channelContract.setCapacity(c.getMaxCapacity());
            channels.add(channelContract);
        });
        cfg.setChannels(channels);
        cfg.setNeedResp(true);
        mqttProducer.send(TopicConfig.TO_VM_TOPIC,2,cfg);
    }
}

