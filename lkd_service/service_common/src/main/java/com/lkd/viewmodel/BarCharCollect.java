package com.lkd.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BarCharCollect implements Serializable {

    @JsonProperty("xAxis")
    private List<String> xAxis= Lists.newArrayList();  //x轴

    private List<Integer> series=Lists.newArrayList(); //数据

}
