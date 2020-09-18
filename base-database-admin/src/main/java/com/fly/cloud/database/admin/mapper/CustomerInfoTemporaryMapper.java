
package com.fly.cloud.database.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.cloud.database.common.entity.CustomerInfoTemporary;

/**
 * 客户信息临时表
 *
 * @author xux
 * @date 2020-09-09 10:08:12
 */
public interface CustomerInfoTemporaryMapper extends BaseMapper<CustomerInfoTemporary> {
    /**
     * 清空临时表数据
     */
    void clearTableData();
}
