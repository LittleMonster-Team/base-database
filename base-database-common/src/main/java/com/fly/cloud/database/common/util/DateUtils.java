package com.fly.cloud.database.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import com.fly.cloud.database.common.constant.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 日期处理
 *
 * @description: 日期处理
 * @program: base-database
 * @author: xux
 * @date: 2020-09-16 17:37:41
 **/
public class DateUtils {
    /**
     * 时间格式(yyyy-MM)
     */
    public final static String DATE_DAY = "MM-dd";
    /**
     * 时间格式(yyyy-MM)
     */
    public final static String DATE_MONTH = "yyyy-MM";
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";


    /**
     * 日期格式化
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_PATTERN
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 获取当前日期(格式：yyyy-MM-dd HH:mm:ss)
     * <p>
     * DATE_TIME_PATTERN
     */
    public static String getCurrentDate() {
        DateFormat fmt = new SimpleDateFormat(DATE_TIME_PATTERN);
        return fmt.format(new Date());
    }

    /**
     * 获取当前日期(格式：MM-dd)
     * <p>
     * DATE_TIME_PATTERN
     */
    public static String getDayTime() {
        DateFormat fmt = new SimpleDateFormat(DATE_DAY);
        return fmt.format(new Date());
    }

    /**
     * 获取当前日期(格式：yyyy-MM-dd)
     * <p>
     * DATE_TIME_PATTERN
     */
    public static String getCurrentTime() {
        DateFormat fmt = new SimpleDateFormat(DATE_PATTERN);
        return fmt.format(new Date());
    }

