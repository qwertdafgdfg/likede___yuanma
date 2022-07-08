package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * 区域
 */
@Data
@TableName(value = "tb_region",autoResultMap = true,resultMap = "regionMap")
public class RegionEntity {
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private String remark;
    @TableField(exist = false)
    private Integer nodeCount;
    @TableField(exist = false)
    private List<NodeEntity> nodeList;
}
