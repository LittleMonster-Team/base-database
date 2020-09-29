
package com.fly.cloud.database.admin.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.CustomerInfoTemporary;

import java.util.List;

/**
 * 数据失败与重复表
 *
 * @author xux
 * @date 2020-09-09 10:08:12
 */
public interface CustomerInfoTemporaryService extends IService<CustomerInfoTemporary> {
    /**
     * 分页列表
     *
     * @param page                  分页
     * @param customerInfoTemporary 客户信息
     * @param month                 月份
     * @return
     */
    Page getPage(Page page, CustomerInfoTemporary customerInfoTemporary, String month);

    /**
     * 分页搜索数据
     *
     * @param obj 搜索数据
     * @return
     */
    Page searchTemporaryInfo(JSONObject obj);

    /**
     * 清空临时表数据
     */
    void clearTableData();

    /**
     * 将数据添加进临时表中
     *
     * @param dataList 手数据
     * @param dataType 数据类型
     */
    void addTemporaryData(List<CustomerInfo> dataList, String dataType);

    /**
     * 导入差异数据
     *
     * @param infoList 数据
     * @return
     */
    R importDifferenceData(List<CustomerInfoTemporary> infoList);

    /**
     * 修改客户信息临时表
     *
     * @param customerInfoTemporary 客户信息
     * @return
     */
    R updateTemporary(CustomerInfoTemporary customerInfoTemporary);

    /**
     * 导入失败数据
     *
     * @param infoList 数据
     * @return
     */
    R importFailData(List<CustomerInfoTemporary> infoList);

}
