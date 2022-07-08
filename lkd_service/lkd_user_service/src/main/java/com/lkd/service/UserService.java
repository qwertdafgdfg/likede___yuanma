package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.UserEntity;
import com.lkd.http.viewModel.LoginReq;
import com.lkd.http.viewModel.LoginResp;
import com.lkd.viewmodel.Pager;
import com.lkd.viewmodel.UserViewModel;
import com.lkd.viewmodel.UserWork;

import java.io.IOException;
import java.util.List;

public interface UserService extends IService<UserEntity> {
    /**
     * 获取所有运营人员数量
     */
    Integer getOperatorCount();

    /**
     * 获取所有维修员数量
     * @return
     */
    Integer getRepairerCount();

    /**
     * 分页查询
     * @param pageIndex
     * @param pageSize
     * @param userName
     * @return
     */
    Pager<UserEntity> findPage(long pageIndex, long pageSize, String userName,Integer roleId,Boolean isRepair);

    /**
     * 后台登录
     * @param req
     * @return
     */
    LoginResp login(LoginReq req) throws IOException;

    /**
     * 发送验证码
     * @param mobile
     */
    void sendCode(String mobile);

    /**
     * 获取某区域下所有运营人员
     * @param regionId
     * @return
     */
    List<UserViewModel> getOperatorList(Long regionId);

    /**
     * 获取某区域下所有运维人员
     * @param regionId
     * @return
     */
    List<UserViewModel> getRepairerList(Long regionId);

    /**
     * 获取某区域下维修员/运营员总数
     * @param isRepair
     * @return
     */
    Integer getCountByRegion(Long regionId,Boolean isRepair);


    /**
     * 查询工作量列表
     * @param pageIndex
     * @param pageSize
     * @param userName
     * @param roleId
     * @param isRepair
     * @return
     */
    Pager<UserWork> searchUserWork( Long pageIndex,Long pageSize,String userName, Integer roleId, Boolean isRepair  );

}
