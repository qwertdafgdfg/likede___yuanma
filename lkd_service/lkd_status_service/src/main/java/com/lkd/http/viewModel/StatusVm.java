package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatusVm implements Serializable{
    private String statusCode;
    private String desc;
    /**
     * true代表正常，false代表异常
     */
    private boolean status;
}
