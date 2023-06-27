package com.lkd.viewmodel;

import lombok.Data;

@Data
public class UserWork {


    private Integer userId;//用户id

    private String userName;//用户名称

    private String roleName;//角色名称

    private String mobile;//手机号

    private Integer workCount;//完成工单数

    private Integer progressTotal;//进行中工单数

    private Integer cancelCount;//取消工单数

    private Integer total;//工单总数


}
