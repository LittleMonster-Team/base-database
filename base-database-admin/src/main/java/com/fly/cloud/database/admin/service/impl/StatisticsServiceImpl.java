
package com.fly.cloud.database.admin.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.security.util.SecurityUtils;
import com.fly.cloud.database.admin.config.DataBaseDataProperties;
import com.fly.cloud.database.admin.config.DataBaseExcelProperties;
import com.fly.cloud.database.admin.mapper.CustomerInfoMapper;
import com.fly.cloud.database.admin.service.CustomerInfoService;
import com.fly.cloud.database.admin.service.StatisticsService;
import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.util.DateUtils;
import com.fly.cloud.database.common.util.JdbcUtils;
import com.fly.cloud.database.common.util.OperationUtils;
import com.fly.cloud.database.common.vo.ProportionVO;
import com.fly.cloud.database.common.vo.StatisticsVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 统计
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
@Service
public class StatisticsServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements StatisticsService {

    @Autowired
    private CustomerInfoService customerInfoService;
    @Autowired
    private DataBaseExcelProperties dataBaseExcelProperties;
    @Autowired
    private DataBaseDataProperties dataBaseDataProperties;

    /**
     * 男女占比统计
     *
     * @param obj 查询条件
     * @return
     */
    @Override
    @Transactional
    public R genderStatistics(JSONObject obj) {
        String organId = SecurityUtils.getUser().getOrganId();
        String year = obj.getStr("year");
        String month = obj.getStr("month");
        Map<String, Object> map = new HashMap<String, Object>();
        // 男客户数
        int maleNum = this.getInfoByGenderNum(organId, year, month, CommonConstants.GENDER_MALE);
        // 女客户数
        int femaleNum = this.getInfoByGenderNum(organId, year, month, CommonConstants.GENDER_FEMALE);
        // 总客户数
        int totalNum = maleNum + femaleNum;
        // 男客户占比数
        String malePro = OperationUtils.getPercentage(maleNum, totalNum);
        // 女客户占比数
        String femalePro = OperationUtils.getPercentage(femaleNum, totalNum);
        map.put("maleNum", maleNum);
        map.put("femaleNum", femaleNum);
        map.put("malePro", malePro);
        map.put("femalePro", femalePro);
        return R.ok(map);
    }

    /**
     * 根据性别批量查询数据
     *
     * @param organId 部门id
     * @param year    选择的年份
     * @param month   选择的月份
     * @param gender  查询条件
     * @return
     */
    private int getInfoByGenderNum(String organId, String year, String month, String gender) {
        // 获取当前序列号
        Long sequence = customerInfoService.getSequenceValue(organId);
        int num = 0;
        for (long i = sequence; i >= 0; i--) {
            // 筛选数据
            num += this.queryInfoNumByGender(organId, i, year, month, gender);
        }
        return num;
    }

