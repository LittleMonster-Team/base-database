
package com.fly.cloud.database.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.common.entity.CustomerInfoVersion;
import com.fly.cloud.database.admin.service.CustomerInfoVersionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;

/**
 * 客户信息版本表
 *
 * @author xux
 * @date 2020-09-09 10:07:55
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/customerinfoversion")
@Api(value = "customerinfoversion", tags = "客户信息版本表管理")
public class CustomerInfoVersionController {

    private final CustomerInfoVersionService customerInfoVersionService;

    /**
     * 分页列表
     * @param page 分页对象
     * @param customerInfoVersion 客户信息版本表
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_customerinfoversion_index')")
    public R getPage(Page page, CustomerInfoVersion customerInfoVersion) {
        return R.ok(customerInfoVersionService.page(page, Wrappers.query(customerInfoVersion)));
    }

    /**
     * 客户信息版本表查询
     * @param id
     * @return R
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_customerinfoversion_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(customerInfoVersionService.getById(id));
    }

    /**
     * 客户信息版本表新增
     * @param customerInfoVersion 客户信息版本表
     * @return R
     */
    @SysLog("新增客户信息版本表")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_customerinfoversion_add')")
    public R save(@RequestBody CustomerInfoVersion customerInfoVersion) {
        return R.ok(customerInfoVersionService.save(customerInfoVersion));
    }

    /**
     * 客户信息版本表修改
     * @param customerInfoVersion 客户信息版本表
     * @return R
     */
    @SysLog("修改客户信息版本表")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_customerinfoversion_edit')")
    public R updateById(@RequestBody CustomerInfoVersion customerInfoVersion) {
        return R.ok(customerInfoVersionService.updateById(customerInfoVersion));
    }

    /**
     * 客户信息版本表删除
     * @param id
     * @return R
     */
    @SysLog("删除客户信息版本表")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_customerinfoversion_del')")
    public R removeById(@PathVariable String id) {
        return R.ok(customerInfoVersionService.removeById(id));
    }

}
