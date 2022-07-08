package com.lkd.client.emq.msg;

import lombok.Data;

@Data
public class ChannelRespData {
    private  String channelId;
    private  String skuId;
    private  Integer capacity;
}
