package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Data
@TableName(value = "tb_vm_type")
public class VmTypeEntity implements Serializable{
    @TableId(value = "type_id",type = IdType.AUTO)
    private Integer typeId;//type_id
    @TableField(value = "vm_row")
    private Integer vmRow;//行数
    @TableField(value = "vm_col")
    private Integer vmCol;//列数
    @TableField(value = "name")
    private String name;//类型名
    @TableField(value = "channel_max_capacity")
    private Integer channelMaxCapacity;//货道最大容量
    /**
     * 售货机型号编码
     */
    private String model;
    /**
     * 型号图片
     */
    private  String image;
}
