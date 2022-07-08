package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.NodeEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.exception.LogicException;
import com.lkd.viewmodel.Pager;

import java.util.List;

public interface NodeService extends IService<NodeEntity> {

    /**
     * 新增
     * @param node
     * @return
     */
    boolean add(NodeEntity node) throws LogicException;

    /**
     * 修改
     * @param nodeEntity
     * @return
     */
    boolean update(NodeEntity nodeEntity) throws LogicException;

    /**
     * 删除
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 搜索点位
     * @param name
     * @param regionId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pager<NodeEntity> search(String name, String regionId, long pageIndex, long pageSize);

    /**
     * 获取点位下所有售货机
     * @param id
     * @return
     */
    List<VendingMachineEntity> getVmList(long id);

    /**
     * 获取合作商的点位数
     * @param ownerId
     * @return
     */
    Integer getCountByOwner(Integer ownerId);
}
