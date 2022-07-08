package com.lkd.contract;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 货道配置
 */
@Data
public class ChannelCfg extends BaseContract implements Serializable{
    /**
     * 配置版本号
     */
    private long versionId;
    /**
     * 售货机编号
     */
    private String vmId;
    /**
     * 货道
     */
    private List<Channel> channels;

    public ChannelCfg() {
        this.setMsgType("channelCfg");
    }
}
