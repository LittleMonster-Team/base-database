package com.fly.cloud.database.common.util;

import com.google.gson.Gson;
import org.apache.poi.ss.formula.functions.T;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.Arrays;

/**
 * JSON工具类
 *
 * @description: JSON工具类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class JsonUtils {
    /**
     * json转List
     *
     * @param json  json字符串
     * @param clazz 类
     * @param <T>
     * @return
     */
    public static <T> java.util.List<T> jsonStrToList(String json, Class<T[]> clazz) {
        try {
            T[] arr = new Gson().fromJson(json, clazz);
            return Arrays.asList(arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obj转实体类
     *
     * @param obj   Obj
     * @param clazz 类
     * @return
     */
    public static <T> T ObjToEntity(Object obj, Class<T> clazz) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            T t = objectMapper.convertValue(obj, clazz);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
