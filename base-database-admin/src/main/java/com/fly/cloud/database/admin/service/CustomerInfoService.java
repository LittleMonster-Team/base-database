
package com.fly.cloud.database.admin.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.vo.CustomerInfoVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 客户信息
 *
 * @author xux
 * @date 2020-09-01 14:17:22
 */
public interface CustomerInfoService extends IService<CustomerInfo> {

    /**
     * 批量导入上传数据
     *
     * @param filePath  文件路径
     * @param fileNames 文件名
     * @param organId   商户id
     * @return
     */
    R importExcelData(String filePath, String fileNames, String organId);

    /**
     * 筛选数据
     *
     * @param obj 筛选数据
     * @return
     */
    Page screenCustomerInfo(JSONObject obj);

    /**
     * 搜索客户信息列表
     *
     * @param obj 搜索数据
     * @return
     */
    Page searchCustomerInfo(JSONObject obj);

    /**
     * 更新客户信息
     *
     * @param customerInfo 客户信息
     * @return
     */
    R updateCustomerInfo(CustomerInfo customerInfo);

    /**
     * 查询当前上传数据的差异信息
     *
     * @param list 查询数据
     * @return
     */
    Map<String, Object> queryCurrentDiffList(List<CustomerInfo> list);

    /**
     * 查询总数据与当前数据的差异信息
     *
     * @param tList 总数据
     * @param cList 当前导入成功数据
     * @return
     */
    Map<String, List<CustomerInfo>> queryTotalDiffList(List<CustomerInfo> tList, List<CustomerInfo> cList);

    /**
     * 根据车牌号查询数据
     *
     * @param carNum    车牌号
     * @param tableName 表名
     * @return
     */
    List<CustomerInfo> getCustomerInfoByCarNum(String carNum, String tableName);

    /**
     * 查询部门下客户信息
     *
     * @param organId  商户id
     * @param sequence 序列
     * @param year     年份
     * @return
     */
    List<CustomerInfo> queryCustomerInfoList(String organId, long sequence, String year);

    /**
     * 查询部门下客户信息
     *
     * @param organId  商户id
     * @param sequence 序列
     * @param year     年份
     * @param month    月份
     * @return
     */
    List<CustomerInfo> queryInfoListByMonth(String organId, long sequence, String year, String month);

    /**
     * 获取数据数量
     *
     * @param organId  商户id
     * @param sequence 序列
     * @param year     年份
     * @return
     */
    int getCustomerInfoListCount(String organId, long sequence, String year);

    /**
     * 将数据转为Excel文件并保存起来
     * 返回文件信息
     *
     * @param infoList 数据
     * @return
     */
    R exprotExcelData(List<CustomerInfo> infoList);

    /**
     * 下载文件保存本地
     *
     * @param fileLink 文件路径
     * @param response 信息流
     * @return
     */
    R downloadExcelFiles(String fileLink, HttpServletResponse response);

    /**
     * 获取表序列
     *
     * @param organId 商户id
     * @return
     */
    Long getSequenceValue(String organId);

    /**
     * 批量插入数据
     *
     * @param infoList    数据
     * @param tableName   表明
     * @param tableFields 表字段
     */
    void insertCustomerInfoData(List<CustomerInfo> infoList, String tableName, String tableFields);

    /**
     * 根据证件号获取地区与性别信息
     *
     * @param list 数据
     * @return
     */
    List<CustomerInfo> dataAssignment(List<CustomerInfo> list);

    /**
     * 获取成功与差异数据
     * 将成功数据保存进数据库
     *
     * @param sList   数据
     * @param organId 商户id
     * @return
     */
    Map<String, Object> getDifferenceInfoData(List<CustomerInfo> sList, String organId);

    /**
     * 查询客户信息详情
     *
     * @param params 参数
     * @return
     */
    R viewDetailsInfo(JSONObject params);

    /**
     * 去除客户信息
     *
     * @param infoVo 客户信息
     * @return
     */
    R removeCustomerInfo(CustomerInfoVO infoVo);

    /**
     * 将数据添加进临时表中
     *
     * @param differenceList
     * @param differenceData
     */
    void saveInfoTemporary(List<CustomerInfo> differenceList, String differenceData);

    /**
     * 格式化客户性别
     *
     * @param info 客户信息
     * @return
     */
    CustomerInfo formatGender(CustomerInfo info);
}
