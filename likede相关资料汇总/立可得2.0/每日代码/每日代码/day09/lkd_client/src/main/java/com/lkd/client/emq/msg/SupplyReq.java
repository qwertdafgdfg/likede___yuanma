package com.lkd.client.emq.msg;

import lombok.Data;

import java.util.List;

/**
 * 补货对象
 */
@Data
public class SupplyReq extends BaseData {

    private List<ChannelRespData> supplyData;
}
