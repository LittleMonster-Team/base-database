
package com.fly.cloud.database.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.cloud.database.common.entity.DbSequence;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 序列
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
public interface DbSequenceMapper extends BaseMapper<DbSequence> {
    /**
     * 保存序列
     *
     * @param seqName  序列名
     * @param startNum 初始值
     * @param step     增值数
     * @return
     */
    @Insert("insert into db_sequence (name, current_value, increment) SELECT '${name}',${startNum},${step} FROM dual WHERE not exists (select * from db_sequence\n" +
            "where name = '${name}')")
    boolean saveSeqByName(@Param("name") String seqName, @Param("startNum") int startNum, @Param("step") int step);


    /**
     * 获取序列号
     *
     * @param seqName  序列名
     * @param fillNum  位数
     * @param fillChar 初始值
     * @return
     */
    @Select("select lpad(nextval('${name}'),${fillNum},'${fillChar}')")
    String getSeqByName(@Param("name") String seqName, @Param("fillNum") int fillNum, @Param("fillChar") String fillChar);
}
