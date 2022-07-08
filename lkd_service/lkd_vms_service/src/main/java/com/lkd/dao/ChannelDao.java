package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkd.entity.ChannelEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChannelDao extends BaseMapper<ChannelEntity> {
    @Select("select * from tb_channel where inner_code=#{innerCode}")
    @Results(id="channelMap",value = {
            @Result(property = "channelId",column = "channel_id"),
            @Result(property = "skuId",column = "sku_id"),
            @Result(property = "sku",column = "sku_id",one = @One(select = "com.lkd.dao.SkuDao.getById"))
    })
    List<ChannelEntity> getChannelsByInnerCode(String innerCode);

    @Select("select * from tb_channel where channel_code=#{channelCode} and inner_code=#{innerCode}")
    @ResultMap(value = "channelMap")
    ChannelEntity getChannelByCode(@Param("innerCode") String innerCode,@Param("channelCode") String channelCode);
}
