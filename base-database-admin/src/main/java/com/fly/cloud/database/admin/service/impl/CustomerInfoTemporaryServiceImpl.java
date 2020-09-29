
package com.fly.cloud.database.admin.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.security.util.SecurityUtils;
import com.fly.cloud.database.admin.mapper.CustomerInfoTemporaryMapper;
import com.fly.cloud.database.admin.service.CustomerInfoService;
import com.fly.cloud.database.admin.service.CustomerInfoTemporaryService;
import com.fly.cloud.database.admin.service.CustomerInfoVersionService;
import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.CustomerInfoTemporary;
import com.fly.cloud.database.common.util.CheckIdCard;
import com.fly.cloud.database.common.util.DateUtils;
import com.fly.cloud.database.common.util.JsonUtils;
import com.fly.cloud.database.common.util.ValidatorUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据失败与重复表
 *
 * @author xux
 * @date 2020-09-09 10:08:12
 */
@Service
public class CustomerInfoTemporaryServiceImpl extends ServiceImpl<CustomerInfoTemporaryMapper, CustomerInfoTemporary> implements CustomerInfoTemporaryService {

    @Autowired
    private CustomerInfoService customerInfoService;
    @Autowired
    private CustomerInfoVersionService infoVersionService;

    /**
     * 分页列表
     *
     * @param page                  分页
     * @param customerInfoTemporary 客户信息
     * @param month                 月份
     * @return
     */
    @Override
    @Transactional
    public Page getPage(Page page, CustomerInfoTemporary customerInfoTemporary, String month) {
        Page dataPage = this.page(page, Wrappers.<CustomerInfoTemporary>query().lambda()
                .eq(CustomerInfoTemporary::getDataType, customerInfoTemporary.getDataType())
                .like(customerInfoTemporary.getVersion() != null, CustomerInfoTemporary::getVersion, customerInfoTemporary.getVersion())
                .like(StringUtils.isNotBlank(customerInfoTemporary.getGender()), CustomerInfoTemporary::getGender, customerInfoTemporary.getGender())
                .like(StringUtils.isNotBlank(customerInfoTemporary.getAccidentsNum()), CustomerInfoTemporary::getAccidentsNum, customerInfoTemporary.getAccidentsNum())
                .like(StringUtils.isNotBlank(customerInfoTemporary.getServiceLife()), CustomerInfoTemporary::getServiceLife, customerInfoTemporary.getServiceLife())
                .like(StringUtils.isNotBlank(customerInfoTemporary.getFirstDate()), CustomerInfoTemporary::getFirstDate, customerInfoTemporary.getFirstDate())
                .like(StringUtils.isNotBlank(customerInfoTemporary.getMonth()), CustomerInfoTemporary::getMonth, month)
        );
        return dataPage;
    }

    /**
     * 分页搜索数据
     *
     * @param obj 搜索数据
     * @return
     */
    @Override
    @Transactional
    public Page searchTemporaryInfo(JSONObject obj) {
        // Obj转实体类
        Page page = JsonUtils.ObjToEntity(obj.getObj("page"), Page.class);
        String condition = obj.getStr("condition");
        String dataType = obj.getStr("dataType");
        Page pageDate = this.page(page, Wrappers.<CustomerInfoTemporary>query().lambda()
                .eq(CustomerInfoTemporary::getDataType, dataType)
                .like(StringUtils.isNotBlank(condition), CustomerInfoTemporary::getCustomerName, condition)
                .or()
                .like(StringUtils.isNotBlank(condition), CustomerInfoTemporary::getPhone, condition));
        return pageDate;
    }

