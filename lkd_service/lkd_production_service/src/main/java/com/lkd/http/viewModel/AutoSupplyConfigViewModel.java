package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class AutoSupplyConfigViewModel implements Serializable{
    /**
     * 预警值(百分比，如50代表，满量的百分之50为补货预警值)
     */
    private int alertValue;
}
