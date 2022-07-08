package com.lkd.client.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_vendout_order")
public class VendoutOrder {
    @TableId
    private String orderNo;
    private Integer payType;
    private Integer payPrice;
    private String channelId;
    private String skuId;
    private LocalDateTime outTime;
    private Integer resultCode;
    @TableField(exist = false)
    private boolean success;
}
