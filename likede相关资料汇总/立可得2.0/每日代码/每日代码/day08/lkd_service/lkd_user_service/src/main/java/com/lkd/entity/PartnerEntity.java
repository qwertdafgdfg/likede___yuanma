package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_partner")
public class PartnerEntity extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = -2609242735884103559L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 合作商名称
     */
    private String name;
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 联系人
     */
    private String contact;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 联系电话
     */
    private String phone;
    private String email;
    /**
     * 城市
     */
    private String city;
    /**
     * 区县
     */
    private String county;
    /**
     * 所在省
     */
    private String province;
    /**
     * 详细地址
     */
    private String addr;
    /**
     * 分成比例
     */
    private Integer ratio;
    /**
     * 状态
     */
    private Boolean status;

    /**
     * 设备数量
     */
    @TableField(exist = false)
    private Integer vmCount;
}
