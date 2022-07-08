package com.lkd.business.converter;

import com.lkd.contract.Channel;
import com.lkd.entity.ChannelEntity;

/**
 * channel实体转换
 */
public class ChannelConverter{
    /**
     * 将实体转为业务对象
     * @param channelEntity
     * @return
     */
    public static Channel convert(ChannelEntity channelEntity){
        Channel channel = new Channel();
        channel.setCapacity(channelEntity.getMaxCapacity());
        channel.setChannelId(channelEntity.getChannelCode());
        channel.setSkuId(channelEntity.getSkuId());

        return channel;
    }
}
