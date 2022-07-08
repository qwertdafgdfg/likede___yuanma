package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * 工单类型
 */
@Data
@TableName(value = "tb_task_type")
public class TaskTypeEntity implements Serializable{

    @TableId(value = "type_id",type = IdType.AUTO)
    private Integer typeId;
    /**
     * 工单名称
     */
    @TableField(value = "type_name")
    private String typeName;

    /**
     * 工单类型  1：运维工单；2：运营工单
     */
    private Integer type;
}
