
package com.fly.cloud.database.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.admin.mapper.DbProvinceCarMapper;
import com.fly.cloud.database.admin.service.DbProvinceCarService;
import com.fly.cloud.database.common.entity.DbProvinceCar;
import com.fly.cloud.database.common.util.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * 车牌省份
 *
 * @author xux
 * @date 2020-09-02 11:17:55
 */
@Service
public class DbProvinceCarServiceImpl extends ServiceImpl<DbProvinceCarMapper, DbProvinceCar> implements DbProvinceCarService {

    /**
     * 导入车牌省份数据
     *
     * @return
     */
    @Override
    @Transactional
    public R importCarData() {
        File jsonFile = null;
        try {
            jsonFile = ResourceUtils.getFile("classpath:province_car.json");
            String json = null;
            try {
                json = FileUtils.readFileToString(jsonFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<DbProvinceCar> list = JsonUtils.jsonStrToList(json, DbProvinceCar[].class);
//            list.forEach(person -> {
//                System.out.println(person.toString());
//            });
            this.saveBatch(list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return R.ok();
    }

    /**
     * 根据车牌号获取省份数据
     *
     * @param carNum 车牌号
     * @return
     */
    @Override
    @Transactional
    public R getCarData(String carNum) {
        // 截取车牌号前两位
        String hp = carNum.substring(0, 2);
        String city = "";
        // 根据前两位获取地区信息
        DbProvinceCar provinceCar = this.getOne(Wrappers.<DbProvinceCar>query().lambda().eq(DbProvinceCar::getHp, hp));
        if (provinceCar != null) {
            city = provinceCar.getCity();
        }
        return R.ok(city);
    }

}
