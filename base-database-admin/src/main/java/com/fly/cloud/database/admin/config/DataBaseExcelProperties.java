package com.fly.cloud.database.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 导入导出配置项
 *
 * @description: 导入导出配置项
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "database.data")
public class DataBaseExcelProperties {
    /**
     * 字段
     */
    private String filedNames;
    /**
     * 校验字段
     */
    private String checkFields;
    /**
     * 必要字段
     */
    private String necessaryFields;
    /**
     * 表前缀
     */
    private String tablePrefix;
    /**
     * 表字段
     */
    private String tableFields;
    /**
     * 建表语句
     */
    private String initTableSql;
    /**
     * 表数量最大限额
     */
    private String maximumLimit;
    /**
     * 导出字段
     */
    private String exprotFileds;
    /**
     * 导出文件根目录
     */
    private String exprotFileRoot;
}
