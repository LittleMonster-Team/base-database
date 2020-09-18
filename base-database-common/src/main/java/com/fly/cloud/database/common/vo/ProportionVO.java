package com.fly.cloud.database.common.vo;

import lombok.Data;

/**
 * 比例数据VO类
 *
 * @description: 比例数据VO类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
@Data
public class ProportionVO {

    /**
     * 标题
     */
    private String name;

    /**
     * 比例
     */
    private String proportion;

    /**
     * 数量
     */
    private String value;
}
