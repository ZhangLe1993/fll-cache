package com.biubiu.map.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheMapPut {
    String key();
    long expire() default 12 * 60 * 60L;
    boolean parse() default false;
    boolean sync() default false;
}