    /**
     * 字符串转换成自定义格式日期
     *
     * @param strDate 日期字符串
     * @param pattern 日期的格式，如：DateUtils.DATE_TIME_PATTERN
     */
    public static Date stringToDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseLocalDateTime(strDate).toDate();
    }

    /**
     * 返回上一个月的日期
     *
     * @param date 日期
     * @return 返回yyyy-MM格式日期
     */
    public static String lastMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return format(cal.getTime(), DATE_MONTH);
    }

    /**
     * 获取当前年份
     */
    public static String getYear() {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year;
    }

    /**
     * 获取当前月份
     * <p>
     * DATE_TIME_PATTERN
     */
    public static String getMonth() {
        Date date = new Date();
        Calendar from = Calendar.getInstance();
        from.setTime(date);
        int fromMonth = from.get(Calendar.MONTH) + 1;
        String month = DateUtils.padDatazero(Integer.parseInt(fromMonth + ""));
        return month;
    }

    /**
     * 获取n年前后之间年份
     */
    public static List<String> getNumYear(int num) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i >= num; i--) {
            Calendar calendar = Calendar.getInstance();
            //过去num年
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, i);
            String year = String.valueOf(calendar.get(Calendar.YEAR));
            list.add(year);
        }
        return list;
    }

    /**
     * 根据周数，获取开始日期、结束日期
     *
     * @param week 周期  0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        DateTime dateTime = new DateTime();
        LocalDate date = new LocalDate(dateTime.plusWeeks(week));
        date = date.dayOfWeek().withMinimumValue();
        Date beginDate = date.toDate();
        Date endDate = date.plusDays(6).toDate();
        return new Date[]{beginDate, endDate};
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusSeconds(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMinutes(minutes).toDate();
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusHours(hours).toDate();
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusDays(days).toDate();
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusWeeks(weeks).toDate();
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusMonths(months).toDate();
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date  日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.plusYears(years).toDate();
    }

    /**
     * 获取某时间段内的某个时间
     *
     * @param start
     * @param end
     * @return
     */
    public static List<String> getBetweenDates(String start, String end) {
        List<String> result = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat rsdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start_date = sdf.parse(start);
            Date end_date = sdf.parse(end);
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start_date);
            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end_date);
            while (tempStart.before(tempEnd) || tempStart.equals(tempEnd)) {
                result.add(rsdf.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取某时间段内的某个时间
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static String randomDate(String beginDate, String endDate, boolean isDate) {
        // TODO Auto-generated method stub
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);
            if (start.getTime() >= end.getTime()) {
                return null;
            }
            long date = random(start.getTime(), end.getTime());
            Date sr = new Date(date);
            String d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(date));
            if (isDate) {
                return d.substring(0, 10);
            } else {
                return d.substring(10);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取两数之间随机数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long random(long begin, long end) {
        // TODO Auto-generated method stub
        long rtn = begin + (long) (Math.random() * (end - begin));
        if (rtn == begin || rtn == end) {
            return random(begin, end);
        }
        return rtn;
    }

    /**
     * 毫秒值转日期
     *
     * @param time
     * @return
     */
    public static String getDateByTime(String time) {
        Long t = Long.parseLong(time);
        Date d = new Date(t);
        return format(d, DATE_TIME_PATTERN);
    }

    /**
     * 数据补零
     *
     * @param s
     * @return
     */
    public static String padDatazero(int s) {
        return s < 10 ? "0" + s : "" + s;
    }

    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     *
     * @param beginDate
     * @param endDate
     * @return List<Date>
     * @throws ParseException
     */
    public static List<String> getDatesBetweenTwoDate(String beginDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> lDate = new ArrayList<String>();
        lDate.add(beginDate);//把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(sdf.parse(beginDate));
        boolean bContinue = true;
        while (bContinue) {
            //根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (sdf.parse(endDate).after(cal.getTime())) {
                lDate.add(sdf.format(cal.getTime()));
            } else {
                break;
            }
        }
        lDate.add(endDate);//把结束时间加入集合
        return lDate;
    }

    /**
     * 判断时间是否在范围内
     *
     * @param createTime
     * @param beginTime
     * @param endTime
     * @return
     * @throws Exception
     */
    public static Boolean isInTime(String createTime, String beginTime, String endTime) {
        return DateUtil.isIn(DateUtil.parse(createTime, "yyyy-MM-dd HH:mm:ss")
                , DateUtil.parse(beginTime, "yyyy-MM-dd HH:mm:ss")
                , DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss")
        );
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @return str
     */
    public static String DateToStr(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String str = format.format(date);
        return str;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取两个日期之间的毫秒值
     *
     * @param date1
     * @param date2
     * @return date
     */
    public static Long daysDifference(Date date1, Date date2) {
        // 获取相差的天数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long timeInMillis1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long timeInMillis2 = calendar.getTimeInMillis();
        long betweenDays = (timeInMillis2 - timeInMillis1) / (1000L * 3600L * 24L);
        return betweenDays;
    }

    /**
     * 获取当前年份1月至今(不包括当前月)的所有月份
     */
    public static List<String> getcurrentMonthList() {
        List<String> reList = new ArrayList<String>();
        Calendar canlendar = Calendar.getInstance();
        int curmonth = canlendar.get(Calendar.MONTH) + 1;
        for (int i = 0; i < curmonth - 1; i++) {
            canlendar.set(Calendar.DAY_OF_MONTH, 0);
            reList.add(format(canlendar.getTime(), DATE_MONTH));
        }
        return reList;
    }

    /**
     * 截取日期
     *
     * @param workShiftList
     * @return
     */
    public static String subDate(JSONArray workShiftList) {
        String workShifTime = (String) workShiftList.get(1);// 2020-05-01
        String creationTime = workShifTime.substring(0, 7);// 截取年月
        return creationTime;
    }


    /**
     * 获取下一月的当前日期
     *
     * @param dataTime
     * @return
     */
    public static String getLastDateMonth(String dataTime) {
        LocalDate today = LocalDate.parse(dataTime);
        //当前月份+（-1）
        today = today.minusMonths(-1);
        //LocalDate日期格式是"YYYY-MM-DD"，只需要用toString()就可以转化成字符串类型
        return today.toString();
    }

    /**
     * 获取当前日期半年后的日期
     *
     * @param dataTime
     * @return
     */
    public static String getPastHalfYear(String dataTime, Integer month) {
        Calendar c = Calendar.getInstance();//获得一bai个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(dataTime);//初始日期
        } catch (Exception e) {
        }
        c.setTime(date);//设置du日历时间
        c.add(Calendar.MONTH, month);//在日历的月份zhi上增加6个月
        return sdf.format(c.getTime());

    }

    /**
     * 获取当前月的第一天与最后一天
     *
     * @return
     */
    public static Map<String, String> getFirstdayAndlastday() {
        Map<String, String> map = new HashMap<>();
        // 获取当前年份、月份、日期
        Calendar cale = null;
        // 获取当月第一天和最后一天
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String firstday, lastday;
        // 获取当前月的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        firstday = format.format(cale.getTime());
        // 获取当前月的最后一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        lastday = format.format(cale.getTime());
        map.put("firstday", firstday);
        map.put("lastday", lastday);
        return map;
    }

    /**
     * 获取n分钟后的时间
     *
     * @param minute n分钟
     * @return
     */
    public static String getTimeByMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    /**
     * 毫秒值转时分秒
     *
     * @param time 毫秒值
     * @return
     */
    public static String getGapTime(long time) {
        long hours = time / (1000 * 60 * 60);
        long minutes = (time - hours * (1000 * 60 * 60)) / (1000 * 60);
        String diffTime = "";
        if (minutes < 10) {
            diffTime = hours + ":0" + minutes;
        } else {
            diffTime = hours + ":" + minutes;
        }
        return diffTime;
    }

    /**
     * 创建定时器
     */
    public static void createTimer() {
        try {
            // 创建定时器
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("任务执行了---------" + new Date());
                }
            }, 0, 5000);// 五秒执行一次
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拼接当天开始结束时间
     *
     * @param startStr
     * @param endStr
     * @return
     */
    public static Map<String, String> splicingTime(String startStr, String endStr) {
        Map<String, String> map = new HashMap<String, String>();
        String[] startTimeSplit = startStr.split(" ");
        String[] endTimeSplit = endStr.split(" ");
        // 获取当天时间
        String currentTime = getCurrentTime();
        // 拼接开始时间
        String startTime = currentTime + " " + startTimeSplit[1];
        // 拼接结束时间
        String endTime = currentTime + " " + endTimeSplit[1];
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }

    /**
     * 计算2个日期之间相差的  相差多少年月日
     * 比如：2011-02-02 到  2017-03-02 相差 6年，1个月，0天
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Map<String, Integer> dayComparePrecise(Date fromDate, Date toDate) {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int fromDay = from.get(Calendar.DAY_OF_MONTH);
        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int toDay = to.get(Calendar.DAY_OF_MONTH);
        int year = toYear - fromYear;
        int month = toMonth - fromMonth;
        int day = toDay - fromDay;
        Map<String, Integer> map = new HashMap<>();
        map.put("year", year);
        map.put("month", month);
        map.put("day", day);
        return map;
    }

    /**
     * 计算2个日期之间相差的  以年、月、日为单位，各自计算结果是多少
     * 比如：2011-02-02 到  2017-03-02
     * 以年为单位相差为：6年
     * 以月为单位相差为：73个月
     * 以日为单位相差为：2220天
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Map<String, Integer> dayCompare(Date fromDate, Date toDate) {
        Calendar from = Calendar.getInstance();
        from.setTime(fromDate);
        Calendar to = Calendar.getInstance();
        to.setTime(toDate);
        //只要年月
        int fromYear = from.get(Calendar.YEAR);
        int fromMonth = from.get(Calendar.MONTH);
        int toYear = to.get(Calendar.YEAR);
        int toMonth = to.get(Calendar.MONTH);
        int year = toYear - fromYear;
        int month = toYear * 12 + toMonth - (fromYear * 12 + fromMonth);
        int day = (int) ((to.getTimeInMillis() - from.getTimeInMillis()) / (24 * 3600 * 1000));
        Map<String, Integer> map = new HashMap<>();
        map.put("year", year);
        map.put("month", month);
        map.put("day", day);
        return map;
    }

    /**
     * 计算2个日期相差多少年
     * 列：2011-02-02  ~  2017-03-02 大约相差 6.1 年
     *
     * @param fromDate
     * @param toDate
     * @return
     */
    public static Map<String, Object> yearCompare(String fromDate, String toDate) {
        Date date1 = StrToDate(fromDate);
        Date date2 = StrToDate(toDate);
        Map<String, Integer> timeMap = dayComparePrecise(date1, date2);
        double month = timeMap.get("month");
        double year = timeMap.get("year");
        // 返回2位小数，并且四舍五入
        DecimalFormat df = new DecimalFormat("######0.0");
        int useYear = (int) (year + month / 12);
        String useYears = df.format(year + month / 12);
        Map<String, Object> map = new HashMap<>();
        map.put("useYear", useYear);
        map.put("useYears", useYears);
        return map;
    }

    public static void main(String[] args) throws Exception {
        List<String> list = new ArrayList<>();
        List<String> chuList = new ArrayList<>();
        List<String> yuList = new ArrayList<>();
//        for (int i = 1; i <= 1234567; i++) {
//            list.add(i + "");
//        }
        long chu = 10 / 10000;
        long yu = 10 % 10000;
        System.out.println(chu);
        System.out.println(yu);
//        for (long index = 0; index < chu; index++) {
//            chuList = list.subList((int) index * 10000, (int) index * 10000 + 10000);
//            for (String s : chuList) {
//                System.out.println("chuList：" + s);
//            }
//        }
//        yuList = list.subList((int) (list.size() - yu), list.size());
//        for (String s : yuList) {
//            System.out.println("yuList：" + s);
//        }

    }
}