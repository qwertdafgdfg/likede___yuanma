package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "tb_job")
public class JobEntity{
    @TableId(value = "id",type = IdType.AUTO)
    private int id;
    /**
     * 警戒值百分比
     */
    @TableField(value="alert_value")
    private int alertValue;
}
