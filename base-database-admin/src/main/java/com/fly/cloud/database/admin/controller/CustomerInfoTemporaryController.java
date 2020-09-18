
package com.fly.cloud.database.admin.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.database.admin.service.CustomerInfoTemporaryService;
import com.fly.cloud.database.common.entity.CustomerInfoTemporary;
import com.fly.cloud.database.common.util.DateUtils;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户信息临时表
 *
 * @author xux
 * @date 2020-09-09 10:08:12
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/customerinfotemporary")
@Api(value = "customerinfotemporary", tags = "客户信息临时表管理")
public class CustomerInfoTemporaryController {

    private final CustomerInfoTemporaryService customerInfoTemporaryService;

    /**
     * 分页列表
     *
     * @param page                  分页对象
     * @param customerInfoTemporary 客户信息临时表
     * @return
     */
    @GetMapping("/page")
    public R getPage(Page page, CustomerInfoTemporary customerInfoTemporary) {
        String month = "";
        if (StringUtils.isNotBlank(customerInfoTemporary.getMonth())) {
            month = DateUtils.padDatazero(Integer.parseInt(customerInfoTemporary.getMonth()));
        }
        return R.ok(customerInfoTemporaryService.getPage(page, customerInfoTemporary, month));
    }

    /**
     * 筛选客户信息列表
     *
     * @param obj 筛选信息
     * @return
     */
    @PostMapping("/searchTemporaryInfo")
    public R searchTemporaryInfo(@RequestBody JSONObject obj) {
        return R.ok(customerInfoTemporaryService.searchTemporaryInfo(obj));
    }

    /**
     * 客户信息临时表查询
     *
     * @param id
     * @return R
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable("id") String id) {
        return R.ok(customerInfoTemporaryService.getById(id));
    }

    /**
     * 客户信息临时表新增
     *
     * @param customerInfoTemporary 客户信息临时表
     * @return R
     */
    @SysLog("新增客户信息临时表")
    @PostMapping
    public R save(@RequestBody CustomerInfoTemporary customerInfoTemporary) {
        return R.ok(customerInfoTemporaryService.save(customerInfoTemporary));
    }

    /**
     * 客户信息临时表修改
     *
     * @param customerInfoTemporary 客户信息临时表
     * @return R
     */
    @SysLog("修改客户信息临时表")
    @PutMapping
    public R updateById(@RequestBody CustomerInfoTemporary customerInfoTemporary) {
        return customerInfoTemporaryService.updateTemporary(customerInfoTemporary);
    }

    /**
     * 客户信息临时表删除
     *
     * @param id
     * @return R
     */
    @SysLog("删除客户信息临时表")
    @DeleteMapping("/{id}")
    public R removeById(@PathVariable String id) {
        return R.ok(customerInfoTemporaryService.removeById(id));
    }

    /**
     * 导入差异数据
     *
     * @param infoList
     * @return
     */
    @SysLog("导入差异数据")
    @PostMapping("/importDifferenceData")
    public R importDifferenceData(@RequestBody List<CustomerInfoTemporary> infoList) {
        return customerInfoTemporaryService.importDifferenceData(infoList);
    }


    /**
     * 导入失败数据
     *
     * @param infoList
     * @return
     */
    @SysLog("导入失败数据")
    @PostMapping("/importFailData")
    public R importFailData(@RequestBody List<CustomerInfoTemporary> infoList) {
        return customerInfoTemporaryService.importFailData(infoList);
    }
}
