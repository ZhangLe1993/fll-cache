package com.biubiu.map.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheMapGet {
    String key();
    long expire() default 12 * 60 * 60L;
    boolean parse() default false;
}
