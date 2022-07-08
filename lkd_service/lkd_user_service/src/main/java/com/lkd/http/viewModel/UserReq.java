package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserReq implements Serializable {
    private String userName;
    private Integer roleId;
    private String mobile;
    private String regionId;
    private String regionName;
    private Boolean status;
    private String image;
}
