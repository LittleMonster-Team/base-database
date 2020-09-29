
package com.fly.cloud.database.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.common.entity.ImportRecord;
import com.fly.cloud.database.admin.service.ImportRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;

/**
 * 导入记录表
 *
 * @author xux
 * @date 2020-09-29 09:30:17
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/importrecord")
@Api(value = "importrecord", tags = "导入记录表管理")
public class ImportRecordController {

    private final ImportRecordService importRecordService;

    /**
     * 分页列表
     * @param page 分页对象
     * @param importRecord 导入记录表
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_importrecord_index')")
    public R getPage(Page page, ImportRecord importRecord) {
        return R.ok(importRecordService.page(page, Wrappers.query(importRecord)));
    }

    /**
     * 导入记录表查询
     * @param id
     * @return R
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_importrecord_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(importRecordService.getById(id));
    }

    /**
     * 导入记录表新增
     * @param importRecord 导入记录表
     * @return R
     */
    @SysLog("新增导入记录表")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_importrecord_add')")
    public R save(@RequestBody ImportRecord importRecord) {
        return R.ok(importRecordService.save(importRecord));
    }

    /**
     * 导入记录表修改
     * @param importRecord 导入记录表
     * @return R
     */
    @SysLog("修改导入记录表")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_importrecord_edit')")
    public R updateById(@RequestBody ImportRecord importRecord) {
        return R.ok(importRecordService.updateById(importRecord));
    }

    /**
     * 导入记录表删除
     * @param id
     * @return R
     */
    @SysLog("删除导入记录表")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_importrecord_del')")
    public R removeById(@PathVariable String id) {
        return R.ok(importRecordService.removeById(id));
    }

}
