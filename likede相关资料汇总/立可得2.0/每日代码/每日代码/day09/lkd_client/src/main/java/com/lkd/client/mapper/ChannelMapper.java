package com.lkd.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkd.client.pojo.Channel;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 货道mapper
 */
public interface ChannelMapper extends BaseMapper<Channel> {
    @Update("truncate table tb_channel")
    void deleteAllChannels();

}
