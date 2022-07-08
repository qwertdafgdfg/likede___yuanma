package com.lkd.client.config;
import lombok.Getter;

@Getter
public enum MsgType {
    versionCfg("versionCfg","同步基础信息"),
    channelCfg("channelCfg","同步管道信息"),
    skuCfg("skuCfgResp","同步商品信息"),
    skuPrice("skuPrice","价格变动"),
    supplyReq("supplyReq","价格变动"),
    vendoutReq("vendoutReq","出货通知"),
    vendoutResp("vendoutResp","出货上报");


    private String type;
    private String desc;

    MsgType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
