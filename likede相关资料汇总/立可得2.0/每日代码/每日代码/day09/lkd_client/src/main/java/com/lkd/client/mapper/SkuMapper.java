package com.lkd.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkd.client.pojo.Sku;
import org.apache.ibatis.annotations.Update;

/**
 * 商品mapper
 */
public interface SkuMapper extends BaseMapper<Sku> {

    @Update("truncate table tb_sku")
    void deleteAllSkus();
}
