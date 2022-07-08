package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Data
@TableName(value = "tb_status_type")
public class StatusTypeEntity implements Serializable{

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;//id


	
    @TableField(value = "status_code")
    private String statusCode;//状态代码
    @TableField(value = "descr")
    private String descr;//状态描述

}
