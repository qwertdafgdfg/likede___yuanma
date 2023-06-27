package com.lkd.http.viewModel;

import lombok.Data;

import java.util.List;

@Data
public class PolicyReq{
    private List<String> innerCodeList;
    private int policyId;
}
