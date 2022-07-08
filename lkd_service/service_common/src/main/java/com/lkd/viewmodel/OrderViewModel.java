package com.lkd.viewmodel;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderViewModel implements Serializable {
    private Long id;//id
    private String orderNo;//订单编号
    private String thirdNo;//支付流水号
    private String innerCode;//机器编号
    /**
     * 点位地址
     */
    private String addr;
    private int areaId;
    private Long skuId;//skuId
    private String skuName;//商品名称
    /**
     * 商品类别Id
     */
    private Integer classId;
    private Integer status;//订单状态:0-创建;1-支付完成;2-出货成功;3-出货失败;
    private Integer amount;//支付金额
    private Integer bill;//分账金额
    private Integer price;//商品金额
    private String payType;//支付类型，1支付宝 2微信
    private Integer payStatus;//支付状态，0-未支付;1-支付完成;2-退款中;3-退款完成
    /**
     * 所属区域Id
     */
    private Long regionId;
    /**
     * 所属区域名称
     */
    private String regionName;
    /**
     * 所属商圈Id
     */
    private Integer businessId;
    /**
     * 所属商圈名称
     */
    private String businessName;
    /**
     * 合作商Id
     */
    private Integer ownerId;
    /**
     * 微信用户openId
     */
    private String openId;
    /**
     * 取消原因
     */
    private String cancelDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
