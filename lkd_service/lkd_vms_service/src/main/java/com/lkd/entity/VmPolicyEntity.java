package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Data
@TableName(value = "tb_vm_policy")
public class VmPolicyEntity extends AbstractEntity implements Serializable{
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;//id
    @TableField(value = "vm_id")
    private Long vmId;//售货机id
    @TableField(value = "inner_code")
    private String innerCode;//售货机编号
    @TableField(value = "policy_id")
    private Integer policyId;//策略id
    @TableField(value = "policy_name")
    private String policyName;//策略名称
    @TableField(value = "discount")
    private Integer discount;//折扣
}
