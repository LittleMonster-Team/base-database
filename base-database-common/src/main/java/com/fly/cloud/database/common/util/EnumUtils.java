package com.fly.cloud.database.common.util;


import com.fly.cloud.database.common.enums.CodeEnum;

/**
 * 枚举工具类
 *
 * @description: 枚举工具类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class EnumUtils {
    /**
     * 获取枚举
     *
     * @param code      编号
     * @param enumClass 类
     * @param <T>
     * @return
     */
    public static <T extends CodeEnum> T getEnumByType(Integer code, Class<T> enumClass) {
        for (T t : enumClass.getEnumConstants()) {
            if (t.getCode().equals(code)) {
                return t;
            }
        }
        return null;
    }
}
