package com.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.util.DateUtils;

/**
 * 
 * 日期操作工具类
 * 
 * @author fum
 */
public class DateUtils {
    
    private static final Logger logger = Logger.getLogger(DateUtils.class);
    
    public static final String NORMAL_DATE_FORMAT_NEW = "yyyy-mm-dd hh24:mi:ss";
    
    // 日期格式，年份，例如：2004，2008
    public static final String DATE_FORMAT_YYYY = "yyyy";
    
    // 日期格式，年份和月份，例如：200707，200808
    public static final String DATE_FORMAT_YYYYMM = "yyyyMM";
    
    // 日期格式，年月日，例如：20050630，20080808
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    
    // 日期格式，年月日，用横杠分开，例如：2006-12-25，2008-08-08
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    
    // 日期格式，年月，用横杠分开，例如：2006-12，2008-08
    public static final String DATE_FORMAT_YYYY_MM = "yyyy-MM";
    
    // 日期格式，年月日时分秒，例如：20001230120000，20080808200808
    public static final String DATE_TIME_FORMAT_YYYYMMDDHHMISS = "yyyyMMddHHmmss";
    
    // 时间格式，时分秒，例如：12:00:00，20:08:08
    public static final String DATE_TIME_FORMAT_HHMISS = "HH:mm:ss";
    
    // 日期格式，年月日时分秒，年月日用横杠分开，时分秒用冒号分开，
    // 例如：2005-05-10 23：20：00，2008-08-08 20:08:08
    public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";
    
    // 日期格式，年月日时分，年月日用横杠分开，时分秒用冒号分开，
    public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI = "yyyy-MM-dd HH:mm";
    
    public static Long strDateToNum(String paramString) throws Exception {
        if(paramString == null) return null;
        String[] arrayOfString = null;
        String str = "";
        if(paramString.indexOf("-") >= 0) {
            arrayOfString = paramString.split("-");
            for(int i = 0; i < arrayOfString.length; ++i)
                str = str + arrayOfString[i];
            return Long.valueOf(Long.parseLong(str));
        }
        return Long.valueOf(Long.parseLong(paramString));
    }
    
    public static Long strDateToNum1(String paramString) throws Exception {
        if(paramString == null) return null;
        String[] arrayOfString = null;
        String str = "";
        if(paramString.indexOf("-") >= 0) {
            arrayOfString = paramString.split("-");
            for(int i = 0; i < arrayOfString.length; ++i)
                if(arrayOfString[i].length() == 1)
                    str = str + "0" + arrayOfString[i];
                else str = str + arrayOfString[i];
            return Long.valueOf(Long.parseLong(str));
        }
        return Long.valueOf(Long.parseLong(paramString));
    }
    
    public static String numDateToStr(Long paramLong) {
        if(paramLong == null) return null;
        String str = null;
        str = paramLong.toString().substring(0, 4)
                + "-" + paramLong.toString().substring(4, 6) + "-" + paramLong.toString().substring(6, 8);
        return str;
    }
    
