
package com.fly.cloud.database.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.admin.service.CustomerInfoExtraService;
import com.fly.cloud.database.common.entity.CustomerInfoExtra;
import com.fly.cloud.database.common.entity.ImportRecord;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 数据临时表
 *
 * @author xux
 * @date 2020-09-28 13:25:03
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/customerinfoextra")
@Api(value = "customerinfoextra", tags = "数据临时表管理")
public class CustomerInfoExtraController {

    private final CustomerInfoExtraService customerInfoExtraService;

    /**
     * 分页列表
     *
     * @param page              分页对象
     * @param customerInfoExtra 数据临时表
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_customerinfoextra_index')")
    public R getPage(Page page, CustomerInfoExtra customerInfoExtra) {
        return R.ok(customerInfoExtraService.page(page, Wrappers.query(customerInfoExtra)));
    }

    /**
     * 数据临时表查询
     *
     * @param id
     * @return R
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_customerinfoextra_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(customerInfoExtraService.getById(id));
    }

    /**
     * 数据临时表新增
     *
     * @param customerInfoExtra 数据临时表
     * @return R
     */
    @SysLog("新增数据临时表")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_customerinfoextra_add')")
    public R save(@RequestBody CustomerInfoExtra customerInfoExtra) {
        return R.ok(customerInfoExtraService.save(customerInfoExtra));
    }

    /**
     * 数据临时表修改
     *
     * @param customerInfoExtra 数据临时表
     * @return R
     */
    @SysLog("修改数据临时表")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_customerinfoextra_edit')")
    public R updateById(@RequestBody CustomerInfoExtra customerInfoExtra) {
        return R.ok(customerInfoExtraService.updateById(customerInfoExtra));
    }

    /**
     * 数据临时表删除
     *
     * @param id
     * @return R
     */
    @SysLog("删除数据临时表")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_customerinfoextra_del')")
    public R removeById(@PathVariable String id) {
        return R.ok(customerInfoExtraService.removeById(id));
    }

    /**
     * 去除重复数据
     *
     * @param importRecord
     * @return R
     */
    @SysLog("去除重复数据")
    @PostMapping("/dataDeDuplication")
    public R dataDeDuplication(@RequestBody ImportRecord importRecord) {
        return R.ok(customerInfoExtraService.dataDeDuplication(importRecord));
    }

}
