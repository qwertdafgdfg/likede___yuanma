package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class AuthViewModel{
    /**
     * 用户Id
     */
    private int userId;
    /**
     * 密码
     */
    private String password;
    /**
     * 密钥
     */
    private String secret;
}
