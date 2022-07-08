package com.lkd.http.viewModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class NodeReq implements Serializable{
    private String name;
    private String addr;
    private Integer areaId;
    private Integer createUserId;
    private String regionId;
    private Integer businessId;
    private Integer ownerId;
    private String ownerName;
}
