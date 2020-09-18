package com.fly.cloud.database.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 数据库配置项
 *
 * @description: 数据库配置项
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.druid")
public class DataBaseDataProperties extends Properties {
    /**
     * 驱动程序类名
     */
    private String driverClassName;
    /**
     * 用户名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 数据库路径
     */
    private String url;

}
