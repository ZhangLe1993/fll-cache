package com.biubiu.map.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheItemGet {
    String key();
    String hKey();
}
