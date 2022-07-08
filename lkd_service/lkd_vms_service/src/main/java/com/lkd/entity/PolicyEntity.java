package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_policy")
public class PolicyEntity extends AbstractEntity implements Serializable{
    @TableId(value = "policy_id",type = IdType.AUTO)
    private Integer policyId;//策略id
    @TableField(value = "policy_name")
    private String policyName;//策略名称
    @TableField(value = "discount")
    private Integer discount;//折扣，如：80代表8折
}