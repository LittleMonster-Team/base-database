
package com.fly.cloud.database.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.CustomerInfoVersion;

import java.util.List;

/**
 * 客户信息版本表
 *
 * @author xux
 * @date 2020-09-09 10:07:55
 */
public interface CustomerInfoVersionService extends IService<CustomerInfoVersion> {

    /**
     * 批量添加数据
     *
     * @param infoList 数据
     */
    R saveCustomerInfoVersion(List<CustomerInfo> infoList);

    /**
     * 根据车牌号查询信息
     *
     * @param carNum 车牌号
     * @return
     */
    List<CustomerInfo> viewDetailsInfo(String carNum);
}

