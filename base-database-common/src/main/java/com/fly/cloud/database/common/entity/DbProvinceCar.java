
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
 * 车牌省份
 *
 * @author xux
 * @date 2020-09-02 11:17:55
 */
@Data
@TableName("db_province_car")
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "车牌省份")
public class DbProvinceCar extends Model<DbProvinceCar> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 车牌号前两位
     */
    @ApiModelProperty(value = "车牌号前两位")
    private String hp;
    /**
     * 地区
     */
    @ApiModelProperty(value = "地区")
    private String city;
    /**
     * 省份
     */
    @ApiModelProperty(value = "省份")
    private String province;
    /**
     * 简称
     */
    @ApiModelProperty(value = "简称")
    private String pCode;
    /**
     * 地区编码
     */
    @ApiModelProperty(value = "地区编码")
    private String areaCode;
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
