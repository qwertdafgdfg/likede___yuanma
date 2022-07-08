package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value = "tb_vending_machine",autoResultMap = true,resultMap = "vmMapper")
public class VendingMachineEntity extends AbstractEntity implements Serializable{
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;//id
    @TableField(value = "vm_type")
    private Integer vmType;//售货机类型
    @TableField(value = "inner_code")
    private String innerCode;//售货机软编号
    @TableField(value = "node_id")
    private Long nodeId;//点位Id
    @TableField(value = "vm_status")
    private Integer vmStatus;//售货机状态，0:未投放;1-运营;3-撤机
    @TableField(value = "last_supply_time")
    private LocalDateTime lastSupplyTime;//上次补货时间
    @TableField(value = "city_code")
    private String cityCode;//所在城市ID
    @TableField(value = "area_id")
    private Integer areaId;//区域id
    @TableField(value = "create_user_id")
    private Long createUserId;//创建人id
    @TableField(value = "create_user_name")
    private String createUserName;
    /**
     * 客户端连接唯一Id
     */
    @TableField(value = "client_id")
    private String clientId;

    @TableField(value = "longitudes")
    private Double longitudes;//经度
    @TableField(value = "latitude")
    private Double latitude;//纬度
    /**
     * 点位主Id
     */
    @TableField(value = "owner_id")
    private Integer ownerId;
    /**
     * 点位主名称
     */
    @TableField(value = "owner_name")
    private String ownerName;
    /**
     * 商圈Id
     */
    @TableField(value = "business_id")
    private Integer businessId;
    /**
     * 所属区域Id
     */
    @TableField(value = "region_id")
    private Long regionId;
    @TableField(exist = false)
    private VmTypeEntity type;
    @TableField(exist = false)
    private NodeEntity node;
    /**
     * 设备区域
     */
    @TableField(exist = false)
    private RegionEntity region;
}
