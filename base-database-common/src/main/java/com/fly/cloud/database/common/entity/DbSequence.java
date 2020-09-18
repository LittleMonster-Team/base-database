
package com.fly.cloud.database.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
    import java.time.LocalDateTime;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;

/**
 * 序列
 *
 * @author xux
 * @date 2020-09-01 14:17:31
 */
@Data
@TableName("db_sequence")
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "序列")
public class DbSequence extends Model<DbSequence> {
    private static final long serialVersionUID=1L;

    /**
     * 编号名称
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "编号名称")
    private String name;
    /**
     * 当前值
     */
    @ApiModelProperty(value = "当前值")
    private Long currentValue;
    /**
     * 增值
     */
    @ApiModelProperty(value = "增值")
    private Integer increment;

}