    /**
     * 将数据添加进临时表中
     *
     * @param dataList 数据
     * @param dataType 数据类型
     */
    @Override
    @Transactional
    public void addTemporaryData(List<CustomerInfo> dataList, String dataType) {
        List<CustomerInfoTemporary> list = new ArrayList<CustomerInfoTemporary>();
        dataList.stream().map(info -> {
            CustomerInfoTemporary temporary = new CustomerInfoTemporary();
            // 转换数据
            BeanUtils.copyProperties(info, temporary);
            // 设置id
            temporary.setId(UUID.randomUUID().toString().replace("-", ""));
            // 添加id
            temporary.setParentId(info.getId());
            // 设置初登日期的月份，用于后续查询
            temporary.setMonth(StringUtils.isNotBlank(info.getFirstDate()) && !"无".equals(info.getFirstDate()) ? info.getFirstDate().split("-")[1] : "");
            // 记录数据类型
            temporary.setDataType(dataType);
            list.add(temporary);
            // 批量插入数据
            if (list.size() % 500 == 0) {
                this.saveBatch(list);
                list.clear();
            }
            return info;
        }).collect(Collectors.toList());
        if (list.size() > 0) {
            this.saveBatch(list);
            list.clear();
        }
    }

    /**
     * 导入差异数据
     *
     * @param infoList 数据
     * @return
     */
    @Override
    @Transactional
    public R importDifferenceData(List<CustomerInfoTemporary> infoList) {
        // 需要更新的数据
        List<CustomerInfoTemporary> updateInfoList = new LinkedList<CustomerInfoTemporary>();
        // 需要添加进版本库的数据
        List<CustomerInfo> versionInfoList = new LinkedList<CustomerInfo>();
        // 需要移除临时表的数据
        List<String> idList = new ArrayList<String>();
        infoList.forEach(temporary -> {
            if (temporary.getVersion() == CommonConstants.NEW_DATA) {// 新数据
                // 如果数据修改了更新表数据否则不做操作
                // 将表中数据查询出来
                List<CustomerInfo> dataList = customerInfoService.getCustomerInfoByCarNum(temporary.getCarNum(), temporary.getTableInfo());
                if (dataList != null && dataList.size() > 0) {
                    // 将表中数据添加进版本表中
                    versionInfoList.add(dataList.get(0));
                } else {
                    CustomerInfo info = new CustomerInfo();
                    // 转换数据类型
                    BeanUtils.copyProperties(temporary, info);
                    // 设置id
                    info.setId(UUID.randomUUID().toString().replace("-", ""));
                    versionInfoList.add(info);
                }
                // 更新表数据
                updateInfoList.add(temporary);
            } else {// 旧数据
                // 如果数据修改了更新表数据否则将数据添加进版本表中
                if (StringUtils.isNotBlank(temporary.getEditFlag()) && temporary.getEditFlag().equals(CommonConstants.EDIT_FLAG)) {
                    // 将表中数据查询出来
                    List<CustomerInfo> dataList = customerInfoService.getCustomerInfoByCarNum(temporary.getCarNum(), temporary.getTableInfo());
                    if (dataList != null && dataList.size() > 0) {
                        // 将表中数据添加进版本表中
                        versionInfoList.add(dataList.get(0));
                    }
                    // 更新表数据
                    updateInfoList.add(temporary);
                } else {
                    // 添加需要移除临时表的数据
                    idList.add(temporary.getId());
                }
            }
        });
        if (updateInfoList != null && updateInfoList.size() > 0) {
            // 更新表数据
            updateInfoList.forEach(temporary -> {
                CustomerInfo info = new CustomerInfo();
                // 转换数据类型
                BeanUtils.copyProperties(temporary, info);
                info.setId(temporary.getParentId());
                // 更新表数据
                R result = customerInfoService.updateCustomerInfo(info);
                if (result.getCode() != CommonConstants.ERROR_RESULT) {
                    // 添加需要移除临时表的数据
                    idList.add(temporary.getId());
                } else {
                    try {
                        throw new Exception("导入失败");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (versionInfoList != null && versionInfoList.size() > 0) {
            // 数据添加进版本表中
            infoVersionService.saveCustomerInfoVersion(versionInfoList);
        }
        if (idList != null && idList.size() > 0) {
            // 删除临时表数据
            this.removeByIds(idList);
        }
        return R.ok();
    }

    /**
     * 修改客户信息临时表
     *
     * @param customerInfoTemporary 数据
     * @return
     */
    @Override
    @Transactional
    public R updateTemporary(CustomerInfoTemporary customerInfoTemporary) {
        // 检验数据格式
        R result = this.verificationInfo(customerInfoTemporary);
        if (result.getCode() != CommonConstants.ERROR_RESULT) {
            // 更改修改标识
            customerInfoTemporary.setEditFlag(CommonConstants.EDIT_FLAG);
            // 设置更新时间
            customerInfoTemporary.setUpdateTime(DateUtils.getCurrentDate());
            // 清空错误信息
            customerInfoTemporary.setErrorMsg("");
            return R.ok(this.updateById(customerInfoTemporary));
        } else {
            return result;
        }
    }

    /**
     * 导入失败数据
     *
     * @param customerInfoList 数据
     * @return
     */
    @Override
    @Transactional
    public R importFailData(List<CustomerInfoTemporary> customerInfoList) {
        // 获取登录的商户信息
        String organId = SecurityUtils.getUser().getOrganId();
        // 存放客户信息数据
        List<CustomerInfo> list = new ArrayList<CustomerInfo>();
        // 需要移除临时表的数据
        List<String> idList = new ArrayList<String>();
        int num = 0;
        for (CustomerInfoTemporary temporary : customerInfoList) {
            // 校验数据格式
            R result = this.verificationInfo(temporary);
            if (result.getCode() == CommonConstants.ERROR_RESULT) {
                // 记录错误信息
                num++;
            } else {
                CustomerInfo info = new CustomerInfo();
                BeanUtils.copyProperties(temporary, info);
                list.add(info);
            }
            idList.add(temporary.getId());
        }
        // 校验数据是否有错
        if (num != 0) {
            return R.failed("所选信息中含有错误信息");
        } else {
            // 创建差异数据集合
            List<CustomerInfo> differenceList = new ArrayList<CustomerInfo>();
            if (list != null && list.size() > 0) {
                // 给地区与性别信息赋值
                list = customerInfoService.dataAssignment(list);
                // 获取成功与差异数据
                Map<String, Object> map = customerInfoService.getDifferenceInfoData(list, organId);
                // 获取差异数据
                differenceList = (List<CustomerInfo>) map.get("differenceList");
            }
            // 删除临时表数据
            this.removeByIds(idList);
            return R.ok(differenceList);
        }
    }

    /**
     * 清空临时表数据
     */
    @Override
    @Transactional
    public void clearTableData() {
        this.baseMapper.clearTableData();
    }

    /**
     * 检验数据格式
     *
     * @param info 数据
     * @return
     */
    private R verificationInfo(CustomerInfoTemporary info) {
        if (StringUtils.isBlank(info.getVinNum())) {
            return R.failed(CommonConstants.ERROR_RESULT, "VIN码不可为空");
        }
        if (!ValidatorUtil.CarNum(info.getCarNum())) {
            return R.failed(CommonConstants.ERROR_RESULT, "车牌号格式错误");
        }
        if (StringUtils.isNotBlank(info.getIdCard()) && !info.getIdCard().equals(CommonConstants.DEFAULT_INITIAL_VALUE_NOTHING)) {
            if (!CheckIdCard.check(info.getIdCard())) {
                return R.failed(CommonConstants.ERROR_RESULT, "证件号格式错误");
            }
        }
        if (StringUtils.isNotBlank(info.getPhone()) && !"无".equals(info.getPhone())) {
            if (!ValidatorUtil.isMobile(info.getPhone())) {
                return R.failed(CommonConstants.ERROR_RESULT, "手机号格式错误");
            }
        }
        return R.ok();
    }

}
