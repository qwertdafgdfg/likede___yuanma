package com.lkd.redis;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
    private static Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    @Autowired private RedisTemplate<String, Object> redisTemplate;
    private int defauleExpireSeconds = 60;

    /**
     * 指定缓存失效时间
     *
     * @param key 键
     * @param seconds 时间(秒)
     * @return
     */
    public boolean expire(String key, long seconds) {
        if (seconds <= 0) {
            return false;
        }
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);

        return true;
    }

    /**
     * 判断是否存在
     * @param key
     * @return
     */
    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断集合是否有值
     * @param key
     * @return
     */
    public boolean listHasValue(String key){
        long size = redisTemplate.opsForList().size(key);

        return size>0;
    }

    public boolean hasKey(String key) {

        return redisTemplate.hasKey(key);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

    public void removeAll(String... keys) {
        if (keys == null || keys.length <= 0) {
            return;
        }
        redisTemplate.delete(Lists.newArrayList(keys));
    }

    public <T> T get(String key, Class<T> T) {
        if (key == null) {
            return null;
        }
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        return (T) value;
        // return Primitives.wrap(T).cast(value);
    }

    public void set(String key, Object value) {
        this.set(key, value, defauleExpireSeconds);
    }

    /**
     * 向缓存里存入数据
     * @param key
     * @param value
     * @param expireSeconds 缓存项失效的秒数
     */
    public void set(String key, Object value, int expireSeconds) {
        redisTemplate.opsForValue().set(key, value, expireSeconds, TimeUnit.SECONDS);
    }

    public <T> T hget(String key, String hash) {
        if (key == null || hash == null) {
            return null;
        }
        Object value = redisTemplate.opsForHash().get(key, hash);
        if (value == null) {
            return null;
        }

        return (T) value;
    }

    public void hset(String key, String hash, Object value) {
        redisTemplate.opsForHash().put(key, hash, value);
    }

    public void hdel(String key, String hash) {
        redisTemplate.opsForHash().delete(key, hash);
    }

    public boolean hHasKey(String key, String hash) {
        return redisTemplate.opsForHash().hasKey(key, hash);
    }

    /**
     * 向list左侧队尾push数据
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void lPush(String key, T value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public <T> T getLast(String key,Class<T> clazz){
        long size = redisTemplate.opsForList().size(key);
        if (size <= 0) {
            return null;
        }
        return (T) redisTemplate.opsForList().index(key,size-1);
    }

    /**
     * 从list右侧弹出数据
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T rPop(String key, Class<T> clazz) {
        long size = redisTemplate.opsForList().size(key);
        if (size <= 0) {
            return null;
        }
        return (T) redisTemplate.opsForList().rightPop(key);
    }
}