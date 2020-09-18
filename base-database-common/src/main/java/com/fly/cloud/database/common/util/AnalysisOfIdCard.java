package com.fly.cloud.database.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析身份证信息
 *
 * @description: 解析身份证信息
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class AnalysisOfIdCard {
    /**
     * 根据身份证获取地区
     *
     * @param idNum 身份证
     * @return 结果 地区
     */
    public static Map<String, Object> getNativePlace(String idNum) {
        int nativePlaceCode = Integer.parseInt(idNum.substring(0, 6));
        Map<String, Object> map = new HashMap<String, Object>();
        int provinceCode = nativePlaceCode / 10000;
        int cityCode = nativePlaceCode / 100;
        int countyCode = nativePlaceCode;
        String province = "";
        String city = "";
        String county = "";
        if (StringUtils.isNotBlank(NativePlace.getProvinceName(provinceCode)) && NativePlace.getProvinceName(provinceCode) != null) {
            province = NativePlace.getProvinceName(provinceCode);
        }
        if (StringUtils.isNotBlank(NativePlace.getCityName(cityCode)) && NativePlace.getCityName(cityCode) != null) {
            city = NativePlace.getCityName(cityCode);
        }
        if (StringUtils.isNotBlank(NativePlace.getCountyName(countyCode)) && NativePlace.getCountyName(countyCode) != null) {
            county = NativePlace.getCountyName(countyCode);
        }
        map.put("province", province);
        map.put("city", city);
        map.put("county", county);
        return map;
    }


    /**
     * 根据身份证号码判断用户性别
     *
     * @param idCard 身份证
     * @return 结果 0：男 ；1：女
     */
    public static Integer judgeSex(String idCard) {
        String sexStr = idCard.substring(16, 17);
        int i = Integer.parseInt(sexStr);
        if (i % 2 == 0) {
            return 1;
        } else {
            return 0;
        }
    }
}