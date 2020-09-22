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
     * 将数据添加进临时表中
     *
     * @param differenceList 差异数据
     * @param failList       失败数据
     */
    void saveInfoTemporary(List<CustomerInfo> differenceList, List<CustomerInfo> failList);
}
