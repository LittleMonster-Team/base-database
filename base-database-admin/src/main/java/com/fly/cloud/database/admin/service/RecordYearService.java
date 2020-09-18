
package com.fly.cloud.database.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.database.common.entity.RecordYear;

import java.util.Map;

/**
 * 记录建表年份
 *
 * @author xux
 * @date 2020-09-15 10:41:32
 */
public interface RecordYearService extends IService<RecordYear> {
    /**
     * 获取建表年份
     *
     * @param organId 商户id
     * @return
     */
    Map<String, String> getYearData(String organId);

    /**
     * 更新表数据
     *
     * @param organId 商户id
     */
    void updateYearInfo(String organId);

    /**
     * 新增表数据
     *
     * @param organId 商户id
     */
    void saveYearInfo(String organId);
}
