
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
 * 客户信息
 *
 * @author xux
 * @date 2020-09-01 14:17:22
 */
@Data
@TableName("customerInfo")
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "客户信息")
public class CustomerInfo extends Model<CustomerInfo> {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 客户姓名
     */
    @ApiModelProperty(value = "客户姓名")
    private String customerName;
    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String gender;
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phone;
    /**
     * 证件号
     */
    @ApiModelProperty(value = "证件号")
    private String idCard;
    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    private String carNum;
    /**
     * 厂牌型号
     */
    @ApiModelProperty(value = "厂牌型号")
    private String brandModel;
    /**
     * VIN码(车架号)
     */
    @ApiModelProperty(value = "VIN码(车架号)")
    private String vinNum;
    /**
     * 发动机号
     */
    @ApiModelProperty(value = "发动机号")
    private String engineNum;
    /**
     * 条款
     */
    @ApiModelProperty(value = "条款")
    private String clause;
    /**
     * 车辆种类代码
     */
    @ApiModelProperty(value = "车辆种类代码")
    private String vehicleTypeCode;
    /**
     * 使用性质代码
     */
    @ApiModelProperty(value = "使用性质代码")
    private String usePropertyCode;
    /**
     * 出险次数
     */
    @ApiModelProperty(value = "出险次数")
    private String accidentsNum;
    /**
     * 使用年限
     */
    @ApiModelProperty(value = "使用年限(客户提供)")
    private String serviceLife;
    /**
     * 初登日期
     */
    @ApiModelProperty(value = "初登日期")
    private String firstDate;
    /**
     * 业务员
     */
    @ApiModelProperty(value = "业务员")
    private String salesman;
    /**
     * 费用
     */
    @ApiModelProperty(value = "费用")
    private String cost;
    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private String province;
    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private String city;
    /**
     * 区/县
     */
    @ApiModelProperty(value = "区/县")
    private String county;
    /**
     * 版本(控制获取最新数据)
     */
    @ApiModelProperty(value = "版本(控制获取最新数据)")
    private Integer version;
    /**
     * 错误信息
     */
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;
    /**
     * 表信息(存放表名)
     */
    @ApiModelProperty(value = "表信息")
    private String tableInfo;
    /**
     * 使用年限(含有小数)
     */
    @ApiModelProperty(value = "使用年限(含有小数)")
    private String useYears;
    /**
     * 使用年限(整年)
     */
    @ApiModelProperty(value = "使用年限(整年)")
    private String useYear;
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
    /**
     * 保险起期日
     */
    @ApiModelProperty(value = "保险起期日")
    private String insuranceStart;
    /**
     * 保险到期日
     */
    @ApiModelProperty(value = "保险到期日")
    private String insuranceEnd;
    /**
     * 座位数
     */
    @ApiModelProperty(value = "座位数")
    private String seatNum;
    /**
     * 汽车类型1
     */
    @ApiModelProperty(value = "汽车类型1")
    private String carTypeOne;
    /**
     * 汽车类型2
     */
    @ApiModelProperty(value = "汽车类型2")
    private String carTypeTwo;
    /**
     * 备用电话1
     */
    @ApiModelProperty(value = "备用电话1")
    private String phoneOne;
    /**
     * 备用电话2
     */
    @ApiModelProperty(value = "备用电话2")
    private String phoneTwo;
    /**
     * 备用电话3
     */
    @ApiModelProperty(value = "备用电话3")
    private String phoneThree;
    /**
     * 召回电话1
     */
    @ApiModelProperty(value = "召回电话1")
    private String recallOne;
    /**
     * 召回电话2
     */
    @ApiModelProperty(value = "召回电话2")
    private String recallTwo;
    /**
     * 召回电话3
     */
    @ApiModelProperty(value = "召回电话3")
    private String recallThree;

    /**
     * 备用字段
     */
    @ApiModelProperty(value = "备用字段")
    private String spareField1;
    /**
     * 备用字段
     */
    @ApiModelProperty(value = "备用字段")
    private String spareField2;
    /**
     * 备用字段
     */
    @ApiModelProperty(value = "备用字段")
    private String spareField3;
    /**
     * 备用字段
     */
    @ApiModelProperty(value = "备用字段")
    private String spareField4;
    /**
     * 备用字段
     */
    @ApiModelProperty(value = "备用字段")
    private String spareField5;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerInfo that = (CustomerInfo) o;
        if (customerName != null ? !customerName.equals(that.customerName) : that.customerName != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (idCard != null ? !idCard.equals(that.idCard) : that.idCard != null) return false;
        if (carNum != null ? !carNum.equals(that.carNum) : that.carNum != null) return false;
        if (brandModel != null ? !brandModel.equals(that.brandModel) : that.brandModel != null) return false;
        if (vinNum != null ? !vinNum.equals(that.vinNum) : that.vinNum != null) return false;
        if (engineNum != null ? !engineNum.equals(that.engineNum) : that.engineNum != null) return false;
        if (clause != null ? !clause.equals(that.clause) : that.clause != null) return false;
        if (vehicleTypeCode != null ? !vehicleTypeCode.equals(that.vehicleTypeCode) : that.vehicleTypeCode != null)
            return false;
        if (usePropertyCode != null ? !usePropertyCode.equals(that.usePropertyCode) : that.usePropertyCode != null)
            return false;
        if (serviceLife != null ? !serviceLife.equals(that.serviceLife) : that.serviceLife != null) return false;
        if (firstDate != null ? !firstDate.equals(that.firstDate) : that.firstDate != null) return false;
        return salesman != null ? salesman.equals(that.salesman) : that.salesman == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (idCard != null ? idCard.hashCode() : 0);
        result = 31 * result + (carNum != null ? carNum.hashCode() : 0);
        result = 31 * result + (brandModel != null ? brandModel.hashCode() : 0);
        result = 31 * result + (vinNum != null ? vinNum.hashCode() : 0);
        result = 31 * result + (engineNum != null ? engineNum.hashCode() : 0);
        result = 31 * result + (clause != null ? clause.hashCode() : 0);
        result = 31 * result + (vehicleTypeCode != null ? vehicleTypeCode.hashCode() : 0);
        result = 31 * result + (usePropertyCode != null ? usePropertyCode.hashCode() : 0);
        result = 31 * result + (accidentsNum != null ? accidentsNum.hashCode() : 0);
        result = 31 * result + (serviceLife != null ? serviceLife.hashCode() : 0);
        result = 31 * result + (firstDate != null ? firstDate.hashCode() : 0);
        result = 31 * result + (salesman != null ? salesman.hashCode() : 0);
        return result;
    }
}
