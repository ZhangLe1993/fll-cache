package com.biubiu.map.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheItemPut {
    String key();
    String hKey();
}
