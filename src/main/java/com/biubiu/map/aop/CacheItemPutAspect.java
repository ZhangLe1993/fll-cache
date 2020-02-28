package com.biubiu.map.aop;

import com.biubiu.map.annotation.CacheItemPut;
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

/**
 * @author yule.zhang
 * @date 2020/2/27 19:53
 * @email zhangyule1993@sina.com
 * @description put一个元素到map中
 */

@Aspect
@Component
public class CacheItemPutAspect {

    @Qualifier("hashRedisTemplate")
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Around("@annotation(com.biubiu.map.annotation.CacheItemPut)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = joinPoint.getTarget().getClass().getMethod(signature.getName(), signature.getMethod().getParameterTypes());
        CacheItemPut cacheItemPut = method.getAnnotation(CacheItemPut.class);
        String key = cacheItemPut.key();
        String hKeyEl = cacheItemPut.hKey();
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
        //强制刷缓存
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        Object val;
        //加锁
        synchronized (this) {
            //执行目标方法
            val = joinPoint.proceed();
            //把值设置回去
            ops.put(key, hKey, val);
            return val;
        }
    }

}
