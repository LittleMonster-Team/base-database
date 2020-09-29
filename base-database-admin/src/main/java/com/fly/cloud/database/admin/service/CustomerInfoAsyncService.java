package com.fly.cloud.database.admin.service;


import com.fly.cloud.database.common.entity.CustomerInfo;

import java.util.List;

public interface CustomerInfoAsyncService {

    /**
     * 异步任务
     * 将数据添加进表中
     *
     * @param infoList 数据
     */
    void saveCustomerInfo(List<CustomerInfo> infoList, String organId, String year, Long num, String key);

    /**
     * 异步任务
     * 将差异与失败数据添加进表中
     *
     * @param dataList 数据
     * @param dataType 数据类型
     */
    void saveInfoTemporary(List<CustomerInfo> dataList, String dataType);

    /**
     * 异步任务
     * 将数据保存进临时表
     *
     * @param sList    数据
     * @param fileName 文件名称
     */
    void saveExtraInfo(List<CustomerInfo> sList, String fileName);

    /**
     * 异步任务
     * 将数据转为Excel文件
     *
     * @param infoList 数据
     * @param ctxPath  路径
     * @return
     */
    void exprotExcelData(List<CustomerInfo> infoList, String ctxPath);
}
