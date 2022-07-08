package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "tb_order_month_collect")
public class OrderMonthCollectEntity{
    @TableId(value = "id",type = IdType.ID_WORKER)
    private long id;
    @TableField(value = "company_id")
    private int companyId;

    /**
     * 所属区域Id
     */
    @TableField(value = "area_id")
    private int areaId;
    @TableField(value = "order_total_money")
    private long orderTotalMoney;
    /**
     * 出货总量
     */
    @TableField(value = "vendout_total_count")
    private long vendoutTotalCount;
    /**
     * 所属月份
     */
    @TableField(value = "month")
    private int month;
    /**
     * 所属年份
     */
    @TableField(value = "year")
    private int year;
    /**
     * 公司名
     */
    @TableField(value = "company_name")
    private String companyName;
    /**
     * 所属区域名
     */
    @TableField(value = "area_name")
    private String areaName;
}
