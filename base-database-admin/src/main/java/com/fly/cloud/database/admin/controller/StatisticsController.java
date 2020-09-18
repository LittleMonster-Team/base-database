
package com.fly.cloud.database.admin.controller;

import cn.hutool.json.JSONObject;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.admin.service.StatisticsService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 统计
 *
 * @author xux
 * @date 2020-09-01 14:17:22
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/statistics")
@Api(value = "statistics", tags = "统计")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 男女占比统计
     *
     * @param obj 筛选条件
     * @return
     */
    @SysLog("男女占比统计")
    @PostMapping("/genderStatistics")
    @PreAuthorize("@ato.hasAuthority('database_statistics_gender')")
    public R genderStatistics(@RequestBody JSONObject obj) {
        return statisticsService.genderStatistics(obj);
    }

    /**
     * 区域占比统计
     *
     * @param obj 筛选条件
     * @return
     */
    @SysLog("区域占比统计")
    @PostMapping("/regionalShareStatistics")
    @PreAuthorize("@ato.hasAuthority('database_statistics_regional')")
    public R regionalShareStatistics(@RequestBody JSONObject obj) {
        return statisticsService.regionalShareStatistics(obj);
    }

    /**
     * 出险次数占比统计
     *
     * @param obj 筛选条件
     * @return
     */
    @SysLog("出险次数占比统计")
    @PostMapping("/accidentsNumStatistics")
    @PreAuthorize("@ato.hasAuthority('database_statistics_accnum')")
    public R accidentsNumStatistics(@RequestBody JSONObject obj) {
        return statisticsService.accidentsNumStatistics(obj);
    }

    /**
     * 使用年限占比统计
     *
     * @param obj 筛选条件
     * @return
     */
    @SysLog("使用年限占比统计")
    @PostMapping("/serviceLifeStatistics")
    @PreAuthorize("@ato.hasAuthority('database_statistics_serlife')")
    public R serviceLifeStatistics(@RequestBody JSONObject obj) {
        return statisticsService.serviceLifeStatistics(obj);
    }


    /**
     * 导出统计数据
     *
     * @param obj      筛选条件
     * @param response 数据流
     * @return
     */
    @SysLog("导出统计数据")
    @PostMapping("/exprotStatisticsData")
    @PreAuthorize("@ato.hasAuthority('database_statistics_exprot')")
    public R exprotStatisticsData(@RequestBody JSONObject obj, HttpServletResponse response) {
        return statisticsService.exprotStatisticsData(obj, response);
    }
}
