package com.lkd.sms;

import com.alibaba.alicloud.sms.ISmsService;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsSender {
    @Autowired
    private SmsConfig smsConfig;
    @Autowired
    private ISmsService smsService;

    /**
     *  发送验证码短信
     * @param telphone 手机号
     * @param code 手机验证码
     */
    public void sendMsg(String telphone,String code){
        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();

        // 必填:待发送手机号
        request.setPhoneNumbers(telphone);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(smsConfig.getSignName());
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(smsConfig.getTemplateCode());
        // 可选:模板中的变量替换JSON串,如模板内容为"【企业级分布式应用服务】,您的验证码为${code}"时,此处的值为
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.createObjectNode();
        ((ObjectNode)rootNode).put("code",code);

        try {
            request.setTemplateParam(mapper.writeValueAsString(rootNode));
            smsService.sendSmsRequest(request);
        }
        catch (Exception e) {
            log.error("send sms error.",e);
        }
    }

}
