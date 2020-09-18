
package com.fly.cloud.database.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.database.admin.mapper.CustomerInfoVersionMapper;
import com.fly.cloud.database.admin.service.CustomerInfoTemporaryService;
import com.fly.cloud.database.admin.service.CustomerInfoVersionService;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.CustomerInfoVersion;
import com.fly.cloud.database.common.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 客户信息版本表
 *
 * @author xux
 * @date 2020-09-09 10:07:55
 */
@Service
public class CustomerInfoVersionServiceImpl extends ServiceImpl<CustomerInfoVersionMapper, CustomerInfoVersion> implements CustomerInfoVersionService {

    @Autowired
    private CustomerInfoTemporaryService infoTemporaryService;

    /**
     * 批量添加数据
     *
     * @param infoList 数据
     * @return
     */
    @Override
    @Transactional
    public R saveCustomerInfoVersion(List<CustomerInfo> infoList) {
        List<CustomerInfoVersion> list = new ArrayList<CustomerInfoVersion>();
        List<String> idList = new ArrayList<String>();
        if (infoList != null && infoList.size() > 0) {
            infoList.forEach(temporary -> {
                CustomerInfoVersion info = new CustomerInfoVersion();
                // 转换数据类型
                BeanUtils.copyProperties(temporary, info);
                info.setId(UUID.randomUUID().toString().replace("-", ""));
                info.setVersion(info.getVersion() + 1);
                info.setCreateTime(DateUtils.getCurrentDate());
                list.add(info);
                // 添加临时表id
                idList.add(temporary.getId());
            });
        }
        // 删除临时表数据
        infoTemporaryService.removeByIds(idList);
        // 批量更新数据
        return R.ok(this.saveBatch(list));
    }

    /**
     * 根据车牌号查询信息
     *
     * @param carNum 车牌号
     * @return
     */
    @Override
    @Transactional
    public List<CustomerInfo> viewDetailsInfo(String carNum) {
        List<CustomerInfo> list = new ArrayList<CustomerInfo>();
        if (StringUtils.isNotBlank(carNum)) {
            List<CustomerInfoVersion> infoList = this.list(Wrappers.<CustomerInfoVersion>query().lambda()
                    .eq(CustomerInfoVersion::getCarNum, carNum));
            if (infoList != null && infoList.size() > 0) {
                infoList.forEach(version -> {
                    CustomerInfo info = new CustomerInfo();
                    // 转换数据类型
                    BeanUtils.copyProperties(version, info);
                    list.add(info);
                });
            }
            return list;
        } else {
            return null;
        }
    }
}
