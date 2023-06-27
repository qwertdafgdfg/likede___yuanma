package com.lkd.utils;

import lombok.Data;

@Data
public class TokenObject{
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户Id
     */
    private Integer userId;
    /**职能*/
    private String roleCode;

    /**
     * 公司名称
     */
    private int companyId;
}
