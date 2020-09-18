package com.fly.cloud.database.common.vo;

import com.fly.cloud.database.common.entity.CustomerInfo;
import lombok.Data;

/**
 * 客户信息VO类
 *
 * @description: 客户信息VO类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
@Data
public class CustomerInfoVO extends CustomerInfo {

    /**
     * 数据状态(0：最新数据，1：其他版本数据)
     */
    private String dataStatus;
}
