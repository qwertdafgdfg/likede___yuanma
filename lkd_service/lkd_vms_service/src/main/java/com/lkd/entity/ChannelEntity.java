package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 售货机货道实体类
 */
@Data
@TableName(value = "tb_channel",autoResultMap = true,resultMap = "channelMap")
public class ChannelEntity extends AbstractEntity implements Serializable{

    @TableId(value = "channel_id",type = IdType.AUTO)
    private Long channelId;//表Id
    @TableField(value = "channel_code")
    private String channelCode;//货道编号
    @TableField(value = "sku_id")
    private Long skuId;//商品Id
    /**
     * 货道商品最终售价
     */
    private Integer price;
    @TableField(value = "vm_id")
    private Long vmId;//售货机Id
    @TableField(value = "inner_code")
    private String innerCode;//售货机软编号
    @TableField(value = "max_capacity")
    private Integer maxCapacity;//货道最大容量
    @TableField(value = "current_capacity")
    private Integer currentCapacity;//货道当前容量
    @TableField(value = "last_supply_time")
    private LocalDateTime lastSupplyTime;//上次补货时间
    @TableField(exist = false)
    private SkuEntity sku;
}
