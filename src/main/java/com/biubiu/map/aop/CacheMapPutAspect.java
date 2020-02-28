package com.biubiu.map.aop;

import com.biubiu.map.annotation.CacheMapPut;
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
public class CacheMapPutAspect {

    @Qualifier("hashRedisTemplate")
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String AHS_AUTH_CACHE_UPDATE_KEY = "ahs:auth:cache:update:key";

    @Around("@annotation(com.biubiu.map.annotation.CacheMapPut)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getMethod().getParameterTypes());
        CacheMapPut cacheMap = method.getAnnotation(CacheMapPut.class);
        String key = cacheMap.key();

        if(cacheMap.sync()){//TODO

        }

        //强制刷缓存
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        Object val;
        //加锁
        synchronized (this) {
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
