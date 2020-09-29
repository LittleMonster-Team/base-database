package com.fly.cloud.database.admin.service.impl;

import com.fly.cloud.database.admin.config.DataBaseDataProperties;
import com.fly.cloud.database.admin.config.DataBaseExcelProperties;
import com.fly.cloud.database.admin.service.*;
import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.ImportRecord;
import com.fly.cloud.database.common.util.DateUtils;
import com.fly.cloud.database.common.util.ExeclUtil;
import com.fly.cloud.database.common.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private CustomerInfoExtraService infoExtraService;
    @Autowired
    private ImportRecordService importRecordService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 执行异步任务
     * 将数据添加进表中
     *
     * @param infoList 数据
     */
    @Override
    @Async("asyncServiceExecutorOne")
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
            // 批量插入数据
            customerInfoService.insertCustomerInfoData(infoList, tableName, tableFields);
            try {
                // 将数据存进缓存中
                redisTemplate.opsForValue().set(key, organId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 批量插入数据
            customerInfoService.insertCustomerInfoData(infoList, tableName, tableFields);
        }
        System.out.println("end saveCustomerInfo");
    }

    /**
     * 异步任务
     * 将差异与失败数据添加进表中
     *
     * @param dataList 数据
     * @param dataType 数据类型
     */
    @Override
    @Async("asyncServiceExecutorTwo")
    @Transactional
    public void saveInfoTemporary(List<CustomerInfo> dataList, String dataType) {
        if (dataList != null && dataList.size() > 0) {
            infoTemporaryService.addTemporaryData(dataList, dataType);
        }
        System.out.println("end saveInfoTemporary");
    }

    /**
     * 将数据保存进临时表
     *
     * @param sList    数据
     * @param fileName 文件名称
     */
    @Override
    @Async("asyncServiceExecutorThree")
    @Transactional
    public void saveExtraInfo(List<CustomerInfo> sList, String fileName) {
        String relationText = UUID.randomUUID().toString().replace("-", "");
        ImportRecord importRecord = new ImportRecord();
        importRecord.setFileName(fileName);
        importRecord.setRelationText(relationText);
        importRecord.setDataNum(sList.size());
        importRecord.setCreateTime(DateUtils.getCurrentDate());
        importRecord.setDuplicateRemoval(CommonConstants.NO_DE_DUPLICATION);
        importRecord.setDelFlag(CommonConstants.DEL_FLAG_NOT_DELETED);
        infoExtraService.saveExtraInfo(sList, relationText);
        // 新增导入流程表
        importRecordService.save(importRecord);
        System.out.println("end saveExtraInfo");
    }

    /**
     * 将数据转为Excel文件
     *
     * @param infoList 数据
     * @param ctxPath  路径
     * @return
     */
    @Override
    @Async("asyncServiceExecutorFour")
    @Transactional
    public void exprotExcelData(List<CustomerInfo> infoList, String ctxPath) {
        String starTime = DateUtils.getCurrentDate();
        System.out.println("starTime" + starTime);
        //获取导出字段列表
        LinkedHashMap<String, String> fields = new LinkedHashMap<String, String>();
        String fieldNames = dataBaseExcelProperties.getExprotFileds();
        String[] fieldArr = fieldNames.split(",");
        for (String f : fieldArr) {
            String[] row = f.split("_");
            fields.put(row[0], row[1]);
        }
        // 获取需要创建文档的数量
        long chu = infoList.size() / 50000;
        if (chu > 0) {
            for (long index = 0; index < chu; index++) {
                List<CustomerInfo> chuList = infoList.subList((int) index * 50000, (int) index * 50000 + 50000);
                try {
                    // 获取文件相关信息
                    ExeclUtil.WorkToLocal(ExeclUtil.ListToWorkbook(chuList, fields, CommonConstants.Excel_2007), ctxPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // 获取剩余添加的数
        long yu = infoList.size() % 50000;
        if (yu > 0) {
            List<CustomerInfo> yuList = infoList.subList((int) (infoList.size() - yu), infoList.size());
            try {
                // 获取文件相关信息
                ExeclUtil.WorkToLocal(ExeclUtil.ListToWorkbook(yuList, fields, CommonConstants.Excel_2007), ctxPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String endTime = DateUtils.getCurrentDate();
        System.out.println("endTime" + endTime);
    }
}
