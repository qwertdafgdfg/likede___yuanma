package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 售货机货道配置
 */
@Data
public class VMChannelConfig implements Serializable {
    private String innerCode;
    private List<SetChannelSkuReq> channelList;
}
