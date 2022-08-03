package com.lkd.business.msgHandler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lkd.annotations.ProcessType;
import com.lkd.business.MsgHandler;
import com.lkd.contract.server.PartnerUpdate;
import com.lkd.entity.NodeEntity;
import com.lkd.service.NodeService;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 更新合作商信息
 */
@Slf4j
@Component
@ProcessType(value = "partnerUpdate")
public class PartnerUpdateHandler implements MsgHandler {
    @Autowired
    @Lazy
    private NodeService nodeService;
    @Override
    public void process(String jsonMsg) throws IOException {
        PartnerUpdate partnerUpdate = JsonUtil.getByJson(jsonMsg, PartnerUpdate.class);

        QueryWrapper<NodeEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(NodeEntity::getOwnerId,partnerUpdate.getId());
        nodeService
                .list(wrapper)
                .forEach(n->{
                    n.setOwnerName(partnerUpdate.getName());
                    nodeService.update(n);
                });
    }
}
