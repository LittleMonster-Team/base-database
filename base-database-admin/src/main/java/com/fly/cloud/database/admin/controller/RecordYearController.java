
package com.fly.cloud.database.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.common.entity.RecordYear;
import com.fly.cloud.database.admin.service.RecordYearService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;

/**
 * 记录建表年份
 *
 * @author xux
 * @date 2020-09-15 10:41:32
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/recordyear")
@Api(value = "recordyear", tags = "记录建表年份管理")
public class RecordYearController {

    private final RecordYearService recordYearService;

    /**
     * 分页列表
     * @param page 分页对象
     * @param recordYear 记录建表年份
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_recordyear_index')")
    public R getPage(Page page, RecordYear recordYear) {
        return R.ok(recordYearService.page(page, Wrappers.query(recordYear)));
    }

    /**
     * 记录建表年份查询
     * @param id
     * @return R
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_recordyear_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(recordYearService.getById(id));
    }

    /**
     * 记录建表年份新增
     * @param recordYear 记录建表年份
     * @return R
     */
    @SysLog("新增记录建表年份")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_recordyear_add')")
    public R save(@RequestBody RecordYear recordYear) {
        return R.ok(recordYearService.save(recordYear));
    }

    /**
     * 记录建表年份修改
     * @param recordYear 记录建表年份
     * @return R
     */
    @SysLog("修改记录建表年份")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_recordyear_edit')")
    public R updateById(@RequestBody RecordYear recordYear) {
        return R.ok(recordYearService.updateById(recordYear));
    }

    /**
     * 记录建表年份删除
     * @param id
     * @return R
     */
    @SysLog("删除记录建表年份")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_recordyear_del')")
    public R removeById(@PathVariable String id) {
        return R.ok(recordYearService.removeById(id));
    }

}
