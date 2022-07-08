package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

@Data
@TableName(value = "tb_vm_cfg_version")
public class VmCfgVersionEntity implements Serializable{

    @TableId(value = "version_id",type = IdType.AUTO)
    private Long versionId;//id


	
    @TableField(value = "vm_id")
    private Long vmId;//售货机Id
    @TableField(value = "inner_code")
    private String innerCode;//售货机编号
    @TableField(value = "channel_cfg_version")
    private Long channelCfgVersion;//货道配置版本
    @TableField(value = "basecfg_version")
    private Long basecfgVersion;//基础配置版本
    @TableField(value = "sku_cfg_version")
    private Long skuCfgVersion;//商品配置版本
    @TableField(value = "price_cfg_version")
    private Long priceCfgVersion;//价格配置版本
    @TableField(value = "supply_version")
    private Long supplyVersion;//补货版本

}
