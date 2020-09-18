
package com.fly.cloud.database.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.common.entity.DbProvinceCar;

/**
 * 车牌省份
 *
 * @author xux
 * @date 2020-09-02 11:17:55
 */
public interface DbProvinceCarService extends IService<DbProvinceCar> {
    /**
     * 导入车牌省份数据
     *
     * @return
     */
    R importCarData();

    /**
     * 根据车牌号获取省份数据
     *
     * @param carNum 车牌号
     * @return
     */
    R getCarData(String carNum);
}