    /**
     * 根据性别查询数据
     *
     * @param organId  部门id
     * @param sequence 表序列
     * @param year     选择的年份
     * @param month    选择的月份
     * @param gender   查询条件
     * @return
     */
    private int queryInfoNumByGender(String organId, long sequence, String year, String month, String gender) {
        try {
            month = DateUtils.padDatazero(Integer.parseInt(month));
            // 获取表名
            String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + sequence;
            String sql = "SELECT COUNT(*) FROM " + tableName + "  WHERE gender = '" + gender + "' AND create_time LIKE '%-" + month + "-%' ORDER BY create_time DESC";
            return JdbcUtils.executeQueryNum(dataBaseDataProperties, sql);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 区域占比统计
     *
     * @param obj 查询条件
     * @return
     */
    @Override
    @Transactional
    public R regionalShareStatistics(JSONObject obj) {
        String organId = SecurityUtils.getUser().getOrganId();
        String year = obj.getStr("year");
        String month = obj.getStr("month");
        Map<String, Object> map = new HashMap<String, Object>();
        // 总数量
        int regionalTatal = 0;
        // 比例数据
        List<ProportionVO> regionalList = new ArrayList<ProportionVO>();
        // 查询数据
        List<StatisticsVO> list = this.getInfoByConditionList(organId, year, month, CommonConstants.SELECT_COUNTY);
        if (list != null && list.size() > 0) {
            regionalTatal = list.stream().mapToInt(info -> Integer.parseInt(info.getValue())).sum();
            int finalTotal = regionalTatal;
            list.forEach(info -> {
                ProportionVO pvo = new ProportionVO();
                // 如果区县不存在则转换为默认值
                if (!StringUtils.isNotBlank(info.getCounty())) {
                    pvo.setName(CommonConstants.DEFAULT_INITIAL_VALUE_UNKNOWN);
                } else {
                    pvo.setName(info.getCounty());
                }
                pvo.setProportion(OperationUtils.getPercentage(Integer.parseInt(info.getValue()), finalTotal));
                pvo.setValue(info.getValue());
                regionalList.add(pvo);
            });
        }
        map.put("regionalTatal", regionalTatal);
        map.put("regionalList", regionalList);
        return R.ok(map);
    }


    /**
     * 出险次数占比统计
     *
     * @param obj 查询条件
     * @return
     */
    @Override
    @Transactional
    public R accidentsNumStatistics(JSONObject obj) {
        String organId = SecurityUtils.getUser().getOrganId();
        String year = obj.getStr("year");
        String month = obj.getStr("month");
        Map<String, Object> map = new HashMap<String, Object>();
        // 总数量
        int accidentsTatal = 0;
        // 比例数据
        List<ProportionVO> accidentsList = new ArrayList<ProportionVO>();
        // 查询数据
        List<StatisticsVO> list = this.getInfoByConditionList(organId, year, month, CommonConstants.SELECT_ACCIDENTS_NUM);
        if (list != null && list.size() > 0) {
            // 获取总人数
            accidentsTatal = list.stream().mapToInt(info -> Integer.parseInt(info.getValue())).sum();
            int finalTotal = accidentsTatal;
            list.forEach(info -> {
                ProportionVO pvo = new ProportionVO();
                pvo.setName(info.getAccidentsNum() + CommonConstants.COMPANY_SECOND);
                pvo.setProportion(OperationUtils.getPercentage(Integer.parseInt(info.getValue()), finalTotal));
                pvo.setValue(info.getValue());
                accidentsList.add(pvo);
            });
        }
        map.put("accidentsTatal", accidentsTatal);
        map.put("accidentsList", accidentsList);
        return R.ok(map);

    }

    /**
     * 使用年限占比统计
     *
     * @param obj 查询条件
     * @return
     */
    @Override
    @Transactional
    public R serviceLifeStatistics(JSONObject obj) {
        String organId = SecurityUtils.getUser().getOrganId();
        String year = obj.getStr("year");
        String month = obj.getStr("month");
        Map<String, Object> map = new HashMap<String, Object>();
        // 总数量
        int serviceLifeTatal = 0;
        // 比例数据
        List<ProportionVO> serviceLifeList = new ArrayList<ProportionVO>();
        // 查询数据
        List<StatisticsVO> list = this.getInfoByConditionList(organId, year, month, CommonConstants.SELECT_USE_YEAR);
        if (list != null && list.size() > 0) {
            // 获取总人数
            serviceLifeTatal = list.stream().mapToInt(info -> Integer.parseInt(info.getValue())).sum();
            int finalTotal = serviceLifeTatal;
            list.forEach(info -> {
                ProportionVO pvo = new ProportionVO();
                // 如果使用年限不存在则转换为默认值
                if (!StringUtils.isNotBlank(info.getUseYear())) {
                    pvo.setName(CommonConstants.DEFAULT_INITIAL_VALUE_ZERO + CommonConstants.COMPANY_YEAR);
                } else {
                    pvo.setName(info.getUseYear() + CommonConstants.COMPANY_YEAR);
                }
                pvo.setProportion(OperationUtils.getPercentage(Integer.parseInt(info.getValue()), finalTotal));
                pvo.setValue(info.getValue());
                serviceLifeList.add(pvo);
            });
        }
        map.put("serviceLifeTatal", serviceLifeTatal);
        map.put("serviceLifeList", serviceLifeList);
        return R.ok(map);
    }

    /**
     * 导出统计数据
     *
     * @param obj      查询条件
     * @return
     */
    @Override
    @Transactional
    public R exprotStatisticsData(JSONObject obj) {
        String organId = SecurityUtils.getUser().getOrganId();
        // 查询的年份
        String year = obj.getStr("year");
        // 查询的月份
        String month = obj.getStr("month");
        // 查询数据
        List<CustomerInfo> infoList = this.getInfoList(organId, year, month);
        return customerInfoService.exprotExcelData(infoList);
    }

    /**
     * 根据不同条件批量查询数据
     *
     * @param organId   部门id
     * @param year      选择的年份
     * @param month     选择的月份
     * @param condition 查询条件
     * @return
     */
    private List<StatisticsVO> getInfoByConditionList(String organId, String year, String month, String condition) {
        // 获取当前序列号
        Long sequence = customerInfoService.getSequenceValue(organId);
        List<StatisticsVO> infoList = new ArrayList<StatisticsVO>();
        for (long i = sequence; i >= 0; i--) {
            // 筛选数据
            List<StatisticsVO> list = this.queryInfoListByCondition(organId, i, year, month, condition);
            if (list != null && list.size() > 0) {
                infoList.addAll(list);
            }
        }
        return infoList;
    }

    /**
     * 根据不同条件查询数据
     *
     * @param organId   部门id
     * @param sequence  表序列
     * @param year      选择的年份
     * @param month     选择的月份
     * @param condition 查询条件
     * @return
     */
    private List<StatisticsVO> queryInfoListByCondition(String organId, long sequence, String year, String month, String condition) {
        List<StatisticsVO> list = null;
        try {
            // 格式化月份
            month = DateUtils.padDatazero(Integer.parseInt(month));
            // 获取表名
            String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + sequence;
            String sql = "SELECT count(0) as value,c.* FROM	`" + tableName + "` c WHERE create_time LIKE '%-" + month + "-%' GROUP BY " + condition + " ORDER BY (" + condition + "+0) ASC";
            list = (List<StatisticsVO>) JdbcUtils.executeQuery(dataBaseDataProperties, sql, StatisticsVO.class);
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * 根据年月批量查询数据
     *
     * @param organId 部门id
     * @param year    选择的年份
     * @param month   选择的月份
     * @return
     */
    private List<CustomerInfo> getInfoList(String organId, String year, String month) {
        // 获取当前序列号
        Long sequence = customerInfoService.getSequenceValue(organId);
        // 查询出的数据集合
        List<CustomerInfo> infoList = new ArrayList<CustomerInfo>();
        for (long i = sequence; i >= 0; i--) {
            // 筛选数据
            List<CustomerInfo> list = this.queryInfoList(organId, i, year, month);
            if (list != null && list.size() > 0) {
                infoList.addAll(list);
            }
        }
        return infoList;
    }

    /**
     * 根据年月查询数据
     *
     * @param organId  部门id
     * @param sequence 表序列
     * @param year     选择的年份
     * @param month    选择的月份
     * @return
     */
    private List<CustomerInfo> queryInfoList(String organId, long sequence, String year, String month) {
        List<CustomerInfo> list = null;
        try {
            // 格式化月份
            month = DateUtils.padDatazero(Integer.parseInt(month));
            // 获取表名
            String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + sequence;
            String sql = "SELECT * FROM	`" + tableName + "` WHERE create_time LIKE '%-" + month + "-%' ORDER BY create_time DESC";
            list = (List<CustomerInfo>) JdbcUtils.executeQuery(dataBaseDataProperties, sql, CustomerInfo.class);
        } catch (Exception e) {
        }
        return list;
    }

}
