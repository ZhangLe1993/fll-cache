package com.biubiu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.ClassUtils;

import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

    private static Pattern p = Pattern.compile("\\$(.+?)\\$");

    /**
     * @param source
     * @param <T>
     * @return
     */
    public static <T> List<T> copyUtil(List<T> source) {
        List<T> target = new ArrayList<>(source.size());
        CollectionUtils.addAll(target, new Object[source.size()]);
        Collections.copy(target, source);
        return target;
    }

    /**
     * 将达芬奇式的 $xxx$ 变量语法替换成JSP语法
     *
     * @return
     */
    public static String replace2jsp(String script) {
        Matcher m = p.matcher(script);
        if (m.find()) {
            return m.replaceAll("<%=$1%>");
        } else {
            return script;
        }
    }

    public static final boolean isJson(String data) {
        return checkIsJson(data);
    }

    private static boolean checkIsJson(String data) {
        if(null == data) {
            return false;
        }
        try {
            JSONObject.parseArray(data);
        } catch (JSONException e) {
            try {
                JSONObject.parseObject(data);
            } catch (JSONException je) {
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Java对象序列化为JSON字符串
     *
     * @param obj Java对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 判断是否是简单值类型.包括：基础数据类型、CharSequence、Number、Date、URL、URI、Locale、Class;
     *
     * @param clazz
     * @return
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || URI.class == clazz
                || URL.class == clazz || Locale.class == clazz || Class.class == clazz);
    }

    public static List<Integer> longToInt(List<Long> inList) {
        List<Integer> iList = new ArrayList<Integer>(inList.size());
        CollectionUtils.collect(inList, input -> new Integer( (String) input), iList);
        return iList;
    }




}
