package com.lkd.client.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_version")
public class Version {

    @TableId(type = IdType.ASSIGN_UUID)
    private String  versionId; //版本id
    private Integer channelVersion; //当前货道版本
    private Integer skuVersion; //商品版本
    private Integer skuPriceVersion; //商品价格版本
}
