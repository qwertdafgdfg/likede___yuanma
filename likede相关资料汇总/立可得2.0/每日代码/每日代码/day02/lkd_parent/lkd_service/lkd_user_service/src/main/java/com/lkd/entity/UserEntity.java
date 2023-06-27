package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Data
@TableName(value = "tb_user",autoResultMap = true,resultMap = "userMap")
public class UserEntity implements Serializable{
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;//id
    @TableField(value = "role_id")
    private Integer roleId;//角色Id
    @TableField(value = "role_code")
    private String roleCode;//角色编号
    @TableField(value = "user_name")
    private String userName;//用户名称
    @TableField(value = "login_name")
    private String loginName;//登录名
    @TableField(value = "password")
    private String password;//密码
    @TableField(value = "secret")
    private String secret;//私钥

    @TableField(value = "mobile")
    private String mobile;//手机号
    /**
     * 所属区域Id
     */
    private Long regionId;
    /**
     * 所属区域名称
     */
    private String regionName;
    /**
     * 头像地址
     */
    private String image;
    /**
     * 是否启用
     */
    private Boolean status;
    @TableField(exist = false)
    private RoleEntity role;
}
