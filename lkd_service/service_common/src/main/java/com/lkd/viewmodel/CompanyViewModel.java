package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyViewModel implements Serializable{
    /**
     * 公司id
     */
    private int companyId;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 分成比例
     */
    private int divide;
    /**
     * 是否启用，0：不启用；1：启用
     */
    private int enable;
}