    public static Date stringToDate(String paramString1, String paramString2) throws Exception {
        if(paramString1 == null || paramString1.equals("")) {
            return null;
        }
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString2);
        localSimpleDateFormat.setLenient(false);
        try {
            return localSimpleDateFormat.parse(paramString1);
        } catch (ParseException localParseException) {
            throw new Exception("解析日期字符串时出错！");
        }
    }
    
    /**
     * 将日期按照指定的格式转为字符串
     * 
     * @author lisheng
     * @date 2016年12月27日 下午9:53:48
     * @return String
     * @param paramDate
     * @param paramString
     * @return
     */
    public static String dateToString(Date paramDate, String paramString) {
        if(paramDate == null || paramDate.equals("")) {
            return null;
        }
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString);
        localSimpleDateFormat.setLenient(false);
        return localSimpleDateFormat.format(paramDate);
    }
    
    public static Date compactStringToDate(String paramString) throws Exception {
        return stringToDate(paramString, "yyyyMMdd");
    }
    
    public static String dateToCompactString(Date paramDate) {
        return dateToString(paramDate, "yyyyMMdd");
    }
    
    public static String dateToNormalString(Date paramDate) {
        return dateToString(paramDate, "yyyy-MM-dd");
    }
    
    public static String compactStringDateToNormal(String paramString) throws Exception {
        return dateToNormalString(compactStringToDate(paramString));
    }
    
    public static int getDaysBetween(Date paramDate1, Date paramDate2) throws Exception {
        Calendar localCalendar1 = Calendar.getInstance();
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar1.setTime(paramDate1);
        localCalendar2.setTime(paramDate2);
        if(localCalendar1.after(localCalendar2)) throw new Exception("起始日期小于终止日期!");
        int i = localCalendar2.get(6) - localCalendar1.get(6);
        int j = localCalendar2.get(1);
        if(localCalendar1.get(1) != j) {
            localCalendar1 = (Calendar) localCalendar1.clone();
            do {
                i += localCalendar1.getActualMaximum(6);
                localCalendar1.add(1, 1);
            } while (localCalendar1.get(1) != j);
        }
        return i;
    }
    
    /**
     * 在当前日期上加减天数
     * @author lisheng
     * @date 2016年12月27日 下午10:00:54
     * @return Date
     * @param paramDate
     * @param paramInt
     * @return
     * @throws Exception
     */
    public static Date addDays(Date paramDate, int paramInt) throws Exception {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        int i = localCalendar.get(6);
        localCalendar.set(6, i + paramInt);
        return localCalendar.getTime();
    }
    
    public static Date addDays(String paramString1, String paramString2, int paramInt) throws Exception {
        Calendar localCalendar = Calendar.getInstance();
        Date localDate = stringToDate(paramString1, paramString2);
        localCalendar.setTime(localDate);
        int i = localCalendar.get(6);
        localCalendar.set(6, i + paramInt);
        return localCalendar.getTime();
    }
    
    public static java.sql.Date getSqlDate(Date paramDate) throws Exception {
        java.sql.Date localDate = new java.sql.Date(paramDate.getTime());
        return localDate;
    }
    
    public static String formatDate(Date paramDate) {
        if(paramDate == null) return null;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        localSimpleDateFormat.setLenient(false);
        return localSimpleDateFormat.format(paramDate);
    }
    
    public static String formatDateTime(Date paramDate) {
        if(paramDate == null) return null;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        localSimpleDateFormat.setLenient(false);
        return localSimpleDateFormat.format(paramDate);
    }
    
    public static Date parseDate(String paramString) throws Exception {
        if((paramString == null) || (paramString.trim().equals(""))) return null;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        localSimpleDateFormat.setLenient(false);
        try {
            return localSimpleDateFormat.parse(paramString);
        } catch (ParseException localParseException) {
            throw new Exception("日期解析出错！", localParseException);
        }
    }
    
    public static Date parseDateTime(String paramString) throws Exception {
        if((paramString == null) || (paramString.trim().equals(""))) return null;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        localSimpleDateFormat.setLenient(false);
        try {
            return localSimpleDateFormat.parse(paramString);
        } catch (ParseException localParseException) {
            throw new Exception("时间解析异常！", localParseException);
        }
    }
    
    public static Integer getYM(String paramString) throws Exception {
        if(paramString == null) return null;
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        localSimpleDateFormat.setLenient(false);
        Date localDate;
        try {
            localDate = localSimpleDateFormat.parse(paramString);
        } catch (ParseException localParseException) {
            throw new Exception("时间解析异常！", localParseException);
        }
        return getYM(localDate);
    }
    
    public static Integer getYM(Date paramDate) {
        if(paramDate == null) return null;
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        int i = localCalendar.get(1);
        int j = localCalendar.get(2) + 1;
        return new Integer(i * 100 + j);
    }
    
    public static int addMonths(int paramInt1, int paramInt2) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.set(1, paramInt1 / 100);
        localCalendar.set(2, paramInt1 % 100 - 1);
        localCalendar.set(5, 1);
        localCalendar.add(2, paramInt2);
        return getYM(localCalendar.getTime()).intValue();
    }
    
    /**
     * 在当前日期基础上加减月份
     * 
     * @author lisheng
     * @date 2016年12月27日 下午9:51:21
     * @return Date
     * @param paramDate
     * @param paramInt
     * @return
     */
    public static Date addMonths(Date paramDate, int paramInt) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramDate);
        localCalendar.add(2, paramInt);
        return localCalendar.getTime();
    }
    
    public static int monthsBetween(int paramInt1, int paramInt2) {
        int i = paramInt2 / 100 * 12 + paramInt2 % 100 - (paramInt1 / 100 * 12 + paramInt1 % 100);
        return i;
    }
    
    public static int monthsBetween(Date paramDate1, Date paramDate2) {
        return monthsBetween(getYM(paramDate1).intValue(), getYM(paramDate2).intValue());
    }
    
    public static String getChineseDate(Calendar paramCalendar) {
        int i = paramCalendar.get(1);
        int j = paramCalendar.get(2);
        int k = paramCalendar.get(5);
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(i);
        localStringBuffer.append("年");
        localStringBuffer.append(j + 1);
        localStringBuffer.append("月");
        localStringBuffer.append(k);
        localStringBuffer.append("日");
        return localStringBuffer.toString();
    }
    
    public static String getChineseWeekday(Calendar paramCalendar) {
        switch (paramCalendar.get(7)) {
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            case 1:
                return "星期日";
        }
        return "未知";
    }
    
    public static String longToDateStr(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
    
    /**
     * 把日期字符串转换为日期时间
     * 
     * @author lisheng
     * @date 2016年12月27日 下午9:58:28
     * @return Date
     * @param strDateTime
     * @param fromat
     * @return
     */
    public static Date strToDateTime(String strDateTime, String fromat) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(fromat);
        Date dateTime = null;
        
        try {
            dateTime = dateTimeFormat.parse(strDateTime);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            
        }
        return dateTime;
    }
    
    /**
     * 日期转换为字符串
     * 
     * @param Date
     *            date：需要转换的日期 @param String format：转换格式 @return String @throws
     */
    public static String dateToStr(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        
        return dateFormat.format(date);
    }
    
    /**
     * 日期时间转换为字符串
     * 
     * @param Date
     *            date：需要转换的日期 @param String format：转换格式 @return String @throws
     */
    public static String dateTimeToStr(Date date, String format) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(format);
        
        return dateTimeFormat.format(date);
    }
    
    /**
     * 得到当天的最后时间,today是字符串类型"yyyy-mm-dd", 返回是日期类型"yyyy-mm-dd 23:59:59"
     * 
     * @param String
     *            today @return Date @throws
     */
    public static Date getTodayLastTime(String today) {
        String todayLastTime = today + " 23:59:59";
        
        return strToDateTime(todayLastTime, DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
    }
    
    /**
     * dateStr是字符串类型"yyyymmddhhmiss", 返回是日期类型"yyyy年mm月dd日,hh时mi分ss秒"
     * 
     * @param String
     *            dateStr @return String @throws
     */
    public static String timeStrToTimeCNFormat(String dateStr) {
        String y = dateStr.substring(0, 4) + "年";
        String m = dateStr.substring(4, 6) + "月";
        String d = dateStr.substring(6, 8) + "日";
        String h = dateStr.substring(8, 10) + "时";
        String i = dateStr.substring(10, 12) + "分";
        String s = dateStr.substring(12, 14) + "秒";
        
        return (y + m + d + h + i + s);
    }
    
    /**
     * dateStr是字符串类型"yyyymmddhhmiss", 返回是日期类型"yyyy年mm月dd日"
     * 
     * @param String
     *            dateStr @return String @throws
     */
    public static String timeStrToDateCNFormat(String dateStr) {
        String y = dateStr.substring(0, 4) + "年";
        String m = dateStr.substring(4, 6) + "月";
        String d = dateStr.substring(6, 8) + "日";
        
        return (y + m + d);
    }
    
    /**
     * 两个时间之间的天数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static float getDaysBy2Time(String date1, String date2, String format) {
        float day = 0;
        
        if(date1 == null || date1.equals("")) {
            return 0;
        }
        if(date2 == null || date2.equals("")) {
            return 0;
        }
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat(format);
        Date date = null;
        Date mydate = null;
        
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        
        return day;
    }
    
    /**
     * 两个时间之间的秒数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static Integer getSecondsBy2Time(String date1, String date2, String format) {
        Integer seconds = null;
        
        if(date1 == null || date1.equals("")) {
            return 0;
        }
        if(date2 == null || date2.equals("")) {
            return 0;
        }
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat(format);
        Date date = null;
        Date mydate = null;
        
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
            seconds = (int) (date.getTime() - mydate.getTime()) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        
        return seconds;
    }
    
    public static Date GMTStringToData(String strgmt) {
        if(strgmt != null && strgmt.length() > 0) {
            try {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String oldtime = strgmt.replace('T', ' ').substring(0, strgmt.indexOf('+'));
                Date sourceDate = sf.parse(oldtime);
                
                return sourceDate;
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }
    
    public static String GMTStringToDataString(String strgmt) {
        if(strgmt != null && strgmt.length() > 0) {
            try {
                String oldtime = strgmt.replace('T', ' ').substring(0, strgmt.indexOf('+'));
                
                return oldtime;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }
    
    public static String minusTime(Date startDate, Date endDate) {
        if(null == startDate || null == endDate) {
            return "0";
        }
        
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        
        if(endTime < startTime) {
            return "0";
        }
        
        long between = endTime - startTime;
        long dayTime = between / (24 * 60 * 60 * 1000);
        long hourTime = (between / (60 * 60 * 1000) - dayTime * 24);
        long minuteTime = ((between / (60 * 1000)) - dayTime * 24 * 60 - hourTime * 60);
        long secondTime = (between / 1000 - dayTime * 24 * 60 * 60 - hourTime * 60 * 60 - minuteTime * 60);
        
        String spendTime = String.valueOf(dayTime)
                + ":" + formatTime(String.valueOf(hourTime)) + ":" + formatTime(String.valueOf(minuteTime)) + ":"
                + formatTime(String.valueOf(secondTime));
        
        return spendTime;
    }
    
    public static String getIntervalIime(long between) {
        
        long dayTime = between / (24 * 60 * 60 * 1000);
        long hourTime = (between / (60 * 60 * 1000) - dayTime * 24);
        long minuteTime = ((between / (60 * 1000)) - dayTime * 24 * 60 - hourTime * 60);
        long secondTime = (between / 1000 - dayTime * 24 * 60 * 60 - hourTime * 60 * 60 - minuteTime * 60);
        
        String spendTime = String.valueOf(dayTime)
                + ":" + formatTime(String.valueOf(hourTime)) + ":" + formatTime(String.valueOf(minuteTime)) + ":"
                + formatTime(String.valueOf(secondTime));
        
        return spendTime;
    }
    
    public static int compare_date(String DATE1, String DATE2) {
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        if(null == DATE2) {
            return 3;
        }
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if(dt1.getTime() > dt2.getTime()) {
                // System.out.println("dt1 在dt2前");
                return 1;
            } else if(dt1.getTime() < dt2.getTime()) {
                // System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error(exception.getMessage(), exception);
        }
        return 3;
    }
    
    private static String formatTime(String timeStr) {
        if(timeStr.length() == 1) {
            timeStr = "0" + timeStr;
        }
        return timeStr;
    }
    
    /**
     * 获取两个日期间的小时和分钟数
     * 
     * @author lisheng
     * @date 2015年7月24日 下午3:28:12
     * @return String
     * @param startDate
     * @param endDate
     * @return
     */
    public static String getHoursAndMinutes(Date startDate, Date endDate) {
        if(null == startDate || null == endDate) {
            return "0";
        }
        
        long startTime = startDate.getTime();// 得出来的是毫秒数
        long endTime = endDate.getTime();// 得出来的是毫秒数
        
        if(endTime < startTime) {
            return "0";
        }
        
        long between = endTime - startTime;// 间隔毫秒数
        // long dayTime = between / (24 * 60 * 60 * 1000);
        long hourTime = between / (60 * 60 * 1000);// 间隔总小时数
        long minuteTime = (between / (60 * 1000)) - hourTime * 60;// 剩余分钟数
        
        String spendTime = String.valueOf(hourTime) + "小时" + formatTime(String.valueOf(minuteTime)) + "分钟";
        
        return spendTime;
    }
    
    /**
     * 获取两个日期间的分钟数
     * 
     * @author lisheng
     * @date 2015年7月28日 下午4:42:53
     * @return String
     * @param startDate
     * @param endDate
     */
    public static String getMinutes(Date startDate, Date endDate) {
        if(null == startDate || null == endDate) {
            return "0";
        }
        
        long startTime = startDate.getTime();// 得出来的是毫秒数
        long endTime = endDate.getTime();// 得出来的是毫秒数
        
        if(endTime < startTime) {
            return "0";
        }
        
        long between = endTime - startTime;// 间隔毫秒数
        long minuteTime = (between / (60 * 1000));
        
        return String.valueOf(minuteTime);
    }
    
    /**
     * 获取指定日期的第一天
     * 
     * @author Nick
     * @date 2016年9月25日 下午12:33:41
     * @return String
     * @param datadate
     * @return
     * @throws Exception
     */
    public static String getFirstDay(String dateStr) throws Exception {
        Date date = null;
        String day_first = null;
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
        date = format.parse(dateStr);
        
        // 创建日历
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        day_first = format.format(calendar.getTime());
        return day_first;
    }
    
    /**
     * 获取指定日期的最后一天
     * 
     * @author Nick
     * @date 2016年9月25日 下午12:34:28
     * @return String
     * @param datadate
     * @return
     * @throws Exception
     */
    public static String getLastDay(String dateStr) throws Exception {
        Date date = null;
        String day_last = null;
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD);
        date = format.parse(dateStr);
        
        // 创建日历
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
        day_last = format.format(calendar.getTime());
        return day_last;
    }
    
    public static void main(String[] paramArrayOfString) throws Exception {
        // try {
        // System.out.println(formatDate(addMonths(parseDate("2013-01-06"), 12)));
        // } catch (Exception localException) {
        // System.out.println(localException);
        // }
        
        /*
         * Date t1 = new Date();
         * 
         * Thread.sleep(10000); Date t2 = new Date();
         * 
         * System.out.println(minusTime(t1, t2));
         */
        //
        // String starTime="2015-07-24 14:10:00";
        // String endTime="2015-07-24 16:50:50";
        // Date startDate = LangUtils.parseDate(starTime);
        // Date endDate = LangUtils.parseDate(endTime);
        // System.out.println(getHoursAndMinutes(startDate, endDate));
        
        String dateStr = "2016-12-25";
        
        System.out.println(DateUtils.getFirstDay(dateStr));
        System.out.println(DateUtils.getLastDay(dateStr));
        
        //System.out.println(LangUtils.formatDate(DateUtils.addMonths(new Date(), -2),"yyyy-MM-dd"));
        
    }
    
    public static String getCurrentDateAndTime() {
		return DateUtils.getCurrentDate("yyyyMMddHHmmss");
	}
    
    public static String getCurrentDate(String aFormat) {
		String tDate = new SimpleDateFormat(aFormat).format(new java.util.Date(System.currentTimeMillis()));
		return tDate;
	}
}
