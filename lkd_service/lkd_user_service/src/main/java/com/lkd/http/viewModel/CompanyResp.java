package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyResp implements Serializable{
    private Integer id;//id
    private Integer businessType;//0:平台方;1:加盟客户
    private String name;//合作商名称
    private int divide; //合作商分成
    private int enable; //是否启用
    private String userName;
    private String mobile;
    private int vmCount; //售货机数量
}
