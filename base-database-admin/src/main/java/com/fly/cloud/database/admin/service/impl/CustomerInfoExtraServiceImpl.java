
package com.fly.cloud.database.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.common.security.util.SecurityUtils;
import com.fly.cloud.database.admin.mapper.CustomerInfoExtraMapper;
import com.fly.cloud.database.admin.service.CustomerInfoExtraService;
import com.fly.cloud.database.admin.service.CustomerInfoService;
import com.fly.cloud.database.admin.service.ImportRecordService;
import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.CustomerInfoExtra;
import com.fly.cloud.database.common.entity.ImportRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据临时表
 *
 * @author xux
 * @date 2020-09-28 13:25:03
 */
@Service
public class CustomerInfoExtraServiceImpl extends ServiceImpl<CustomerInfoExtraMapper, CustomerInfoExtra> implements CustomerInfoExtraService {
    @Autowired
    private CustomerInfoService customerInfoService;
    @Autowired
    private ImportRecordService importRecordService;

    /**
     * 将数据保存进临时表
     *
     * @param sList
     */
    @Override
    @Transactional
    public void saveExtraInfo(List<CustomerInfo> sList, String relationText) {
        ArrayList<CustomerInfoExtra> list = new ArrayList<>();
        sList.forEach(info -> {
            CustomerInfoExtra extra = new CustomerInfoExtra();
            BeanUtils.copyProperties(info, extra);
            extra.setRelationText(relationText);
            list.add(extra);
            // 批量插入数据
            if (list.size() % 500 == 0) {
                this.saveBatch(list);
                list.clear();
            }
        });
        if (list.size() > 0) {
            this.saveBatch(list);
            list.clear();
        }
    }

    @Override
    @Transactional
    public Map<String, Object> dataDeDuplication(ImportRecord importRecord) {
        String organId = SecurityUtils.getUser().getOrganId();
        List<CustomerInfo> slist = new ArrayList<CustomerInfo>();
        List<CustomerInfoExtra> list = this.list(Wrappers.<CustomerInfoExtra>query().lambda()
                .eq(CustomerInfoExtra::getRelationText, importRecord.getRelationText()));
        int successNum = 0;
        int differenceNum = 0;
        if (list != null && list.size() > 0) {
            for (CustomerInfoExtra extra : list) {
                CustomerInfo info = new CustomerInfo();
                // 转换数据
                BeanUtils.copyProperties(extra, info);
                slist.add(info);
            }
            // 给地区与性别信息赋值
            slist = customerInfoService.dataAssignment(slist);
            // 获取成功与差异数据
            Map<String, Object> resultMap = customerInfoService.getDifferenceInfoData(slist, organId);
            // 成功数据
            successNum = (int) resultMap.get("successListNum");
            differenceNum = (int) resultMap.get("differenceListNum");
            // 差异数据
            List<CustomerInfo> differenceList = (List<CustomerInfo>) resultMap.get("differenceList");
            // 将差异数据添加进表中
            customerInfoService.saveInfoTemporary(differenceList, CommonConstants.DIFFERENCE_DATA);
        }
        // 创建返回结果集
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("successNum", successNum);
        map.put("differenceNum", differenceNum);
        //  修改导入数据状态
        importRecord.setDuplicateRemoval(CommonConstants.WEIGHT_REMOVED);
        importRecordService.updateById(importRecord);
        // 删除临时表数据
        this.remove(Wrappers.<CustomerInfoExtra>query().lambda()
                .eq(CustomerInfoExtra::getRelationText, importRecord.getRelationText()));
        return map;
    }
}

