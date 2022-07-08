package com.lkd.http.viewModel;

import lombok.Data;

@Data
public class LoginResp{
    private Integer userId;
    private String userName;
    private String roleCode;
    private String token; //jwt令牌
    private boolean success;
    private String regionId;
    private String msg;
    /**
     * 是否是运维人员
     */
    private boolean isRepair;
}
