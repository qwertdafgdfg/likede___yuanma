package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;

@Data
public class VMDistance implements Serializable {


    private String innerCode;//售货机编号

    private String addr;//地址 （不需前端传入）

    private String nodeName;//点位 （不需前端传入）

    private Double lat;//纬度

    private Double lon;//经度


}
