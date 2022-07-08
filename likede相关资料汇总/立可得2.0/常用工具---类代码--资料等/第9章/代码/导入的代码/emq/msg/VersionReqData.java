package com.lkd.client.emq.msg;

import lombok.Data;

@Data
public class VersionReqData {
    private  long skucfgVersion=0;
    private  long channelCfg=0;
    private  long skuPriceCfg=0;
}