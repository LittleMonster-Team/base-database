
package com.fly.cloud.database.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.database.common.entity.DbSequence;
import com.fly.cloud.database.admin.mapper.DbSequenceMapper;
import com.fly.cloud.database.admin.service.DbSequenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 序列
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
@Service
public class DbSequenceServiceImpl extends ServiceImpl<DbSequenceMapper, DbSequence> implements DbSequenceService {
    /**
     * 保存序列
     *
     * @param seqName  序列名
     * @param startNum 初始值
     * @param step     增值数
     * @return
     */
    @Override
    public boolean saveSeqByName(String seqName, int startNum, int step) {
        return this.baseMapper.saveSeqByName(seqName, startNum, step);
    }

    /**
     * 获取序列
     *
     * @param seqName  序列名
     * @param fillNum  位数
     * @param fillChar 初始值
     * @return
     */
    @Override
    public String getSeqByName(String seqName, int fillNum, String fillChar) {
        return this.baseMapper.getSeqByName(seqName, fillNum, fillChar);
    }

    /**
     * 更新序列
     *
     * @param seqName 序列名
     */
    @Override
    @Transactional
    public void updateDbSequence(String seqName) {
        DbSequence dbSequence = this.getById(seqName);
        if (dbSequence != null) {
            dbSequence.setCurrentValue(dbSequence.getCurrentValue() + 1);
            this.updateById(dbSequence);
        }
    }
}
