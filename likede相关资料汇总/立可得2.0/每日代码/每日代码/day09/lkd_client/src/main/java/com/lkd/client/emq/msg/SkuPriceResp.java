package com.lkd.client.emq.msg;

import lombok.Data;

import java.util.List;

@Data
public class SkuPriceResp extends BaseData {
    private List<SkuPriceData> skuPrice;
}
