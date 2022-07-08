package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class LoginReq{
    private String loginName;
    private String password;
    private String mobile;
    /**
     * 账号
     */
    private String account;
    /**
     *  验证码
     */
    private String code;
    /**
     * 客户端请求验证码的token
     */
    private String clientToken;
    /**
     * 登录类型 0：后台；1：运营运维端；2：合作商后台
     */
    private Integer loginType;
}
