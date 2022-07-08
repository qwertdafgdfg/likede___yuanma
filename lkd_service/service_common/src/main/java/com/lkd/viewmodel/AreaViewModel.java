package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaViewModel implements Serializable{
    private Integer id;//id
    private Integer parentId;//父Id
    private String areaName;//区域名称
    private String adCode;//地区编码
    private String cityCode;//城市区号
    private String areaLevel;//地区级别
}
