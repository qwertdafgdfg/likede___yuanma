package com.lkd.business.msgHandler;

import com.google.common.base.Strings;
import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.business.VmCfgService;
import com.lkd.config.TopicConfig;
import com.lkd.contract.SkuCfg;
import com.lkd.emq.MqttProducer;
import com.lkd.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 处理商品配置请求
 */
@Component
@ProcessType(value = "skuCfgReq")
public class SkuCfgMsgHandler implements MsgHandler{
    @Autowired
    private VmCfgService vmCfgService;
    @Autowired
    private MqttProducer mqttProducer;
    @Override
    public void process(String jsonMsg) throws IOException {
        String innerCode = JsonUtil.getValueByNodeName("vmId",jsonMsg);
        if(Strings.isNullOrEmpty(innerCode)) return;
        long sn = JsonUtil.getNodeByName("sn",jsonMsg).asLong();
        SkuCfg skuCfg = vmCfgService.getSkuCfg(innerCode);
        skuCfg.setSn(sn);
        mqttProducer.send(TopicConfig.TO_VM_TOPIC,2,skuCfg);
    }
}
