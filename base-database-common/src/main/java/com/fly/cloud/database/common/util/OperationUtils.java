package com.fly.cloud.database.common.util;

import com.fly.cloud.database.common.entity.CustomerInfo;
import org.apache.commons.lang.StringUtils;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作工具类
 *
 * @description: 操作工具类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class OperationUtils {
    /**
     * 获取num1占num2的百分比
     *
     * @param num1
     * @param num2
     * @return
     */
    public static String getPercentage(double num1, double num2) {
        if (num2 == 0) {
            return "0";
        }
        String result = "";
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        result = numberFormat.format(num1 / num2 * 100);
        return result;
    }

    /**
     * 去除第一个数组中包含第二个数组的数据
     *
     * @param successList    成功数组
     * @param differenceList 差异数组
     * @return
     */
    public static List<String> arrContrast(List<String> successList, List<String> differenceList) {
        List<String> list = new LinkedList<String>();
        // 去除第一个数组的重复数据
        for (String str : successList) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        // 如果第二个数组存在和第一个数组相同的值，就删除
        for (String str : differenceList) {
            if (list.contains(str)) {
                list.remove(str);
            }
        }
        return list;
    }

    /**
     * 返回去除集合中车牌号重复数据
     *
     * @param infoList 需去重的集合
     * @return
     */
    private static List<CustomerInfo> removeDuplicateOrder(List<CustomerInfo> infoList) {
        Set<CustomerInfo> set = new TreeSet<CustomerInfo>(Comparator.comparing(CustomerInfo::getCarNum));
        set.addAll(infoList);
        return new ArrayList<>(set);
    }

    /**
     * 集合去除重复数据
     *
     * @param list 集合
     * @return
     */
    public static <T> java.util.List<T> removeDuplicate(List<T> list) {
        List<T> collect = list.stream().distinct().collect(Collectors.toList());
        return collect;
    }

    /**
     * 条件筛选
     *
     * @param list         筛选目标
     * @param customerInfo 筛选条件
     * @return
     */
    public static List<CustomerInfo> screenData(List<CustomerInfo> list, CustomerInfo customerInfo) {
        if (list != null && list.size() > 0) {
            List<CustomerInfo> collect = list.stream()
                    .filter(info ->
                            info.getGender().contains(StringUtils.isNotBlank(customerInfo.getGender()) ? customerInfo.getGender() : "")// 性别
                                    && info.getAccidentsNum().contains(StringUtils.isNotBlank(customerInfo.getAccidentsNum()) ? customerInfo.getAccidentsNum() : "")// 出险次数
                                    && info.getUseYear().contains(StringUtils.isNotBlank(customerInfo.getUseYear()) ? customerInfo.getUseYear() : "")// // 使用年限
                                    && info.getUseYear().contains(StringUtils.isNotBlank(customerInfo.getFirstDate()) ? customerInfo.getFirstDate() : "")// 初登日期

                    ).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 条件搜索
     *
     * @param list      搜索目标
     * @param condition 搜索条件
     * @return
     */
    public static List<CustomerInfo> searchData(List<CustomerInfo> list, String condition) {
        if (list != null && list.size() > 0) {
            List<CustomerInfo> collect = list.stream()
                    .filter(info ->
                            ValidatorUtil.isPureNumbers(condition) ?
                                    StringUtils.isNotBlank(condition) ? info.getPhone().equals(condition) : info.getPhone().contains("")
                                    : StringUtils.isNotBlank(condition) ? info.getCustomerName().equals(condition) : info.getCustomerName().contains("")
                    ).collect(Collectors.toList());
            return collect;
        }
        return null;
    }


}
