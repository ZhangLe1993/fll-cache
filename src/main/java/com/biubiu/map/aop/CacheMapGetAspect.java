package com.biubiu.map.aop;

import com.biubiu.map.annotation.CacheMapGet;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CacheMapGetAspect {
    @Qualifier("hashRedisTemplate")
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.biubiu.map.annotation.CacheMapGet)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getMethod().getParameterTypes());
        CacheMapGet cacheMap = method.getAnnotation(CacheMapGet.class);
        String key = cacheMap.key();
        //强制刷缓存
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        Object val;
        Map<String, Object> value = ops.entries(key);
        if(value.size() != 0) {
            return value;
        }
        //加锁
        synchronized (this) {
            value = ops.entries(key);
            if(value.size() != 0) {
                return value;
            }
            //执行目标方法
            val = joinPoint.proceed();
            redisTemplate.delete(key);
            //把值设置回去
            ops.putAll(key, (Map<String, Object>) val);
            redisTemplate.expire(key, cacheMap.expire(), TimeUnit.SECONDS);
            return val;
        }
    }

}
