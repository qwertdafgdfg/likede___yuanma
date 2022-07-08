package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 工单汇总
 */
@Data
@TableName(value = "tb_task_collect")
public class TaskCollectEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     * 完成数
     */
    private Integer finishCount;
    /**
     * 进行中工单数
     */
    private Integer progressCount;
    /**
     * 取消的工单数
     */
    private Integer cancelCount;
    /**
     * 发生日期
     */
    private LocalDate collectDate;
}
