package com.github.wxpay.sdk;

import com.lkd.conf.WXConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class WxPaySdkConfig extends WXPayConfig {
    @Autowired
    private WXConfig wxConfig;

    public String getAppID() {
        return wxConfig.getAppId();
    }

    public String getMchID() {
        return wxConfig.getMchId();
    }

    public String getKey() {
        return wxConfig.getPartnerKey();
    }

    InputStream getCertStream() {
        return null;
    }

    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            public void report(String s, long l, Exception e) {

            }

            public DomainInfo getDomain(WXPayConfig wxPayConfig) {
                return new DomainInfo("api.mch.weixin.qq.com",true);
            }
        };
    }
}