
package com.fly.cloud.database.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 记录建表年份
 *
 * @author xux
 * @date 2020-09-15 10:41:32
 */
@Data
@TableName("record_year")
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "记录建表年份")
public class RecordYear extends Model<RecordYear> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 当前年份
     */
    @ApiModelProperty(value = "当前年份")
    private String yearData;
    /**
     * 年份
     */
    @ApiModelProperty(value = "年份")
    private String yearInfo;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private String updateTime;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String creatorUser;
    /**
     * 删除标识(0-未删除 1-已删除)
     */
    @ApiModelProperty(value = "删除标识(0-未删除 1-已删除)")
    private String delFlag;
    /**
     * 栏目描述
     */
    @ApiModelProperty(value = "栏目描述")
    private String tenantId;
    /**
     * 所属商户
     */
    @ApiModelProperty(value = "所属商户")
    private String organId;

}
