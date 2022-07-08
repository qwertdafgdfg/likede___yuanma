package com.lkd.viewmodel;

import lombok.Data;

import java.io.Serializable;
@Data
public class VmSearch  implements Serializable {

    private Double lat;//纬度

    private Double lon;//经度

    private Integer distance;//搜索半径


}
