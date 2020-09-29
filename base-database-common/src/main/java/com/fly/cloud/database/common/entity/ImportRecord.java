
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
 * 导入记录表
 *
 * @author xux
 * @date 2020-09-29 09:30:17
 */
@Data
@TableName("import_record")
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "导入记录表")
public class ImportRecord extends Model<ImportRecord> {
    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    /**
     * 数据数量
     */
    @ApiModelProperty(value = "数据数量")
    private Integer dataNum;
    /**
     * 关联数据
     */
    @ApiModelProperty(value = "关联数据")
    private String relationText;
    /**
     * 去重标识(0-未去重 1-已去重)
     */
    @ApiModelProperty(value = "去重标识(0-未去重 1-已去重)")
    private String duplicateRemoval;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private String updateTime;
    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
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
