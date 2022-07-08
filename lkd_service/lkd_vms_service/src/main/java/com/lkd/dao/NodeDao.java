package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lkd.entity.NodeEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NodeDao extends BaseMapper<NodeEntity> {
    @Results(id = "nodeMap",value = {
            @Result(property = "id",column = "id"),
            @Result(property = "areaId",column = "area_id"),
            @Result(property = "area",column = "area_id",one=@One(select = "com.lkd.dao.AreaDao.selectById")),
            @Result(property = "vmCount",column = "id",many = @Many(select = "com.lkd.dao.VendingMachineDao.getCountByNodeId")),
            @Result(property = "regionId",column = "region_id"),
            @Result(property = "region",column = "region_id",one = @One(select = "com.lkd.dao.RegionDao.selectById")),
            @Result(property = "businessId",column = "business_id"),
            @Result(property = "businessType",column = "business_id",one = @One(select = "com.lkd.dao.BusinessTypeDao.selectById"))
    })
    @Select("select * from tb_node where `name` like CONCAT('%',#{name},'%')")
    Page<NodeEntity> searchByName(Page<NodeEntity> page,String name);


    @Select("<script>" +
            "select * from tb_node where area_id in " +
            "<foreach collection='areaIds' item='item' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>"
            +"</script>")
    @ResultMap(value = "nodeMap")
    Page<NodeEntity> getNodesByAreas(Page<NodeEntity> page,@Param("areaIds") Integer[] areaIds);

    @Select("select * from tb_node where id=#{nodeId} limit 1")
    @ResultMap(value = "nodeMap")
    NodeEntity getById(long nodeId);

    @Select("select IFNULL(COUNT(1),0) from tb_node where region_id=#{regionId} ")
    Integer getCountByRegion(Long regionId);

    @Select("select * from tb_node")
    @ResultMap(value = "nodeMap")
    Page<NodeEntity> getAll(Page<NodeEntity> page);
}
