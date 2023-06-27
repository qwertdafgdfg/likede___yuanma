package com.lkd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "tb_sku",autoResultMap = true,resultMap = "skuMap")
public class SkuEntity extends AbstractEntity implements Serializable{
    @TableId(value = "sku_id",type = IdType.ASSIGN_ID)
    private Long skuId;//sku_id
    @TableField(value = "sku_name")
    private String skuName;//商品名称
    @TableField(value = "sku_image")
    private String skuImage;//商品图片
    @TableField(value = "price")
    private Integer price;//基础价格
    @TableField(value = "class_id")
    private Integer classId;//商品类别Id
    @TableField(value = "is_discount")
    private boolean discount;//是否打折促销
    @TableField(value = "unit")
    private String unit;//净含量
    /**
     * 商品品牌名称
     */
    private String brandName;
    @TableField(exist = false)
    private SkuClassEntity skuClass;//商品类别
    @TableField(exist = false)
    private Integer capacity;//余量

    /**
     * 商品真实售价
     */
    @TableField(exist = false)
    private Integer realPrice;
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        final SkuEntity sku = (SkuEntity) obj;
        if (this == sku) {
            return true;
        } else {
            return this.skuId.equals(sku.skuId);
        }
    }
    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (skuId == null ? 0 : skuId.hashCode());

        return hashno;
    }
}
