package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Data
@TableName(value = "tb_area")
public class AreaEntity implements Serializable{

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;//id
    @TableField(value = "parent_id")
    private Integer parentId;//父Id
    @TableField(value = "area_name")
    private String areaName;//区域名称
    @TableField(value = "ad_code")
    private String adCode;//地区编码
    @TableField(value = "city_code")
    private String cityCode;//城市区号
    @TableField(value = "area_level")
    private String areaLevel;//地区级别

}
