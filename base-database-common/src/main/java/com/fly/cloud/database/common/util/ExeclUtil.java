package com.fly.cloud.database.common.util;

import com.fly.cloud.database.common.constant.CommonConstants;
import com.fly.cloud.database.common.enums.ValidTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Excel工具类
 *
 * @description: Excel工具类
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class ExeclUtil {
    static int sheetsize = 5000;

    /**
     * excel文件转为list集合
     *
     * @param in                  excel文件  文件流
     * @param entityClass         实体类
     * @param fields              字段 字段映射,需要注意的是这个方法中的map中：
     *                            excel表格中每一列名为键，每一列对应的实体类的英文名为值
     * @param fieldTypes          校验字段
     * @param necessaryFieldTypes 必要字段
     * @param fileType            文件类型
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> ExeclToList(InputStream in, Class<T> entityClass, Map<String, String> fields,
                                                      Map<String, Integer> fieldTypes, Map<String, String> necessaryFieldTypes, String fileType) throws Exception {
        // 返货结果集map
        Map<String, Object> resultMap = new HashMap<String, Object>();
        // 成功列表
        List<T> successList = new ArrayList<T>();
        // 错误列表
        List<T> failList = new ArrayList<T>();
        // 内容格式错误
        StringBuilder errorMsg = new StringBuilder();
        // 表头错误信息
        StringBuilder headErrorMsg = new StringBuilder();
        // 将文件流转换成poi对象
        Workbook workbook = null;
        // 校验文件类型
        System.out.println("校验文件类型开始：" + DateUtils.getCurrentDate());
        if (CommonConstants.Excel_2003.equals(fileType)) {// 2003 版本的excel
            workbook = new HSSFWorkbook(in);
        } else if (CommonConstants.Excel_2007.equals(fileType)) {// 2007 版本的excel
            workbook = new XSSFWorkbook(in);
        } else {// 导入格式不正确
            workbook.close();
            throw new Exception("导入文件格式不正确");
        }
        System.out.println("校验文件类型结束：" + DateUtils.getCurrentDate());
        // excel中字段的中文名字数组
        String[] cntitles = getCnAndEnNames(fields).get("cntitles");
        // 得到excel中sheet总数
        int sheetcount = workbook.getNumberOfSheets();
        if (sheetcount == 0) {
            workbook.close();
            throw new Exception("Excel文件中没有任何数据");
        }
        System.out.println("数据的导出开始：" + DateUtils.getCurrentDate());
        // 数据的导出
        for (int i = 0; i < sheetcount; i++) {
            // 过滤隐藏sheet
            boolean sheetHidden = workbook.isSheetHidden(i);
            if (sheetHidden == true) {
                continue;
            }
            // 获取sheet
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            // 每页中的第一行为标题行，对标题行的特殊处理
            Row firstRow = sheet.getRow(0);
            if (firstRow == null) {
                continue;
            }
            // 存放列名与序号
            Map<String, Object> objectMap = getColMap(firstRow, cntitles);
            // 文件字段名称
            String[] excelFieldNames = (String[]) objectMap.get("excelFieldNames");
            // 列名与序号
            Map<String, Integer> colMap = (Map<String, Integer>) objectMap.get("colMap");
            // 判断需要的字段在Excel中是否存在
            Map<String, Object> requiredFields = getRequiredFields(necessaryFieldTypes, excelFieldNames, headErrorMsg);
            // 记录必要字段个数
            int nameNum = (int) requiredFields.get("nameNum");
            // 错误信息
            headErrorMsg = (StringBuilder) requiredFields.get("headErrorMsg");
            // 判断必要字段是否存在
            if (nameNum == 0) {
                resultMap.put("successList", null);
                resultMap.put("failList", null);
                resultMap.put("errorMsg", "");
                resultMap.put("headErrorMsg", headErrorMsg);
                workbook.close();
                in.close();
                return resultMap;
            }
            // 遍历行数
            // 将sheet转换为list最后一行行标，比行数小1
            for (int j = 1; j <= sheet.getLastRowNum(); j++) {
                boolean addFlag = true;
                // 获取一行数据
                Row row = sheet.getRow(j);
                // 获取空行的个数
                int cellNum = getRowIsNull(row, fields.size());
                if (cellNum == fields.size()) {
                    continue;
                }
                // 根据泛型创建实体类
                T entity = entityClass.newInstance();
                // 给对象中的字段赋值
                for (Entry<String, String> entry : fields.entrySet()) {
                    // 获取中文字段名
                    String cnNormalName = entry.getKey();
                    // 获取英文字段名
                    String enNormalName = entry.getValue();
                    // 根据中文字段名获取列号
                    if (colMap.get(cnNormalName) != null) {
                        int col = colMap.get(cnNormalName);
                        // 获取当前单元格中的内容
                        String content = String.valueOf(getCellValue(row.getCell(col)));
                        // 获取校验数据
                        Integer filedType = fieldTypes.get(enNormalName);
                        if (filedType != null) {
                            // 校验数据格式
                            String validMsg = validData(fieldTypes.get(enNormalName), content, cnNormalName);
                            if (StringUtils.isNotBlank(validMsg)) {
                                // 给错误字段添加错误信息
                                setFieldValueByName("errorMsg", validMsg, entity);
                                // 填写错误数据
                                errorMsg.append("第").append(j).append("行，第")
                                        .append(col + 1).append("列数据格式错误：").append(validMsg);
                                // 给对象赋值
                                setFieldValueByName(enNormalName, getCellValue(row.getCell(col)), entity);
                                // 添加错误数据
                                addFlag = false;
                                // 去除重复的失败数据
                                if (failList != null && failList.size() > 0) {
                                    if (failList.get(failList.size() - 1).equals(entity)) {
                                        failList.remove(failList.size() - 1);
                                    }
                                }
                                failList.add(entity);
                            }
                        }
                        // 给对象赋值
                        setFieldValueByName(enNormalName, getCellValue(row.getCell(col)), entity);
                    } else {
                        // 给空数据赋值
                        setFieldValueByName(enNormalName, '无', entity);
                    }
                }
                if (addFlag) {
                    successList.add(entity);
                }
            }
        }
        System.out.println("数据的导出结束：" + DateUtils.getCurrentDate());
        resultMap.put("successList", successList);
        resultMap.put("failList", failList);
        resultMap.put("errorMsg", errorMsg);
        resultMap.put("headErrorMsg", headErrorMsg);
        workbook.close();
        in.close();
        return resultMap;
    }

    /**
     * 校验必要字段
     *
     * @param necessaryFieldTypes 必要字段
     * @param excelFieldNames     文件字段名称
     * @param headErrorMsg        错误信息
     * @return
     */
    private static Map<String, Object> getRequiredFields(Map<String, String> necessaryFieldTypes, String[] excelFieldNames, StringBuilder headErrorMsg) {
        // 返货结果集map
        Map<String, Object> map = new HashMap<String, Object>();
        // 需要注意的是这个方法中的map中：中文名为键，英文名为值
        List<String> excelFieldList = Arrays.asList(excelFieldNames);
        int nameNum = 0;
        for (String cnName : necessaryFieldTypes.keySet()) {
            String[] nameSplit = cnName.split("#");
            // 记录字段是否存在
            int nNum = 0;
            for (String name : nameSplit) {
                // 判断字段中是否包含
                if (excelFieldList.contains(name)) {
                    nNum++;
                }
            }
            if (nNum == 0) {
                // 如果字段不存在将数据存储提醒用户
                headErrorMsg.append("文件缺少[" + cnName + "]字段；");
            } else {
                // 如果字段存在数据加1
                nameNum++;
            }
        }
        map.put("nameNum", nameNum);
        map.put("headErrorMsg", headErrorMsg);
        return map;
    }

    /**
     * 存放列名与序号
     *
     * @param firstRow Sheet的第一行
     * @param cntitles 表头
     * @return
     */
    public static Map<String, Object> getColMap(Row firstRow, String[] cntitles) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        // 获取列数，比最后一列列标大1
        int celllength = firstRow.getLastCellNum();
        // 存放文件字段名称
        String[] excelFieldNames = new String[celllength];
        // 存放列名与序号
        Map<String, Integer> colMap = new LinkedHashMap<String, Integer>();
        for (int f = 0; f < celllength; f++) {
            // 判断空列
            if (firstRow.getCell(f) != null) {
                // 获取文件一列数据
                Cell cell = firstRow.getCell(f);
                // 获取Excel中的列名去除回车
                excelFieldNames[f] = cell.getStringCellValue().trim().replaceAll("\n", "");
                // 将列名和列号放入Map中,这样通过列名就可以拿到列号
                for (int g = 0; g < cntitles.length; g++) {
                    String field = cntitles[g];
                    if (field.indexOf(excelFieldNames[f]) != -1) {
                        colMap.put(field, f);
                        break;
                    }
                }
            }
        }
        // 由于数组是根据长度创建的，所以值是空值，这里对列名map做了去空键的处理
        colMap.remove(null);
        map.put("colMap", colMap);
        map.put("excelFieldNames", excelFieldNames);
        return map;
    }

    /**
     * 获取excel中字段的中英文名字数组
     *
     * @param fields 字段
     * @return
     */
    public static Map<String, String[]> getCnAndEnNames(Map<String, String> fields) {
        // 返货结果集map
        Map<String, String[]> resultMap = new HashMap<String, String[]>();

        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        // 获取迭代器
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        // 遍历数据存放中英文字段
        while (it.hasNext()) {
            String cntitle = it.next();
            String egtitle = fields.get(cntitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }
        resultMap.put("egtitles", egtitles);
        resultMap.put("cntitles", cntitles);
        return resultMap;
    }

    /**
     * list集合转为excel文件
     *
     * @param dataList 导入到excel中的数据
     * @param out      数据写入的文件
     * @param fields   需要注意的是这个方法中的map中：每一列对应的实体类的英文名为键，excel表格中每一列名为值
     * @param fileType 文件类型
     * @param <T>
     * @throws Exception
     */
    public static <T> void ListToExecl(List<T> dataList, OutputStream out, LinkedHashMap<String, String> fields, String fileType, Integer chu) throws Exception {
        // 建立文档
        Workbook workbook = null;
        // 校验文件类型
        if (CommonConstants.Excel_2003.equals(fileType)) {// 2003 版本的excel
            workbook = new HSSFWorkbook();
        } else if (CommonConstants.Excel_2007.equals(fileType)) {// 2007 版本的excel
            workbook = new XSSFWorkbook();
        }
        // 如果导入数据为空，则抛出异常。
        if (dataList == null || dataList.size() == 0) {
            // 将创建好的数据写入输出流
            workbook.write(out);
            // 关闭workbook
            workbook.close();
            throw new Exception("需导出的数据为空");
        }
        // 设置样式
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        // 根据dataList计算需要有多少页sheet
        int sheetSize = dataList.size() / sheetsize;
        if (dataList.size() % sheetsize > 0) {
            sheetSize += 1;
        }
        // 提取表格的字段名（英文字段名是为了对照中文字段名的）
        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        // 获取迭代器
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        // 遍历数据存放中英文字段
        while (it.hasNext()) {
            String cntitle = (String) it.next();
            String egtitle = fields.get(cntitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }
        // 添加数据
        for (int i = 0; i < sheetSize; i++) {
            int rownum = 0;
            // 计算每页的起始数据和结束数据
            int startIndex = i * sheetsize;
            int endIndex = (i + 1) * sheetsize - 1 > dataList.size() ? dataList.size()
                    : (i + 1) * sheetsize - 1;
            // 创建每页，并创建第一行
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(rownum);
            // 设置每一列的宽度
            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 4 * 256);
            sheet.setColumnWidth(2, 10 * 256);
            sheet.setColumnWidth(3, 20 * 256);
            sheet.setColumnWidth(4, 8 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 20 * 256);
            sheet.setColumnWidth(7, 15 * 256);
            sheet.setColumnWidth(8, 10 * 256);
            sheet.setColumnWidth(9, 15 * 256);
            sheet.setColumnWidth(10, 15 * 256);
            sheet.setColumnWidth(11, 8 * 256);
            sheet.setColumnWidth(12, 8 * 256);
            sheet.setColumnWidth(13, 15 * 256);
            sheet.setColumnWidth(14, 8 * 256);
            // 设置字体
            Font font = workbook.createFont();
            font.setFontName("宋体");
            // 设置excel数据字体大小
            font.setFontHeightInPoints((short) 9);

            // 在每页sheet的第一行中，添加字段名
            // 设置表头字段
            for (int f = 0; f < cntitles.length; f++) {
                Cell cell = row.createCell(f);
                cell.getCellStyle().setWrapText(true);// 自动换行
                cell.setCellStyle(style);
                RichTextString richString = null;
                // 添加表头数据
                if (CommonConstants.Excel_2003.equals(fileType)) {
                    richString = new HSSFRichTextString(cntitles[f]);
                } else if (CommonConstants.Excel_2007.equals(fileType)) {
                    richString = new XSSFRichTextString(cntitles[f]);
                }
                richString.applyFont(font);
                cell.setCellValue(richString);
            }
            rownum++;
            // 将数据添加进表格
            for (int j = startIndex; j < endIndex; j++) {
                row = sheet.createRow(rownum);
                T item = dataList.get(j);
                for (int h = 0; h < egtitles.length; h++) {
                    Field fd = item.getClass().getDeclaredField(egtitles[h]);
                    fd.setAccessible(true);
                    Object o = fd.get(item);
                    String value = o == null ? "" : o.toString();
                    Cell cell = row.createCell(h);
                    cell.setCellStyle(style);
                    cell.getCellStyle().setWrapText(true);//自动换行
                    RichTextString richString = null;
                    if (CommonConstants.Excel_2003.equals(fileType)) {
                        richString = new HSSFRichTextString(value);
                    } else if (CommonConstants.Excel_2007.equals(fileType)) {
                        richString = new XSSFRichTextString(value);
                    }
                    richString.applyFont(font);
                    cell.setCellValue(richString);
                }
                rownum++;
            }
        }
        // 将创建好的数据写入输出流
        workbook.write(out);
        // 关闭workbook
        workbook.close();
    }

    /**
     * list集合转为Workbook
     *
     * @param dataList 导入到Workbook中的数据
     * @param fields   需要注意的是这个方法中的map中：每一列对应的实体类的英文名为键，excel表格中每一列名为值
     * @param fileType 文件类型
     * @param <T>
     * @return
     */
    public static <T> Workbook ListToWorkbook(List<T> dataList, LinkedHashMap<String, String> fields, String fileType) throws Exception {
        // 建立文档
        Workbook workbook = null;
        // 校验文件类型
        if (CommonConstants.Excel_2003.equals(fileType)) {// 2003 版本的excel
            workbook = new HSSFWorkbook();
        } else if (CommonConstants.Excel_2007.equals(fileType)) {// 2007 版本的excel
            workbook = new XSSFWorkbook();
        }
        // 设置样式
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        // 根据dataList计算需要有多少页sheet
        int sheetSize = dataList.size() / sheetsize;
        // 提取表格的字段名（英文字段名是为了对照中文字段名的）
        String[] egtitles = new String[fields.size()];
        String[] cntitles = new String[fields.size()];
        // 获取迭代器
        Iterator<String> it = fields.keySet().iterator();
        int count = 0;
        // 遍历数据存放中英文字段
        while (it.hasNext()) {
            String cntitle = it.next();
            String egtitle = fields.get(cntitle);
            egtitles[count] = egtitle;
            cntitles[count] = cntitle;
            count++;
        }
        // 添加数据
        for (int i = 0; i < sheetSize; i++) {
            int rownum = 0;
            // 计算每页的起始数据和结束数据
            int startIndex = i * sheetsize;
            int endIndex = (i + 1) * sheetsize > dataList.size() ? dataList.size()
                    : (i + 1) * sheetsize;
            // 创建每页，并创建第一行
            Sheet sheet = workbook.createSheet();
            Row row = sheet.createRow(rownum);
            // 设置每一列的宽度
            sheet.setColumnWidth(0, 8 * 256);
            sheet.setColumnWidth(1, 4 * 256);
            sheet.setColumnWidth(2, 10 * 256);
            sheet.setColumnWidth(3, 20 * 256);
            sheet.setColumnWidth(4, 8 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 20 * 256);
            sheet.setColumnWidth(7, 15 * 256);
            sheet.setColumnWidth(8, 10 * 256);
            sheet.setColumnWidth(9, 15 * 256);
            sheet.setColumnWidth(10, 15 * 256);
            sheet.setColumnWidth(11, 8 * 256);
            sheet.setColumnWidth(12, 8 * 256);
            sheet.setColumnWidth(13, 15 * 256);
            sheet.setColumnWidth(14, 8 * 256);
            // 设置字体
            Font font = workbook.createFont();
            font.setFontName("宋体");
            // 设置excel数据字体大小
            font.setFontHeightInPoints((short) 9);

            // 在每页sheet的第一行中，添加字段名
            // 设置表头字段
            for (int f = 0; f < cntitles.length; f++) {
                Cell cell = row.createCell(f);
                cell.getCellStyle().setWrapText(true);// 自动换行
                cell.setCellStyle(style);
                RichTextString richString = null;
                // 添加表头数据
                if (CommonConstants.Excel_2003.equals(fileType)) {
                    richString = new HSSFRichTextString(cntitles[f]);
                } else if (CommonConstants.Excel_2007.equals(fileType)) {
                    richString = new XSSFRichTextString(cntitles[f]);
                }
                richString.applyFont(font);
                cell.setCellValue(richString);
            }
            rownum++;
            // 将数据添加进表格
            for (int j = startIndex; j < endIndex; j++) {
                row = sheet.createRow(rownum);
                T item = dataList.get(j);
                for (int h = 0; h < egtitles.length; h++) {
                    Field fd = item.getClass().getDeclaredField(egtitles[h]);
                    fd.setAccessible(true);
                    Object o = fd.get(item);
                    String value = o == null ? "" : o.toString();
                    Cell cell = row.createCell(h);
                    cell.setCellStyle(style);
                    cell.getCellStyle().setWrapText(true);//自动换行
                    RichTextString richString = null;
                    if (CommonConstants.Excel_2003.equals(fileType)) {
                        richString = new HSSFRichTextString(value);
                    } else if (CommonConstants.Excel_2007.equals(fileType)) {
                        richString = new XSSFRichTextString(value);
                    }
                    richString.applyFont(font);
                    cell.setCellValue(richString);
                }
                rownum++;
            }
        }
        return workbook;
    }


    /**
     * 保存文件到本地
     * 返回文件相关信息
     *
     * @param workbook 文件
     * @param ctxPath  保存文件根目录
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> void WorkToLocal(Workbook workbook, String ctxPath) throws Exception {
        try {
            // 设置文件名称
            String name = new SimpleDateFormat("ddHHmmss").format(new Date());
            String fileName = name + "customer_info.xls";
            // 获取文件
            File file = new File(ctxPath);
            // 校验文件夹是否存在
            if (!file.exists()) {
                // 创建文件根目录
                file.mkdirs();
            }
            // 设置文件保存路径
            String fileLink = file.getPath() + File.separator + fileName;
            // 转换斜杠
            if (fileLink.contains("\\")) {
                fileLink = fileLink.replace("\\", "/");
            }
            // 保存到当前路径fileLink
            OutputStream out = new FileOutputStream(fileLink);
            // 写入文件流
            workbook.write(out);
            // 关闭文件
            workbook.close();
            // 清空文件流
            out.flush();
            // 关闭文件流
            out.close();
        } catch (Exception e) {
            workbook.close();
            e.printStackTrace();
        }
    }


    /**
     * 判断是否是空行
     *
     * @param row    行对象
     * @param rowNum 需要判断的列数
     * @return
     */
    private static int getRowIsNull(Row row, int rowNum) {
        //空单元格的数量
        int num = 0;
        for (int i = 0; i < rowNum; i++) {
            Cell cell = row.getCell(i);
            //判断这个行是否为空
            if (null == cell) {
                num++;
            } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {  //空值
                num++;
            }
        }
        return num;
    }

    /**
     * 校验数据类型
     *
     * @param validType    数据类型
     * @param content      数据
     * @param cnNormalName 中文字段名
     * @return
     */
    public static String validData(Integer validType, String content, String cnNormalName) {
        String returnMsg = null;
        switch (EnumUtils.getEnumByType(validType, ValidTypeEnum.class)) {
            case NOTBLANK:
                if (StringUtils.isBlank(content))
                    returnMsg = cnNormalName + "不能为空";
                break;
            case CARNO:
                if (!ValidatorUtil.CarNum(content)) {
                    returnMsg = cnNormalName + "格式错误";
                }
                break;
            case CERTIFY:
                if (!CheckIdCard.check(content)) {
                    returnMsg = cnNormalName + "格式错误";
                }
                break;
            case PHONE:
                if (!ValidatorUtil.isMobile(content)) {
                    returnMsg = cnNormalName + "格式错误";
                }
                break;
            default:
                returnMsg = "";
                break;
        }
        return returnMsg;
    }

    /**
     * 根据字段名给对象的字段赋值
     *
     * @param fieldName  字段英文名
     * @param fieldValue 内容
     * @param o          对象
     * @throws Exception
     */
    private static void setFieldValueByName(String fieldName, Object fieldValue, Object o) throws Exception {
        // 根据字段名获取字段
        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            // 设置字段可访问
            field.setAccessible(true);
            // 获取字段类型
            Class<?> fieldType = field.getType();
            // 根据字段类型给字段赋值
            if (String.class == fieldType) {
                // 字符串类型
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                // int类型
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                // long类型
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                // float类型
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                // double类型
                if (!"".equals(fieldValue.toString())) {
                    // 内容不为空
                    field.set(o, Double.valueOf(fieldValue.toString()));
                }
            } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                // short类型
                if (!"".equals(fieldValue.toString())) {
                    // 内容不为空
                    field.set(o, Short.valueOf(fieldValue.toString()));
                }
            } else if (Character.TYPE == fieldType) {
                // char类型
                if ((fieldValue != null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character.valueOf(fieldValue.toString().charAt(0)));
                }
            } else if (Date.class == fieldType) {
                // 日期类型(格式化数据)
                field.set(o, new SimpleDateFormat("yyyy-MM-dd").parse(fieldValue.toString()));
            } else {
                field.set(o, fieldValue);
            }
        } else {
            throw new Exception(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        }
    }

    /**
     * 根据字段名获取字段
     *
     * @param fieldName 字段英文名
     * @param clazz     类
     * @return 结果 字段
     */
    private static Field getFieldByName(String fieldName, Class<?> clazz) {
        // 拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();
        // 如果本类中存在该字段，则返回
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        // 否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        // 如果本类和父类都没有，则返回空
        return null;
    }

    /**
     * 获取单元格数据
     *
     * @param cell
     * @return
     */
    public static Object getCellValue(Cell cell) {
        Object obj = "";
        if (cell == null) {
            return obj;
        }
        // 获取数据类型
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                // 布尔类型
                obj = cell.getBooleanCellValue();
                break;
            case FORMULA:
                // 公式型
                try {
                    obj = String.valueOf(cell.getStringCellValue());
                } catch (IllegalStateException e) {
                    // 将值转为字符串
                    String valueOf = String.valueOf(cell.getNumericCellValue());
                    // 转换为BigDecimal类型
                    BigDecimal bd = new BigDecimal(Double.valueOf(valueOf));
                    // 获取两位小数数据
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    obj = bd;
                }
                break;
            case ERROR:
                // 错误
                obj = cell.getErrorCellValue();
                break;
            case NUMERIC:
                // 数值型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 数据是日期格式
                    Date date = cell.getDateCellValue();
                    obj = DateUtils.format(date, "yyyy-MM-dd");
                } else {
                    obj = cell.getNumericCellValue();
                    DecimalFormat df = new DecimalFormat("0");
                    obj = df.format(obj);
                }
                break;
            case STRING:
                // 字符串型(去除空格换行)
                String value = String.valueOf(cell.getStringCellValue());
                value = value.replace(" ", "");
                value = value.replace("\n", "");
                value = value.replace("\t", "");
                obj = value;
                break;
            default:
                break;
        }
        return obj;
    }

}