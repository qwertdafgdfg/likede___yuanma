package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@TableName(value = "tb_order_collect")
public class OrderCollectEntity implements Serializable{
    @TableId(value = "id",type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 合作商Id
     */
    private int ownerId;
    /**
     * 合作商名称
     */
    private String ownerName;
    /**
     * 点位Id
     */
    private Long nodeId;
    /**
     * 点位名
     */
    private String nodeName;
    /**
     * 当日分账金额
     */
    private int totalBill;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 当日订单收入金额(平台端总数)
     */
    private int orderTotalMoney;
    /**
     * 统计日期
     */
    private LocalDate date;
    /**
     * 分成比例
     */
    private Integer ratio;
}
