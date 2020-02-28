package com.biubiu.map.aop;

import com.biubiu.map.annotation.CacheItemGet;
import com.biubiu.utils.MD5;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Aspect
@Component
public class CacheItemGetAspect {

    @Qualifier("hashRedisTemplate")
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(com.biubiu.map.annotation.CacheItemGet)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getMethod().getParameterTypes());
        CacheItemGet cacheItemGet = method.getAnnotation(CacheItemGet.class);
        String key = cacheItemGet.key();
        String hKeyEl = cacheItemGet.hKey();
        //创建解析器
        ExpressionParser parser = new SpelExpressionParser();
        Expression hKeyExpression = parser.parseExpression(hKeyEl);
        //设置解析上下文有哪些占位符。
        EvaluationContext context = new StandardEvaluationContext();
        //获取方法参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = new DefaultParameterNameDiscoverer().getParameterNames(method);
        for(int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        //解析得到 item 的 key
        String hKeyValue = hKeyExpression.getValue(context).toString();
        String hKey = MD5.getMD5Str(hKeyValue);
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        Object value = ops.get(key, hKey);
        if(value != null) {
            return value;
        }
        return joinPoint.proceed();
    }

}
