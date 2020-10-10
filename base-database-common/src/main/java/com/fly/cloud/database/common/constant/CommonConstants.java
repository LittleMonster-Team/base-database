package com.fly.cloud.database.common.constant;


/**
 * 公共常量词库
 *
 * @description: 公共常量词库
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public interface CommonConstants {

    /**
     * 正式数据库序列前缀
     */
    String BASE_DATA_PREFIX = "CUSTOMER_INFO";

    /**
     * 数据库缓存前缀
     */
    String BASE_DATA_REDIS_PREFIX = "base_database:customer_info:CUSTOMER_INFO_";

    /**
     * 数据返回状态
     * 0：表示成功
     * 1：表示失败
     */
    Integer CORRECT_RESULT = 0;
    Integer ERROR_RESULT = 1;

    /**
     * 新旧数据标识
     * 0：表示新数据
     * 1：表示旧数据
     */
    Integer NEW_DATA = 0;
    Integer USED_DATA = 1;

    /**
     * 查询数据标识
     * all：表示查询全部
     * car_num：表示查询车牌号
     * count：表示查询数量
     */
    String SELECT_All = "*";
    String SELECT_CAR_NUM = "car_num";
    String SELECT_TABLE_NAME = "table_info";
    String SELECT_COUNTY = "county";
    String SELECT_ACCIDENTS_NUM = "accidents_num";
    String SELECT_USE_YEAR = "use_year";

    /**
     * 上传文件后缀
     * xls：表示2003 版本的excel
     * xlsx：表示2007 版本的excel
     */
    String Excel_2003 = ".xls";
    String Excel_2007 = ".xlsx";

    /**
     * 客户性别标识
     * 0：表示男
     * 1：表示女
     * 2：表示未知
     */
    String GENDER_MALE = "0";
    String GENDER_FEMALE = "1";
    String GENDER_UNKNOWN = "2";

    /**
     * 临时数据标识
     * 0：表示失败数据
     * 1：表示差异数据
     */
    String FAIL_DATA = "0";
    String DIFFERENCE_DATA = "1";

    /**
     * 修改数据标识
     * 1：表示已修改
     */
    String EDIT_FLAG = "1";

    /**
     * 默认初始值
     */
    String DEFAULT_INITIAL_VALUE_ZERO = "0";
    String DEFAULT_INITIAL_VALUE_EMPTY = "";
    String DEFAULT_INITIAL_VALUE_UNKNOWN = "未知";
    String DEFAULT_INITIAL_VALUE_NOTHING = "无";

    /**
     * 数据状态标识
     * 0：最新数据
     * 1：其他版本数据
     */
    String LATEST_DATA = "0";
    String OTHER_VERSIONS_OF_DATA = "1";

    /**
     * 文件大小标识
     * b：字节
     * kb：千字节
     * m：兆字节
     */
    String BYTE_SIZE = "B";
    String BAIKILOBYTE_SIZE = "KB";
    String MEGABYTE_SIZE = "M";

    /**
     * 统计数据单位标识
     * 次：出险次数
     * 年：使用年限
     */
    String COMPANY_SECOND = "次";
    String COMPANY_YEAR = "年";

    /**
     * 数据去重标识
     * 0：表示未去重
     * 1：表示已去重
     */
    String NO_DE_DUPLICATION = "0";
    String WEIGHT_REMOVED = "1";


    /**
     * 删除标识
     * 0 :表示未删除
     * 1 :表示已删除
     */
    String DEL_FLAG_NOT_DELETED = "0";
    String DEL_FLAG_DELETED = "1";
    /**
     * 每份文档的数量
     */
    Integer DOCUMENT_DATA_VOLUME = 50000;
}
