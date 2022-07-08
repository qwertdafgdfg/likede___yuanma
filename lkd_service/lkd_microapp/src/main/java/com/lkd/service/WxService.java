package com.lkd.service;

/**
 * 微信服务接口
 */
public interface WxService {

    /**
     * 通过jsCode获取openId
     * @param jsCode
     * @return
     */
    String getOpenId(String jsCode);

}
