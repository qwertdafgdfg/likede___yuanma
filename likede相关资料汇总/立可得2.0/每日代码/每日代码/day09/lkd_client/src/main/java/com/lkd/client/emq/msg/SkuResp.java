package com.lkd.client.emq.msg;

import lombok.Data;

import java.util.List;

/**
 *  商品基础信息交换协议
 */
@Data
public class SkuResp extends BaseData {
    private List<SkuRespData> skus;
}
