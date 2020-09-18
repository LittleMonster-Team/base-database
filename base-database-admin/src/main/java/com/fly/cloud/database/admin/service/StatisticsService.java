
package com.fly.cloud.database.admin.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.common.entity.CustomerInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 统计
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
public interface StatisticsService extends IService<CustomerInfo> {

    /**
     * 男女占比统计
     *
     * @param obj 查询条件
     * @return
     */
    R genderStatistics(JSONObject obj);

    /**
     * 区域占比统计
     *
     * @param obj 查询条件
     * @return
     */
    R regionalShareStatistics(JSONObject obj);

    /**
     * 出险次数占比统计
     *
     * @param obj 查询条件
     * @return
     */
    R accidentsNumStatistics(JSONObject obj);

    /**
     * 使用年限占比统计
     *
     * @param obj 查询条件
     * @return
     */
    R serviceLifeStatistics(JSONObject obj);

    /**
     * 导出男女占比统计
     *
     * @param obj 查询条件
     * @return
     */
    R exprotStatisticsData(JSONObject obj);
}
