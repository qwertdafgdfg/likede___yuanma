package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value = "tb_vm_status_info")
public class VmStatusInfoEntity implements Serializable{
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;//id
    @TableField(value = "inner_code")
    private String innerCode;//售货机编号
    @TableField(value = "status_code")
    private String statusCode;//售货机状态码
    @TableField(value = "status")
    private Boolean status;//状态
    @TableField(value = "utime")
    private LocalDateTime utime;//发生时间
}
