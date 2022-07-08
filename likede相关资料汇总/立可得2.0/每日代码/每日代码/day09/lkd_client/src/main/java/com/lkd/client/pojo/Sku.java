package com.lkd.client.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_sku")
public class Sku {
    @TableId(type = IdType.ASSIGN_UUID)
    private String skuId;
    private String skuName;
    private String image;
    private Integer price;  //当前售价(以分为单位)
    private Integer realPrice; //商品原价(以分为单位)
    private String classId; //商品类别Id
    private String className; //类别名称
    private Boolean discount; //是否打折
    private String unit; //商品净含量
    private Integer index;  //商品排序索引
}
