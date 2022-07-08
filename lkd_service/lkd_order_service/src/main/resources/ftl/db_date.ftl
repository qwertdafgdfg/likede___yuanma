<#list sqlbean.orderMonthList as month>
    create table if not exists lkd_order.tb_order_${month}
    (
    `id` bigint not null
    primary key,
    `order_no` varchar(34) not null comment '订单编号',
    `third_no` varchar(34) null comment '支付流水号',
    `inner_code` varchar(15) null comment '机器编号',
    `area_id` int null comment '区域Id',
    `company_id` int null comment '所属公司',
    `sku_id` bigint null comment 'skuId',
    `sku_name` varchar(20) null comment '商品名称',
    `status` int null comment '订单状态:0-创建;1-支付完成;2-出货成功;3-出货失败;',
    `ctime` datetime null comment '创建时间',
    `utime` datetime null comment '更新时间',
    `amount` int default '0' not null comment '支付金额',
    `price` int default '0' not null comment '商品金额',
    `pay_type` varchar(1) null comment '支付类型，1支付宝 2微信',
    `pay_status` int default '0' null comment '支付状态，0-未支付;1-支付完成;2-退款中;3-退款完成',
    `bill` int default '0' null comment '合作商账单金额',
    constraint Order_OrderNo_uindex
    unique (`order_no`),
    constraint order_ThirdNo_uindex
    unique (`third_no`)
    )
    ;

</#list>