package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.VmStatusInfoEntity;
import com.lkd.http.viewModel.VmStatusVM;
import com.lkd.viewmodel.Pager;

import java.io.IOException;

public interface VmStatusInfoService extends IService<VmStatusInfoEntity> {
    Pager<VmStatusVM> getAll(long pageIndex, long pageSize);
    void setVmStatus(String innerCode,String statusCode,boolean status) throws IOException;
    VmStatusVM getVMStatus(String innerCode);
    boolean isOnline(String innerCode);
    Pager<VmStatusVM> getAllTrouble(long pageIndex, long pageSize);
}
