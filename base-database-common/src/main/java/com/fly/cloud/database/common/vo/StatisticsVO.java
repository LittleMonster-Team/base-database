package com.fly.cloud.database.common.vo;

import com.fly.cloud.database.common.entity.CustomerInfo;
import lombok.Data;

/**
 * 统计数据VO类
 *
 * @description: 统计数据VO类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
@Data
public class StatisticsVO extends CustomerInfo {

    /**
     * 数量
     */
    private String value;
}
