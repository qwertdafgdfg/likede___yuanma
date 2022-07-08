package com.lkd.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkd.entity.VendingMachineEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VendingMachineDao extends BaseMapper<VendingMachineEntity> {
    @Results(id = "vmMapper",value = {
            @Result(property = "vmType",column = "vm_type"),
            @Result(property = "nodeId",column = "node_id"),
            @Result(property = "type",column = "vm_type",one = @One(select = "com.lkd.dao.VmTypeDao.selectById")),
            @Result(property = "node",column = "node_id",one = @One(select = "com.lkd.dao.NodeDao.getById")),
            @Result(property = "regionId",column = "region_id"),
            @Result(property = "region",column = "region_id",one = @One(select = "com.lkd.dao.RegionDao.selectById"))
    })
    @Select("select * from tb_vending_machine where inner_code=#{innerCode} limit 1")
    VendingMachineEntity findByInnerCode(String innerCode);

    @Select("select IFNULL(COUNT(1),0) from tb_vending_machine where node_id=#{nodeId}")
    long getCountByNodeId(long nodeId);

    @ResultMap(value = "vmMapper")
    @Select("select * from tb_vending_machine order by id desc")
    Page<VendingMachineEntity> getAll(Page<VendingMachineEntity> page);

    @ResultMap(value = "vmMapper")
    @Select("select * from tb_vending_machine where vm_status=#{status} order by create_time desc")
    Page<VendingMachineEntity> getListByStatus(Page<VendingMachineEntity> page,@Param("status") int status);

    @ResultMap(value = "vmMapper")
    @Select("select * from tb_vending_machine ${ew.getCustomSqlSegment} order by create_time desc")
    List<VendingMachineEntity> query(@Param(Constants.WRAPPER) QueryWrapper<VendingMachineEntity> wrapper);
}
