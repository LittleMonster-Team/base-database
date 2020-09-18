package com.fly.cloud.database.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CODE类型枚举
 *
 * @description: CODE类型枚举
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
@Getter
@AllArgsConstructor
public enum ValidTypeEnum implements CodeEnum {

    /**
     * 非空
     */
    NOTBLANK(0, "非空"),

    /**
     * 车牌号
     */
    CARNO(1, "车牌号"),

    /**
     * 证件号
     */
    CERTIFY(2, "证件号"),

    /**
     * 手机号
     */
    PHONE(3, "phone");

    /**
     * 校验类型
     */
    private Integer code;
    private String msg;


}
