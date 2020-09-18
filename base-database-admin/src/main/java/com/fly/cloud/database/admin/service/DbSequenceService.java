
package com.fly.cloud.database.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fly.cloud.database.common.entity.DbSequence;

/**
 * 序列
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
public interface DbSequenceService extends IService<DbSequence> {


    /**
     * 保存序列
     *
     * @param seqName  序列名
     * @param startNum 初始值
     * @param step     增值数
     * @return
     */
    boolean saveSeqByName(String seqName, int startNum, int step);

    /**
     * 获取序列
     *
     * @param seqName  序列名
     * @param fillNum  位数
     * @param fillChar 初始值
     * @return
     */
    String getSeqByName(String seqName, int fillNum, String fillChar);

    /**
     * 更新序列
     *
     * @param seqName 序列名
     */
    void updateDbSequence(String seqName);
}
