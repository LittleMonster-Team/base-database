
package com.fly.cloud.database.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.common.entity.DbSequence;
import com.fly.cloud.database.admin.service.DbSequenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.annotations.Api;

/**
 * 序列
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/dbsequence")
@Api(value = "dbsequence", tags = "序列管理")
public class DbSequenceController {

    private final DbSequenceService dbSequenceService;

    /**
     * 分页列表
     * @param page 分页对象
     * @param dbSequence 序列
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_dbsequence_index')")
    public R getPage(Page page, DbSequence dbSequence) {
        return R.ok(dbSequenceService.page(page, Wrappers.query(dbSequence)));
    }

    /**
     * 序列查询
     * @param name
     * @return R
     */
    @GetMapping("/{name}")
    @PreAuthorize("@ato.hasAuthority('database_dbsequence_get')")
    public R getById(@PathVariable("name") String name) {
        return R.ok(dbSequenceService.getById(name));
    }

    /**
     * 序列新增
     * @param dbSequence 序列
     * @return R
     */
    @SysLog("新增序列")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_dbsequence_add')")
    public R save(@RequestBody DbSequence dbSequence) {
        return R.ok(dbSequenceService.save(dbSequence));
    }

    /**
     * 序列修改
     * @param dbSequence 序列
     * @return R
     */
    @SysLog("修改序列")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_dbsequence_edit')")
    public R updateById(@RequestBody DbSequence dbSequence) {
        return R.ok(dbSequenceService.updateById(dbSequence));
    }

    /**
     * 序列删除
     * @param name
     * @return R
     */
    @SysLog("删除序列")
    @DeleteMapping("/{name}")
    @PreAuthorize("@ato.hasAuthority('database_dbsequence_del')")
    public R removeById(@PathVariable String name) {
        return R.ok(dbSequenceService.removeById(name));
    }

}
