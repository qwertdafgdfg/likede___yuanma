package com.lkd.client.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_channel")
public class Channel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String channelId; //货道Id
    private String   skuId;  //商品id
    private Integer  capacity; //货道容量
}
