package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VmStatusVM implements Serializable{
    private String innerCode;
    private String operaterName;
    private String time;
    private String address;
    private String vmTypeSesc;
    private List<StatusVm> statuses;
}
