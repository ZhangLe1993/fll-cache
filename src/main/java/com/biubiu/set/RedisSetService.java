package com.biubiu.set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author yule.zhang
 * @date 2020/2/13 14:29
 * @email zhangyule1993@sina.com
 * @description 缓存
 */

@Service
public class RedisSetService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 添加元素
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }


    /**
     * 移除元素
     * @param key
     * @param values
     */
    public void remove(String key, Object ... values) {
        if(values == null) {
            return;
        }
        redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取集合
     * @param key
     * @return
     */
    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 是否包含元素
     * @param key
     * @param value
     * @return
     */
    public boolean contains(String key, String value) {
        Boolean res = redisTemplate.opsForSet().isMember(key, value);
        if(res == null) {
            return false;
        }
        return res;
    }

    /**
     * 删除缓存
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除
     * @param keys
     */
    public void delete(Set<String> keys) {
        redisTemplate.delete(keys);
    }

}
