package com.lkd.business.msgHandler;

import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.business.VmCfgService;
import com.lkd.config.TopicConfig;
import com.lkd.contract.SkuPriceCfg;
import com.lkd.emq.MqttProducer;
import com.lkd.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 商品价格变更
 */
@Component
@ProcessType(value = "skuPrice")
public class PriceCfgMsgHandler implements MsgHandler{
    @Autowired
    private VmCfgService vmCfgService;
    @Autowired
    private MqttProducer mqttProducer;
    @Override
    public void process(String jsonMsg) throws IOException {
        String innerCode = JsonUtil.getValueByNodeName("innerCode",jsonMsg);
        long sn = JsonUtil.getNodeByName("sn",jsonMsg).asLong();
        SkuPriceCfg cfg = vmCfgService.getSkuPriceCfg(innerCode);
        cfg.setSn(sn);
        mqttProducer.send(TopicConfig.TO_VM_TOPIC,2,cfg);
    }
}

