package com.fly.cloud.database.admin.service.impl;

import com.fly.cloud.database.admin.config.DataBaseDataProperties;
import com.fly.cloud.database.admin.config.DataBaseExcelProperties;
import com.fly.cloud.database.admin.service.CustomerInfoAsyncService;
import com.fly.cloud.database.admin.service.CustomerInfoService;
import com.fly.cloud.database.admin.service.CustomerInfoTemporaryService;
import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerInfoAsyncServiceImpl implements CustomerInfoAsyncService {

    @Autowired
    private DataBaseDataProperties dataBaseDataProperties;
    @Autowired
    private DataBaseExcelProperties dataBaseExcelProperties;
    @Autowired
    private CustomerInfoService customerInfoService;
    @Autowired
    private CustomerInfoTemporaryService infoTemporaryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 执行异步任务
     * 将数据添加进表中
     *
     * @param infoList 数据
     */
    @Override
//    @Async("asyncServiceExecutorOne")
    @Transactional
    public void saveCustomerInfo(List<CustomerInfo> infoList, String organId, String year, Long num, String key) {
        // 数据库表数据
        Object status = redisTemplate.opsForValue().get(key);
        // 获取表名
        String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + num;
        // 获取建表字段0
        String tableFields = dataBaseExcelProperties.getTableFields();
        // 判断表是否已创建
        if (status == null) {
            // 根据部门id动态创建生成数据表
            String initTableSql = dataBaseExcelProperties.getInitTableSql();
            initTableSql = initTableSql.replace("OID", organId);
            initTableSql = initTableSql.replace("YEAR", year);
            initTableSql = initTableSql.replace("NUM", num.toString());
            // 创建表
            JdbcUtils.exectueUpdate(dataBaseDataProperties, initTableSql, null);
            // 插入数据并记录差异数据
            customerInfoService.insertInfoData(infoList, tableName, tableFields);
            try {
                // 将数据存进缓存中
                redisTemplate.opsForValue().set(key, organId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 插入数据并记录差异数据
            customerInfoService.insertInfoData(infoList, tableName, tableFields);
        }
        System.out.println("end saveCustomerInfo");
    }

    /**
     * 异步任务
     * 将数据添加进临时表中
     *
     * @param differenceList 差异数据
     * @param failList       失败数据
     */
    @Override
//    @Async("asyncServiceExecutorTwo")
    @Transactional
    public void saveInfoTemporary(List<CustomerInfo> differenceList, List<CustomerInfo> failList) {
        if (differenceList != null && differenceList.size() > 0) {
            infoTemporaryService.addTemporaryData(differenceList, CommonConstants.DIFFERENCE_DATA);
        }
        if (failList != null && failList.size() > 0) {
            infoTemporaryService.addTemporaryData(failList, CommonConstants.FAIL_DATA);
        }
        System.out.println("end saveInfoTemporary");
    }
}
