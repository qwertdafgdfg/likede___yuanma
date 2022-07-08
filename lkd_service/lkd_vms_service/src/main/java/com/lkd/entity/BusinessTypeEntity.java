package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商圈类型
 */
@Data
@TableName(value = "tb_business")
public class BusinessTypeEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * 商圈名称
     */
    private String name;
}
