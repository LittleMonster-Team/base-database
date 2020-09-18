
package com.fly.cloud.database.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.common.entity.DbProvinceCar;
import com.fly.cloud.database.admin.service.DbProvinceCarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;

/**
 * 车牌省份
 *
 * @author xux
 * @date 2020-09-02 11:17:55
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/dbprovincecar")
@Api(value = "dbprovincecar", tags = "车牌省份管理")
public class DbProvinceCarController {

    private final DbProvinceCarService dbProvinceCarService;

    /**
     * 分页列表
     *
     * @param page          分页对象
     * @param dbProvinceCar 车牌省份
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_dbprovincecar_index')")
    public R getPage(Page page, DbProvinceCar dbProvinceCar) {
        return R.ok(dbProvinceCarService.page(page, Wrappers.query(dbProvinceCar)));
    }

    /**
     * 车牌省份查询
     *
     * @param id
     * @return R
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_dbprovincecar_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(dbProvinceCarService.getById(id));
    }

    /**
     * 车牌省份新增
     *
     * @param dbProvinceCar 车牌省份
     * @return R
     */
    @SysLog("新增车牌省份")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_dbprovincecar_add')")
    public R save(@RequestBody DbProvinceCar dbProvinceCar) {
        return R.ok(dbProvinceCarService.save(dbProvinceCar));
    }

    /**
     * 车牌省份修改
     *
     * @param dbProvinceCar 车牌省份
     * @return R
     */
    @SysLog("修改车牌省份")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_dbprovincecar_edit')")
    public R updateById(@RequestBody DbProvinceCar dbProvinceCar) {
        return R.ok(dbProvinceCarService.updateById(dbProvinceCar));
    }

    /**
     * 车牌省份删除
     *
     * @param id
     * @return R
     */
    @SysLog("删除车牌省份")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_dbprovincecar_del')")
    public R removeById(@PathVariable String id) {
        return R.ok(dbProvinceCarService.removeById(id));
    }


    /**
     * 导入车牌省份数据
     *
     * @return R
     */
    @GetMapping("/importCarData")
    public R importCarData() {
        return dbProvinceCarService.importCarData();
    }


    /**
     * 导入车牌省份数据
     *
     * @return R
     */
    @GetMapping("/getCarData/{carNum}")
    public R getCarData(@PathVariable String carNum) {
        return dbProvinceCarService.getCarData(carNum);
    }
}
