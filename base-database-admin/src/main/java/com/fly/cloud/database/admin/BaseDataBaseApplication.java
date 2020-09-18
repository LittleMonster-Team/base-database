package com.fly.cloud.database.admin;


import com.fly.cloud.common.security.annotation.EnableBaseFeignClients;
import com.fly.cloud.common.security.annotation.EnableBaseResourceServer;
import com.fly.cloud.common.swagger.annotation.BaseEnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author JL
 * @date 2019/07/29
 * 内容管理模块
 */
@BaseEnableSwagger
@SpringCloudApplication
@EnableBaseFeignClients
@EnableBaseResourceServer
public class BaseDataBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseDataBaseApplication.class, args);
    }

}
