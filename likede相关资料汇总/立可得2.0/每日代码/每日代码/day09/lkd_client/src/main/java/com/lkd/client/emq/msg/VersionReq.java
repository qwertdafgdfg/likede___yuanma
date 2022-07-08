package com.lkd.client.emq.msg;

import com.lkd.client.config.MsgType;
import lombok.Data;


@Data
public class VersionReq extends BaseData {
    public VersionReq() {
        this.setMsgType(MsgType.versionCfg.getType());
    }
    private VersionReqData data =new VersionReqData();
}
