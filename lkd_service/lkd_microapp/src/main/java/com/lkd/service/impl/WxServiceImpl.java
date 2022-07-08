package com.lkd.service.impl;

import com.google.common.base.Strings;
import com.lkd.config.WXConfig;
import com.lkd.service.WxService;
import com.lkd.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WxServiceImpl implements WxService {

    @Autowired
    private WXConfig wxConfig;

    @Override
    public String getOpenId(String jsCode) {
        String getOpenIdUrl = "https://api.weixin.qq.com/sns/jscode2session?appid="+wxConfig.getAppId()+"&secret="+wxConfig.getAppSecret()+"&js_code="+jsCode+"&grant_type=authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        String respResult = restTemplate.getForObject(getOpenIdUrl,String.class);

        log.info("weixin pay result:"+respResult);
        if( Strings.isNullOrEmpty(respResult)) return "";
        try{
            String errorCode = JsonUtil.getValueByNodeName("errcode",respResult) ;
            if(!Strings.isNullOrEmpty(errorCode)){
                int errorCodeInt = Integer.valueOf(errorCode).intValue();
                if(errorCodeInt != 0) return "";
            }

            return JsonUtil.getValueByNodeName("openid",respResult);
        }catch (Exception ex){
            log.error("获取openId失败",ex);
            return "";
        }
    }
}
