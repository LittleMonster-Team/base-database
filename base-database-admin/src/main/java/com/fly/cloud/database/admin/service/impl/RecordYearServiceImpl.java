
package com.fly.cloud.database.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.database.common.entity.RecordYear;
import com.fly.cloud.database.admin.mapper.RecordYearMapper;
import com.fly.cloud.database.admin.service.RecordYearService;
import com.fly.cloud.database.common.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 记录建表年份
 *
 * @author xux
 * @date 2020-09-15 10:41:32
 */
@Service
public class RecordYearServiceImpl extends ServiceImpl<RecordYearMapper, RecordYear> implements RecordYearService {
    /**
     * 获取建表年份
     *
     * @param organId 商户id
     * @return
     */
    @Override
    @Transactional
    public Map<String, String> getYearData(String organId) {
        Map<String, String> map = new HashMap<String, String>();
        RecordYear recordYear = this.getOne(Wrappers.<RecordYear>query().lambda()
                .eq(RecordYear::getOrganId, organId)
        );
        if (recordYear != null) {
            map.put("yearData", recordYear.getYearData());
            map.put("yearInfo", recordYear.getYearInfo());
            return map;
        }
        map.put("yearData", "");
        map.put("yearInfo", "");
        return map;
    }

    /**
     * 更新表数据
     *
     * @param organId 商户id
     */
    @Override
    @Transactional
    public void updateYearInfo(String organId) {
        RecordYear recordYear = this.getOne(Wrappers.<RecordYear>query().lambda()
                .eq(RecordYear::getOrganId, organId)
        );
        if (recordYear != null) {
            recordYear.setYearInfo(recordYear.getYearData() + ",");
            recordYear.setYearData(DateUtils.getYear());
            recordYear.setUpdateTime(DateUtils.getCurrentDate());
            this.updateById(recordYear);
        }
    }

    /**
     * 新增表数据
     *
     * @param organId 商户id
     */
    @Override
    @Transactional
    public void saveYearInfo(String organId) {
        this.remove(Wrappers.<RecordYear>query().lambda()
                .eq(RecordYear::getOrganId, organId));
        RecordYear recordYear = new RecordYear();
        recordYear.setYearData(DateUtils.getYear());
        recordYear.setYearInfo(DateUtils.getYear());
        recordYear.setOrganId(organId);
        recordYear.setCreateTime(DateUtils.getCurrentDate());
        this.save(recordYear);
    }
}
