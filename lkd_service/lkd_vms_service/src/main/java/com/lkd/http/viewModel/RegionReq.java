package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegionReq implements Serializable {
    private String regionId;
    private String regionName;
    private String remark;
}
