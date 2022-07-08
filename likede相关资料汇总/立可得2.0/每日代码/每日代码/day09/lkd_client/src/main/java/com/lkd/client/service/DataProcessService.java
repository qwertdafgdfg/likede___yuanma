package com.lkd.client.service;

import com.lkd.client.emq.msg.*;

public interface DataProcessService {

    /**
     * 处理服务器同步商品信息通知
     * @param skuResp
     */
    public void syncSkus(SkuResp skuResp);


    /**
     * 处理服务器同步货道通知
     * @param channelResp
     */
    public void syncChannel(ChannelResp channelResp);


    /**
     * 处理服务器价格同步通知
     * @param skuPriceResp
     */
    public void syncSkuPrices(SkuPriceResp skuPriceResp);

    /**
     * 处理服务器出货通知
     * @param vendoutReq
     */
    public void vendoutReq(VendoutReq vendoutReq);


    /**
     * 出货上报
     * @param  orderNo
     */
    public void vendoutResp(String orderNo);

    /**
     * 出货上报完成后逻辑
     * @param  orderNo
     */
    public void vendoutComplete(String orderNo);


    /**
     * 售货机启动时候检查是否未上报信息
     */
    public void checkVendoutOrder();



    /**
     * 处理服务器补单通知
     * @param  supplyReq
     */
    public void supplyReq(SupplyReq supplyReq);

}
