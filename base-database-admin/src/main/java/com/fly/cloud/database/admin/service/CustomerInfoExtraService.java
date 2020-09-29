
package com.fly.cloud.database.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.CustomerInfoExtra;
import com.fly.cloud.database.common.entity.ImportRecord;

import java.util.List;
import java.util.Map;

/**
 * 数据临时表
 *
 * @author xux
 * @date 2020-09-28 13:25:03
 */
public interface CustomerInfoExtraService extends IService<CustomerInfoExtra> {
    /**
     * 将数据保存进临时表
     *
     * @param sList
     */
    void saveExtraInfo(List<CustomerInfo> sList, String relationText);

    /**
     * 去除重复数据
     *
     * @param importRecord
     * @return
     */
    Map<String, Object> dataDeDuplication(ImportRecord importRecord);
}
