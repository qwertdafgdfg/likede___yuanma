package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.contract.SupplyCfg;
import com.lkd.contract.VendoutResp;
import com.lkd.entity.ChannelEntity;
import com.lkd.entity.SkuEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.http.viewModel.CreateVMReq;
import com.lkd.viewmodel.*;

import java.util.List;

public interface VendingMachineService extends IService<VendingMachineEntity> {
    /**
     * 根据售货机编号查找
     * @param innerCode
     * @return
     */
    VendingMachineEntity findByInnerCode(String innerCode);

    /**
     * 新增
     * @param vendingMachine
     * @return
     */
    boolean add(CreateVMReq vendingMachine);

    /**
     * 修改售货机
     * @param id
     * @param nodeId
     * @return
     */
    boolean update(Long id,Long nodeId);

    /**
     * 获取售货机所有货道
     * @param innerCode
     * @return
     */
    List<ChannelEntity> getAllChannel(String innerCode);

    /**
     * 获取售货机里所有商品
     * @param innerCode
     * @return
     */
    List<SkuViewModel> getSkuList(String innerCode);

    /**
     * 获取售货机商品信息
     * @param innerCode
     * @param
     * @return
     */
    SkuEntity getSku(String innerCode,long skuId);


    /**
     * 补货
     * @param supply
     * @return
     */
    boolean supply(SupplyCfg supply);

    /**
     * 更新机器状态
     * @param innerCode
     * @param status
     * @return
     */
    boolean updateStatus(String innerCode,int status);

    /**
     * 出货结果处理
     * @param vendoutResp
     * @return
     */
    boolean vendOutResult(VendoutResp vendoutResp);

    /**
     * 根据机器状态获取机器编号列表
     * @param isRunning
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pager<String> getAllInnerCodes(boolean isRunning, long pageIndex, long pageSize);

    /**
     * 根据状态获取售货机列表
     * @param status
     * @return
     */
    Pager<VendingMachineEntity> query(Long pageIndex, Long pageSize, Integer status,String innerCode);


    /**
     * 获取合作商下设备数量
     * @param ownerId
     * @return
     */
    Integer getCountByOwnerId(Integer ownerId);



    /**
     * 缺货货道数
     * @param percent
     * @param vmEntity
     * @return
     */
    int inventory( int percent, VendingMachineEntity vmEntity );


    /**
     * 发送补货工单
     * @param vmEntity
     */
    void sendSupplyTask( VendingMachineEntity vmEntity );



    /**
     * 商品是否还有余量
     * @param skuId
     * @return
     */
    Boolean hasCapacity(String innerCode,Long skuId);


    /**
     * 设置售货机位置信息
     * @param vmDistance
     * @return
     */
    Boolean setVMDistance( VMDistance vmDistance );


    /**
     * 附近售货机搜索
     * @param vmSearch
     * @return
     */
    List<VmInfoDTO> search(VmSearch vmSearch);


}
