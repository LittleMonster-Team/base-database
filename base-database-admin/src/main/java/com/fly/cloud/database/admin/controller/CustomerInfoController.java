
package com.fly.cloud.database.admin.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.log.annotation.SysLog;
import com.fly.cloud.common.security.util.SecurityUtils;
import com.fly.cloud.database.admin.service.CustomerInfoService;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.vo.CustomerInfoVO;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 客户信息
 *
 * @author xux
 * @date 2020-09-01 14:17:22
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/customerinfo")
@Api(value = "customerinfo", tags = "客户信息管理")
public class CustomerInfoController {

    private final CustomerInfoService customerInfoService;

    /**
     * 分页列表
     *
     * @param page         分页对象
     * @param customerInfo 客户信息
     * @return
     */
    @GetMapping("/page")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_index')")
    public R getPage(Page page, CustomerInfo customerInfo) {
        return R.ok(customerInfoService.page(page, Wrappers.query(customerInfo)));
    }

    /**
     * 筛选客户信息列表
     *
     * @param obj 筛选信息
     * @return
     */
    @SysLog("筛选客户信息列表")
    @PostMapping("/screenCustomerInfo")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_screen')")
    public R screenCustomerInfo(@RequestBody JSONObject obj) {
        Page page = customerInfoService.screenCustomerInfo(obj);
        return R.ok(page);
    }

    /**
     * 搜索客户信息列表
     *
     * @param obj 搜索信息
     * @return
     */
    @SysLog("搜索客户信息列表")
    @PostMapping("/searchCustomerInfo")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_search')")
    public R searchCustomerInfo(@RequestBody JSONObject obj) {
        Page page = customerInfoService.searchCustomerInfo(obj);
        return R.ok(page);
    }

    /**
     * 客户信息查询
     *
     * @param id
     * @return R
     */
    @SysLog("客户信息查询")
    @GetMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_get')")
    public R getById(@PathVariable("id") String id) {
        return R.ok(customerInfoService.getById(id));
    }

    /**
     * 客户信息新增
     *
     * @param customerInfo 客户信息
     * @return R
     */
    @SysLog("新增客户信息")
    @PostMapping
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_add')")
    public R save(@RequestBody CustomerInfo customerInfo) {
        return R.ok(customerInfoService.save(customerInfo));
    }

    /**
     * 客户信息修改
     *
     * @param customerInfo 客户信息
     * @return R
     */
    @SysLog("修改客户信息")
    @PutMapping
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_edit')")
    public R updateById(@RequestBody CustomerInfo customerInfo) {
        return customerInfoService.updateCustomerInfo(customerInfo);
    }

    /**
     * 客户信息删除
     *
     * @param id
     * @return R
     */
    @SysLog("删除客户信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_del')")
    public R removeById(@PathVariable String id) {
        return R.ok(customerInfoService.removeById(id));
    }

    /**
     * 导入上传数据
     *
     * @param obj
     * @return
     */
    @SysLog("导入上传数据")
    @PostMapping("/importExcelData")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_import')")
    public R importExcelData(@RequestBody JSONObject obj) {
        String organId = SecurityUtils.getUser().getOrganId();
        return customerInfoService.importExcelData(obj.getStr("fileLink"), obj.getStr("fileNames"), organId);
    }

    /**
     * 将数据转为Excel文件并保存起来
     * 返回文件信息
     *
     * @param infoList 数据
     * @return
     */
    @SysLog("导出数据")
    @PostMapping("/exprotExcelData")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_exprot')")
    public R exprotExcelData(@RequestBody List<CustomerInfo> infoList) {
        return customerInfoService.exprotExcelData(infoList);
    }

    /**
     * 下载文件保存本地
     *
     * @param obj      文件存放路径
     * @param response 数据流
     * @return
     */
    @SysLog("下载文件保存本地")
    @PostMapping("/downloadExcelFiles")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_download')")
    public R downloadExcelFiles(@RequestBody JSONObject obj, HttpServletResponse response) {
        return customerInfoService.downloadExcelFiles(obj.getStr("fileLink"), response);
    }

    /**
     * 查询客户信息详情
     *
     * @param params 参数
     * @return
     */
    @SysLog("查询客户信息详情")
    @PostMapping("/viewDetailsInfo")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_view')")
    public R viewDetailsInfo(@RequestBody JSONObject params) {
        return customerInfoService.viewDetailsInfo(params);
    }

    /**
     * 去除客户信息
     *
     * @param customerInfo 参数
     * @return
     */
    @SysLog("去除客户信息")
    @PostMapping("/removeCustomerInfo")
    @PreAuthorize("@ato.hasAuthority('database_customerinfo_remove')")
    public R removeCustomerInfo(@RequestBody CustomerInfoVO customerInfo) {
        return customerInfoService.removeCustomerInfo(customerInfo);
    }
}
