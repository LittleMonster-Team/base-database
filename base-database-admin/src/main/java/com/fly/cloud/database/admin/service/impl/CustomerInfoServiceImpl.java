
package com.fly.cloud.database.admin.service.impl;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fly.cloud.common.core.util.R;
import com.fly.cloud.common.security.util.SecurityUtils;
import com.fly.cloud.database.admin.config.DataBaseDataProperties;
import com.fly.cloud.database.admin.config.DataBaseExcelProperties;
import com.fly.cloud.database.admin.mapper.CustomerInfoMapper;
import com.fly.cloud.database.admin.service.*;
import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.entity.CustomerInfo;
import com.fly.cloud.database.common.entity.DbSequence;
import com.fly.cloud.database.common.util.*;
import com.fly.cloud.database.common.vo.CustomerInfoVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 客户信息
 *
 * @author xux
 * @date 2020-09-01 14:17:22
 */
@Service
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {


    @Autowired
    private DataBaseDataProperties dataBaseDataProperties;
    @Autowired
    private DataBaseExcelProperties dataBaseExcelProperties;
    @Autowired
    private DbSequenceService dbSequenceService;
    @Autowired
    private CustomerInfoTemporaryService infoTemporaryService;
    @Autowired
    private CustomerInfoVersionService infoVersionService;
    @Autowired
    private RecordYearService recordYearService;

    @Autowired
    private CustomerInfoAsyncService asyncService;

    /**
     * 批量导入上传数据
     *
     * @param filePathStr 文件路径
     * @param organId     商户id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R importExcelData(String filePathStr, String fileNames, String organId) {
        try {
//             filePathStr = "C:/Users/Administrator/Desktop/昌吉工作簿9.4-2(2).xlsx";
            // 文件路径
            String[] filePathSplit = filePathStr.split("\\|");
            // 文件名
            String[] fileNameSplit = fileNames.split("\\|");
            // 存放成功数据
            List<CustomerInfo> sInfoList = new LinkedList<CustomerInfo>();
            // 存放失败数据
            List<CustomerInfo> fInfoList = new LinkedList<CustomerInfo>();
            // 存放失败文件名数据
            List<String> fFileList = new LinkedList<String>();
            // 循环上传文件
            layer:
            for (int i = 0; i < filePathSplit.length; i++) {
                // 文件版本类型
                String fileType = "";
                //获取文件的后缀名
                String suffixName = filePathSplit[i].substring(filePathSplit[i].lastIndexOf("."));
                // 判断文件版本格式
                if (CommonConstants.Excel_2003.equals(suffixName) || CommonConstants.Excel_2007.equals(suffixName)) {
                    if (CommonConstants.Excel_2003.equals(suffixName)) {
                        fileType = CommonConstants.Excel_2003;
                    }
                    if (CommonConstants.Excel_2007.equals(suffixName)) {
                        fileType = CommonConstants.Excel_2007;
                    }
                } else {
                    // 导入格式不正确
                    return R.failed("导入格式不正确：导入失败！");
                }
                // 读取本地Excel文件
//                 InputStream in = new FileInputStream(filePathSplit[i]);
                // 读取网络Excel文件
                URL url = new URL(filePathSplit[i]);
                URLConnection connection = url.openConnection();
                InputStream in = connection.getInputStream();
                // 获取解析字段列表
                Map<String, String> fields = new HashMap<String, String>();
                String fieldNames = dataBaseExcelProperties.getFiledNames();
                String[] fieldArr = fieldNames.split(",");
                for (String f : fieldArr) {
                    String[] row = f.split("_");
                    fields.put(row[0], row[1]);
                }
                // 获取解析字段校验类型
                Map<String, Integer> fieldTypes = new HashMap<String, Integer>();
                String validType = dataBaseExcelProperties.getCheckFields();
                String[] fieldTypeArr = validType.split(",");
                for (String f : fieldTypeArr) {
                    String[] row = f.split("_");
                    fieldTypes.put(row[0], Integer.parseInt(row[1]));
                }
                // 获取必要字段列表
                Map<String, String> necessaryFieldTypes = new HashMap<String, String>();
                String necessaryFields = dataBaseExcelProperties.getNecessaryFields();
                String[] necessaryFieldArr = necessaryFields.split(",");
                for (String f : necessaryFieldArr) {
                    String[] row = f.split("_");
                    necessaryFieldTypes.put(row[0], row[1]);
                }
                // 获取Excel解析数据
                Map<String, Object> resultMap = ExeclUtil.ExeclToList(in, CustomerInfo.class, fields, fieldTypes, necessaryFieldTypes, fileType);
                // 解析数据结果集
                List<CustomerInfo> sList = (List<CustomerInfo>) resultMap.get("successList");
                List<CustomerInfo> failList = (List<CustomerInfo>) resultMap.get("failList");
                // 解析错误信息提示
                String errorMsg = resultMap.get("errorMsg").toString();
                // 表头的错误信息
                String headErrorMsg = resultMap.get("headErrorMsg").toString();
                if (StringUtils.isNotBlank(headErrorMsg)) {
                    // 记录错误文件
                    fFileList.add(fileNameSplit[i]);
                    // 跳出本次循环,上传下一份文件
                    continue layer;
                }
                if (sList != null && sList.size() > 0) {
                    sInfoList.addAll(sList);
                    // 将数据保存进临时表
                    asyncService.saveExtraInfo(sList, fileNameSplit[i]);
                }
                if (failList != null && failList.size() > 0) {
                    fInfoList.addAll(failList);
                    // 将失败数据保存进表
                    asyncService.saveInfoTemporary(failList, CommonConstants.FAIL_DATA);
                }
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("successNum", sInfoList.size());
            map.put("failNum", fInfoList.size());
            map.put("failFileList", fFileList);
            map.put("failFileNum", fFileList.size());
            return R.ok(map);
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("导入失败");
        }
    }

    /**
     * 查询当前导入的数据差异信息
     *
     * @param list 导入的数据
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> queryCurrentDiffList(List<CustomerInfo> list) {
        // 建立结果Map
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 成功数据集合
        List<CustomerInfo> successList = new ArrayList<CustomerInfo>();
        // 差异数据集合
        List<CustomerInfo> differenceList = new ArrayList<CustomerInfo>();
        Set<String> set = new HashSet<String>();
        Map<String, CustomerInfo> map = new HashMap<>();
        // 遍历上传的数据
        for (int i = 0; i < list.size(); i++) {
            // 获取车牌号
            String carNum = list.get(i).getCarNum();
            if (set.contains(carNum.trim())) {
                differenceList.add(list.get(i));// 新数据
                CustomerInfo customerInfo = map.get(carNum.trim());
                customerInfo.setVersion(CommonConstants.ERROR_RESULT);
                differenceList.add(customerInfo);// 旧数据
            } else {
                set.add(carNum.trim());// 记录车牌号
                map.put(carNum.trim(), list.get(i));// 记录客户信息
                successList.add(list.get(i));
            }
        }
        resultMap.put("successList", successList);
        resultMap.put("differenceList", differenceList);
        return resultMap;
    }


    /**
     * 将数据转为Excel文件并保存起来
     * 返回文件信息
     *
     * @param infoList 数据
     * @return
     */
    @Override
    @Transactional
    public R exprotExcelData(List<CustomerInfo> infoList) {
        // 返回文件大小
        double fileSize = 0;
        // 以时间作为一批
        String nowday = new SimpleDateFormat("ddHHmmss").format(new Date());
        // 保存文件根目录
        String ctxPath = dataBaseExcelProperties.getExprotFileRoot() + File.separator + nowday;
        Map<String, Object> map = new HashMap<String, Object>();
        // 将数据转为Excel文件
        asyncService.exprotExcelData(infoList, ctxPath);
        map.put("fileLink", ctxPath);
        map.put("fileName", DateUtils.getCurrentTime() + "客户信息.zip");
        map.put("fileSize", fileSize + CommonConstants.BAIKILOBYTE_SIZE);
        return R.ok(map);
    }

    /**
     * 下载文件保存本地
     *
     * @param fileLink 文件路径
     * @param response 信息流
     * @return
     */
    @Override
    @Transactional
    public R downloadExcelFiles(String fileLink, HttpServletResponse response) {
        try {
            String fileName = fileLink + "客户信息.zip";
            ZipUtil.zip(fileLink, fileName);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("fileName", URLEncoder.encode("客户信息", "UTF-8") + ".zip");
            response.setHeader("content-Type", "application/json");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("客户信息", "UTF-8") + ".zip");
            // 1.获取要下载的文件输入流
            InputStream in = new FileInputStream(fileName);
            int len = 0;
            // 2.创建数据缓冲区
            byte[] buffer = new byte[1024];
            // 3.通过response对象获取OutputStream流
            OutputStream out = response.getOutputStream();
            // 4.将FileInputStream流写入到buffer缓冲区
            while ((len = in.read(buffer)) > 0) {
                // 5.使用OutputStream将缓冲区的数据输出到客户端浏览器
                out.write(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.ok();
    }

    /**
     * 查询总数据与当前导入成功数据的差异信息
     *
     * @param tList 总数据
     * @param cList 当前导入成功数据
     * @return
     */
    @Override
    @Transactional
    public Map<String, List<CustomerInfo>> queryTotalDiffList(List<CustomerInfo> tList, List<CustomerInfo> cList) {
        // 返货结果集map
        Map<String, List<CustomerInfo>> resultMap = new HashMap<String, List<CustomerInfo>>();
        // 如果数据库中无数据，直接保存新数据
        if (tList != null && tList.size() > 0) {
            // 总数据MAP
            Map<String, CustomerInfo> tmap = tList.stream()
                    .collect(Collectors.toMap(CustomerInfo::getCarNum, Function.identity(), (key1, key2) -> key2));
            // 导入成功数据数据MAP
            Map<String, CustomerInfo> cmap = cList.stream()
                    .collect(Collectors.toMap(CustomerInfo::getCarNum, Function.identity(), (key1, key2) -> key2));
            Set<String> objects = tmap.keySet();
            List<CustomerInfo> successList = new LinkedList<CustomerInfo>();
            List<CustomerInfo> differenceList = new LinkedList<CustomerInfo>();
            // 遍历新导入的数据
            for (String carNum : cmap.keySet()) {
                // 如果数据库中的车牌号相等
                if (objects.contains(carNum)) {
                    // 如果两条数据不相同 则加入差异列表
                    if (!cmap.get(carNum).equals(tmap.get(carNum))) {
                        CustomerInfo newInfo = cmap.get(carNum);
                        newInfo.setTableInfo(tmap.get(carNum).getTableInfo());
                        differenceList.add(newInfo);// 新数据
                        CustomerInfo usedInfo = tmap.get(carNum);
                        usedInfo.setVersion(CommonConstants.USED_DATA);
                        differenceList.add(usedInfo);// 旧数据
                    }
                } else {
                    successList.add(cmap.get(carNum));// 新数据
                }
            }
            resultMap.put("successList", successList);
            resultMap.put("differenceList", differenceList);
            return resultMap;
        } else {
            resultMap.put("successList", cList);
            resultMap.put("differenceList", null);
            return resultMap;
        }
    }

    /**
     * 筛选客户信息列表
     *
     * @param obj 数据信息
     * @return
     */
    @Override
    @Transactional
    public Page screenCustomerInfo(JSONObject obj) {
        // Obj转实体类
        Page page = JsonUtils.ObjToEntity(obj.getObj("page"), Page.class);
        CustomerInfo customerInfo = JsonUtils.ObjToEntity(obj.getObj("customerInfo"), CustomerInfo.class);
        String month = obj.getStr("month");
        // 获取登录的商户信息
        String organId = SecurityUtils.getUser().getOrganId();
        // 查询数据
        // 获取当前序列号
        Long sequence = this.getSequenceValue(organId);
        // 多表查询比较重复数据
        List<CustomerInfo> customerInfoList = new LinkedList<CustomerInfo>();
        // 获取建表年份
        Map<String, String> yearMap = recordYearService.getYearData(organId);
        String yearInfo = yearMap.get("yearInfo");
        for (long i = sequence; i >= 0; i--) {
            String[] yaerSplit = yearInfo.split(",");
            for (String year : yaerSplit) {
                // 筛选数据
                List<CustomerInfo> customerInfos = OperationUtils.screenData(this.queryInfoListByMonth(organId, i, year, month), customerInfo);
                if (customerInfos != null && customerInfos.size() > 0) {
                    customerInfoList.addAll(customerInfos);
                }
            }
        }
        List<CustomerInfo> records = null;
        int total = 0;
        if (customerInfoList != null && customerInfoList.size() > 0) {
            // 总条数
            total = customerInfoList.size();
            // 分页查询数据
            records = PageUtils.getPage(customerInfoList, String.valueOf(page.getCurrent()), page.getSize());
        }
        // 获取数据数量
        page.setTotal(total);
        // 赋值
        page.setRecords(records);
        return page;
    }

    /**
     * 搜索客户信息列表
     *
     * @param obj 数据信息
     * @return
     */
    @Override
    @Transactional
    public Page searchCustomerInfo(JSONObject obj) {
        // Obj转实体类
        Page page = JsonUtils.ObjToEntity(obj.getObj("page"), Page.class);
        String condition = obj.getStr("condition");
        // 获取登录的商户信息
        String organId = SecurityUtils.getUser().getOrganId();
        // 查询数据
        // 获取当前序列号
        Long sequence = this.getSequenceValue(organId);
        // 多表查询比较重复数据
        List<CustomerInfo> customerInfoList = new LinkedList<CustomerInfo>();
        Map<String, String> yearMap = recordYearService.getYearData(organId);
        String yearInfo = yearMap.get("yearInfo");
        for (long i = sequence; i >= 0; i--) {
            String[] yaerSplit = yearInfo.split(",");
            for (String year : yaerSplit) {
                // 筛选数据
                List<CustomerInfo> customerInfos = OperationUtils.searchData(this.queryCustomerInfoList(organId, i, year), condition);
                if (customerInfos != null && customerInfos.size() > 0) {
                    customerInfoList.addAll(customerInfos);
                }
            }
        }
        List<CustomerInfo> records = null;
        int total = 0;
        if (customerInfoList != null && customerInfoList.size() > 0) {
            // 总条数
            total = customerInfoList.size();
            // 分页查询数据
            records = PageUtils.getPage(customerInfoList, String.valueOf(page.getCurrent()), page.getSize());
        }
        // 获取数据数量
        page.setTotal(total);
        // 赋值
        page.setRecords(records);
        return page;
    }

    /**
     * 获取表序列
     *
     * @param organId 商户id
     * @return
     */
    @Override
    @Transactional
    public Long getSequenceValue(String organId) {
        // 获取正式表序列名
        String seqName = CommonConstants.BASE_DATA_PREFIX + "_" + organId;
        DbSequence dbSequence = dbSequenceService.getById(seqName);
        if (dbSequence != null) {
            return dbSequence.getCurrentValue();
        } else {
            dbSequenceService.saveSeqByName(seqName, 0, 1);
            DbSequence sequence = dbSequenceService.getById(seqName);
            recordYearService.saveYearInfo(organId);
            return sequence.getCurrentValue();
        }
    }

    /**
     * 更新客户信息
     *
     * @param customerInfo 客户信息
     * @return
     */
    @Override
    @Transactional
    public R updateCustomerInfo(CustomerInfo customerInfo) {
        //  获取表名
        String tableName = "";
        if (StringUtils.isNotBlank(customerInfo.getTableInfo())) {
            tableName = customerInfo.getTableInfo();
        } else {
            // 获取登录的商户信息
            String organId = SecurityUtils.getUser().getOrganId();
            // 获取表序列号
            Long num = this.getSequenceValue(organId);
            // 获取当前年份
            String year = DateUtils.getYear();
            // 获取表名
            tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + num;
        }
        // 设置出险次数默认值
        if (customerInfo.getAccidentsNum().equals(CommonConstants.DEFAULT_INITIAL_VALUE_NOTHING) || !StringUtils.isNotBlank(customerInfo.getAccidentsNum())) {
            customerInfo.setAccidentsNum(CommonConstants.DEFAULT_INITIAL_VALUE_ZERO);
        }
        // 编辑字段
        String fieldSql = "customer_name = '" + customerInfo.getCustomerName() + "', " +
                "gender = '" + customerInfo.getGender() + "', " +
                "phone = '" + customerInfo.getPhone() + "', " +
                "id_card = '" + customerInfo.getIdCard() + "', " +
                "car_num = '" + customerInfo.getCarNum() + "', " +
                "brand_model = '" + customerInfo.getBrandModel() + "', " +
                "vin_num = '" + customerInfo.getVinNum() + "', " +
                "engine_num = '" + customerInfo.getEngineNum() + "', " +
                "clause = '" + customerInfo.getClause() + "', " +
                "vehicle_type_code = '" + customerInfo.getVehicleTypeCode() + "', " +
                "use_property_code = '" + customerInfo.getUsePropertyCode() + "', " +
                "accidents_num = '" + customerInfo.getAccidentsNum() + "', " +
                "service_life = '" + customerInfo.getServiceLife() + "', " +
                "first_date = '" + customerInfo.getFirstDate() + "', " +
                "salesman = '" + customerInfo.getSalesman() + "', " +
                "update_time = '" + DateUtils.getCurrentDate() + "'";

        String sql = "UPDATE " + tableName + " SET " + fieldSql + " WHERE car_num=" + "'" + customerInfo.getCarNum() + "'";
        try {
            // 更新表数据
            JdbcUtils.exectueUpdate(dataBaseDataProperties, sql, null);
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed(CommonConstants.ERROR_RESULT, "更新失败");
        }
        return R.ok();
    }

    /**
     * 批量插入数据
     *
     * @param infoList    数据
     * @param tableName   表明
     * @param tableFields 表字段
     */
    @Override
    @Transactional
    public void insertCustomerInfoData(List<CustomerInfo> infoList, String tableName, String tableFields) {
        // 转换数据类型
        List<Object> dataList = new ArrayList<Object>();
        // 设置表名用于修改使用
        infoList.forEach(info -> {
            // 设置出险次数
            if (!StringUtils.isNotBlank(info.getAccidentsNum()) || info.getAccidentsNum().equals("无")) {
                info.setAccidentsNum(CommonConstants.DEFAULT_INITIAL_VALUE_ZERO);
            }
            // 设置使用年限(含有小数)
            info.setUseYears(DateUtils.yearCompare(info.getFirstDate(), DateUtils.getCurrentTime()).get("useYears").toString());
            // 设置使用年限(整年)
            info.setUseYear(DateUtils.yearCompare(info.getFirstDate(), DateUtils.getCurrentTime()).get("useYear").toString());
            // 设置表信息(存放表名，用于以后修改)
            info.setTableInfo(tableName);
            dataList.add(info);
            // 批量插入数据
            if (dataList.size() % 1000 == 0) {
                JdbcUtils.svaeBatch(dataBaseDataProperties, tableName, tableFields, dataList, CustomerInfo.class);
                dataList.clear();
            }
        });
        if (dataList.size() > 0) {
            JdbcUtils.svaeBatch(dataBaseDataProperties, tableName, tableFields, dataList, CustomerInfo.class);
            dataList.clear();
        }
    }

    /**
     * 根据车牌号查询数据
     *
     * @param carNum    车牌号
     * @param tableName 表名
     * @return
     */
    @Override
    @Transactional
    public List<CustomerInfo> getCustomerInfoByCarNum(String carNum, String tableName) {
        if (StringUtils.isNotBlank(carNum) && StringUtils.isNotBlank(tableName)) {
            String sql = "select * from " + tableName + " where car_num = '" + carNum + "' order by create_time desc";
            try {
                List<CustomerInfo> list = (List<CustomerInfo>) JdbcUtils.executeQuery(dataBaseDataProperties, sql, CustomerInfo.class);
                return list;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;

    }

    /**
     * 查询部门下客户信息
     *
     * @param organId  商户id
     * @param sequence 序列
     * @param year     年份
     * @return
     */
    @Override
    @Transactional
    public List<CustomerInfo> queryCustomerInfoList(String organId, long sequence, String year) {

        List<CustomerInfo> list = null;
        // 获取表名
        String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + sequence;
        String sql = "select * from " + tableName + " order by create_time desc";
        try {
            list = (List<CustomerInfo>) JdbcUtils.executeQuery(dataBaseDataProperties, sql, CustomerInfo.class);
        } catch (Exception e) {

        }
        return list;
    }

    /**
     * 查询部门下客户信息
     *
     * @param organId  商户id
     * @param sequence 序列
     * @param year     年份
     * @param month    月份
     * @return
     */
    @Override
    @Transactional
    public List<CustomerInfo> queryInfoListByMonth(String organId, long sequence, String year, String month) {
        List<CustomerInfo> list = null;
        try {
            if (StringUtils.isNotBlank(month)) {
                // 格式化月份
                month = "-" + DateUtils.padDatazero(Integer.parseInt(month)) + "-";
            }
            // 获取表名
            String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + sequence;
            String sql = "SELECT * FROM	`" + tableName + "` WHERE first_date LIKE '%" + month + "%' ORDER BY create_time DESC";
            list = (List<CustomerInfo>) JdbcUtils.executeQuery(dataBaseDataProperties, sql, CustomerInfo.class);
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * 获取数据数量
     *
     * @param organId  商户id
     * @param sequence 序列
     * @param year     年份
     * @return
     */
    @Override
    @Transactional
    public int getCustomerInfoListCount(String organId, long sequence, String year) {
        try {
            // 获取表名
            String tableName = dataBaseExcelProperties.getTablePrefix() + organId + "_" + year + "_" + sequence;
            String sql = "select count(0) from " + tableName + " order by create_time desc";
            // 查询数据数量
            return JdbcUtils.executeQueryNum(dataBaseDataProperties, sql);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据证件号获取地区与性别信息
     *
     * @param successList 导入成功的数据
     * @return
     */
    @Override
    @Transactional
    public List<CustomerInfo> dataAssignment(List<CustomerInfo> successList) {
        if (successList != null && successList.size() > 0) {
            successList.stream().map(info -> {
                // 获取客户证件号
                String idCard = info.getIdCard();
                // 默认性别未知
                info.setGender(CommonConstants.GENDER_UNKNOWN);
                // 如果客户的证件号存在
                if (StringUtils.isNotBlank(idCard) && !idCard.equals("无")) {
                    // 获取用户的地址信息
                    Map<String, Object> map = AnalysisOfIdCard.getNativePlace(idCard);
                    // 通过证件号获取用户性别
                    Integer integer = AnalysisOfIdCard.judgeSex(idCard);
                    // 设置省
                    info.setProvince(map.get("province").toString());
                    // 设置市
                    info.setCity(map.get("city").toString());
                    // 设置区/县
                    info.setCounty(map.get("county").toString());
                    // 设置性别
                    info.setGender(integer.toString());
                }
                // 设置id
                info.setId(UUID.randomUUID().toString().replace("-", ""));
                // 日期数据格式化
                info.setFirstDate(info.getFirstDate().replace("/", "-"));
                // 设置创建时间
                info.setCreateTime(DateUtils.getCurrentDate());
                // 设定初始版本
                info.setVersion(0);
                return info;
            }).collect(Collectors.toList());
        }
        return successList;
    }

    /**
     * 获取成功与差异数据
     * 将成功数据保存进数据库
     *
     * @param sList   数据
     * @param organId 商户id
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> getDifferenceInfoData(List<CustomerInfo> sList, String organId) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (sList == null || sList.size() == 0) {
            map.put("successListNum", 0);
            map.put("differenceListNum", 0);
            map.put("differenceList", null);
            return map;
        }
        List<CustomerInfo> successInfoList = new LinkedList<CustomerInfo>();
        // 创建差异数据集合
        List<CustomerInfo> differenceList = new ArrayList<CustomerInfo>();
        // 获取差异数据(校验当前文件的差异)
        Map<String, Object> currentMap = this.queryCurrentDiffList(sList);
        // 上传成功数据
        List<CustomerInfo> successList = (List<CustomerInfo>) currentMap.get("successList");
        // 上传有差异数据
        List<CustomerInfo> dList = (List<CustomerInfo>) currentMap.get("differenceList");
        if (dList != null && dList.size() > 0) {
            differenceList.addAll(dList);
        }
        // 获取当前序列号
        Long sequence = this.getSequenceValue(organId);
        if (successList != null && successList.size() > 0) {
            Map<String, String> yearMap = recordYearService.getYearData(organId);
            String yearData = yearMap.get("yearData");
            String yearInfo = yearMap.get("yearInfo");
            // 多表查询比较重复数据
            for (long i = sequence; i >= 0; i--) {
                String[] yaerSplit = yearInfo.split(",");
                for (String year : yaerSplit) {
                    List<CustomerInfo> querylist = this.queryCustomerInfoList(organId, i, year);
                    // 比较库中是否有差异数据
                    Map<String, List<CustomerInfo>> stringListMap = this.queryTotalDiffList(querylist, successList);
                    List<CustomerInfo> successInfo = stringListMap.get("successList");
                    List<CustomerInfo> differenceInfo = stringListMap.get("differenceList");
                    if (successInfo != null && successInfo.size() > 0) {
                        successInfoList.addAll(successInfo);
                    }
                    if (differenceInfo != null && differenceInfo.size() > 0) {
                        differenceList.addAll(differenceInfo);
                    }
                }
            }
            // 去除重复数据
            List<CustomerInfo> infoList = OperationUtils.removeDuplicate(successInfoList);
            // 获取数据库表数据
            int count = this.getCustomerInfoListCount(organId, sequence, yearData);
            // 校验是否需要新建表
            if (infoList.size() + count > Long.parseLong(dataBaseExcelProperties.getMaximumLimit())) {
                // 获取正式表序列名
                String fName = CommonConstants.BASE_DATA_PREFIX + "_" + organId;
                // 更新表序列
                dbSequenceService.updateDbSequence(fName);
            }
            if (StringUtils.isNotBlank(yearData)) {
                if (!DateUtils.getYear().equals(yearData)) {
                    // 更新表数据
                    recordYearService.updateYearInfo(organId);
                }
            }
            // 获取表序列号
            Long num = this.getSequenceValue(organId);
            // 获取当前年份
            String year = DateUtils.getYear();
            // 查询表是否存在redis缓存key
            String key = CommonConstants.BASE_DATA_REDIS_PREFIX + organId + "_" + year + "_" + num;
            // 将数据添加进表中
            asyncService.saveCustomerInfo(successList, organId, year, num, key);
            // 将差异数据添加进表中
            asyncService.saveInfoTemporary(differenceList, CommonConstants.DIFFERENCE_DATA);
        }
        map.put("successListNum", successList.size());
        map.put("differenceListNum", differenceList.size());
        map.put("differenceL", differenceList);
        return map;
    }

    /**
     * 查询客户信息详情
     *
     * @param params 参数
     * @return
     */
    @Override
    @Transactional
    public R viewDetailsInfo(JSONObject params) {
        Map<String, Object> map = new HashMap<String, Object>();
        String carNum = params.getStr("carNum");
        String tableInfo = params.getStr("tableInfo");
        List<CustomerInfoVO> customerInfoList = new LinkedList<CustomerInfoVO>();
        List<CustomerInfoVO> carsTableDataList = new LinkedList<CustomerInfoVO>();
        List<CustomerInfo> customerInfos = this.getCustomerInfoByCarNum(carNum, tableInfo);
        if (customerInfos != null && customerInfos.size() > 0) {
            List<CustomerInfoVO> list = new ArrayList<CustomerInfoVO>();
            customerInfos.forEach(info -> {
                CustomerInfoVO infoVO = new CustomerInfoVO();
                // 转换数据类型
                BeanUtils.copyProperties(this.formatGender(info), infoVO);
                // 设置数据状态，用于去除判断
                infoVO.setDataStatus(CommonConstants.LATEST_DATA);
                list.add(infoVO);
            });
            customerInfoList.addAll(list);
            carsTableDataList.add(list.get(0));
            map.put("detailsForm", list.get(0));
            map.put("carsTableData", carsTableDataList);
        } else {
            map.put("detailsForm", null);
            map.put("carsTableData", null);
        }
        // 根据车牌号查询信息
        List<CustomerInfo> infoList = infoVersionService.viewDetailsInfo(carNum);
        if (infoList != null && infoList.size() > 0) {
            List<CustomerInfoVO> list = new ArrayList<CustomerInfoVO>();
            infoList.forEach(version -> {
                CustomerInfoVO infoVO = new CustomerInfoVO();
                // 转换数据类型
                BeanUtils.copyProperties(this.formatGender(version), infoVO);
                // 设置数据状态，用于去除判断
                infoVO.setDataStatus(CommonConstants.OTHER_VERSIONS_OF_DATA);
                list.add(infoVO);
            });
            customerInfoList.addAll(list);
        }
        map.put("detailsTableData", customerInfoList);
        return R.ok(map);
    }

    /**
     * 去除客户信息
     *
     * @param infoVo 客户信息
     * @return
     */
    @Override
    @Transactional
    public R removeCustomerInfo(CustomerInfoVO infoVo) {
        // 判断数据是最新数据还是往期版本数据
        if (infoVo.getDataStatus().equals(CommonConstants.LATEST_DATA)) {
            String sql = "delete from " + infoVo.getTableInfo() + " where id = '" + infoVo.getId() + "'";
            try {
                JdbcUtils.exectueUpdate(dataBaseDataProperties, sql, null);
                return R.ok();
            } catch (Exception e) {
                e.printStackTrace();
                return R.failed("去除失败");
            }
        } else {
            return R.ok(infoTemporaryService.removeById(infoVo.getId()));
        }
    }

    /**
     * 将差异数据添加进表中
     *
     * @param differenceList
     * @param differenceData
     */
    @Override
    @Transactional
    public void saveInfoTemporary(List<CustomerInfo> differenceList, String differenceData) {
        asyncService.saveInfoTemporary(differenceList, CommonConstants.DIFFERENCE_DATA);
    }

    /**
     * 格式化客户性别
     *
     * @param info 客户信息
     * @return
     */
    @Override
    @Transactional
    public CustomerInfo formatGender(CustomerInfo info) {
        if (info.getGender().equals(CommonConstants.GENDER_MALE)) {
            info.setGender("男");
        } else if (info.getGender().equals(CommonConstants.GENDER_FEMALE)) {
            info.setGender("女");
        } else {
            info.setGender("未知");
        }
        return info;
    }

}


