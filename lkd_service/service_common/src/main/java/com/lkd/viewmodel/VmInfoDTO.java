package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;
@Data
public class VmInfoDTO implements Serializable {


    private String location;//坐标

    private String innerCode;//售货机编号

    private String nodeName;//点位名称

    private String addr;//地址

    private Integer distance;//半径

    private String typeName;//售货机类型


}
