DROP TABLE IF EXISTS tb_channel;

CREATE TABLE tb_channel
(
	channel_id VARCHAR(64) NOT NULL COMMENT '货道ID',
	sku_id VARCHAR(64) NOT NULL  COMMENT '商品id',
	capacity INT(11) NULL DEFAULT NULL COMMENT 'capacity',
	PRIMARY KEY (channel_id)
);

DROP TABLE IF EXISTS tb_sku;
CREATE TABLE tb_sku
(
	sku_id VARCHAR(64) NOT NULL COMMENT '商品ID',
	sku_name VARCHAR(64) NOT NULL  COMMENT '商品名称',
	image VARCHAR(255) NULL DEFAULT NULL COMMENT '图片地址',
	price INT(11) NULL DEFAULT NULL COMMENT '商品原价',
	real_price INT(11) NULL DEFAULT NULL COMMENT '商品真实售价',
	class_id VARCHAR(64) NULL DEFAULT NULL  COMMENT '商品类别Id',
	class_name VARCHAR(64) NULL DEFAULT NULL  COMMENT '商品类别Id',
    discount INT(1) NULL DEFAULT NULL COMMENT '是否打折',
    unit VARCHAR(32) NULL DEFAULT NULL  COMMENT '商品净含量',
    index INT(11) NULL DEFAULT NULL  COMMENT '商品排序索引',
	PRIMARY KEY (sku_id)
);

DROP TABLE IF EXISTS tb_version;
CREATE TABLE tb_version
(
	version_id INT(11) NOT NULL COMMENT '版本ID',
	channel_version INT(11) NOT NULL DEFAULT 0 COMMENT '货道版本',
	sku_version INT(11) NULL DEFAULT 0 COMMENT '商品版本',
	sku_price_version INT(11) NULL DEFAULT 0 COMMENT '商品价格版本',
	PRIMARY KEY (version_id)
);

INSERT INTO tb_version (version_id, channel_version, sku_version, sku_price_version) VALUES
(1, 0, 0, 0);


/**
出货信息表
 */
DROP TABLE IF EXISTS tb_vendout_order;
CREATE TABLE tb_vendout_order
(
	order_no VARCHAR(64) NOT NULL COMMENT '出货订单号',
	pay_type INT(11) NOT NULL DEFAULT 0 COMMENT '支付方式',
	channel_id VARCHAR(64)  NULL DEFAULT '' COMMENT '货道id',
	sku_id VARCHAR(64)  NULL DEFAULT '' COMMENT '商品id',
    pay_price INT(11)  NULL DEFAULT 0 COMMENT '价格',
	out_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '售货机出货时间',
	result_code INT(11) NULL DEFAULT 0 COMMENT '出货结果编号，0-成功,1-货道售空,2-设备故障,3-机器出货中,4-连续支付,5-服务器超时',
	PRIMARY KEY (order_no)
);