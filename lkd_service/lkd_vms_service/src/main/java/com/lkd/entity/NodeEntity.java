package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_node",autoResultMap = true,resultMap = "nodeMap")
public class NodeEntity extends AbstractEntity implements Serializable{
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;//id
    @TableField(value = "name")
    private String name;//点位名称
    @TableField(value = "addr")
    private String addr;//点位详细地址
    @TableField(value = "area_id")
    private Integer areaId;//所在区域Id
    @TableField(value = "create_user_id")
    private Integer createUserId;//创建人id
    /**
     * 点位主Id
     */
    private Integer ownerId;
    /**
     * 点位主名称
     */
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
    private AreaEntity area;
    @TableField(exist = false)
    private long vmCount;
    @TableField(exist = false)
    private RegionEntity region;
    @TableField(exist = false)
    private BusinessTypeEntity businessType;
}
