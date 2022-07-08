package com.lkd.client.emq.msg;

import lombok.Data;

import java.util.List;

/**
 * 货架同步对象
 */
@Data
public class ChannelResp extends BaseData {

    private List<ChannelRespData> channels;

}
