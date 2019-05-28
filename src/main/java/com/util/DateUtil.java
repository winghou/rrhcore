package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

public class DateUtil
{
    private static Logger logger = Logger.getLogger(DateUtil.class);

    /******************************** 以下是时间类常量 **************************************/
    /**
     * 年月日时分秒，时间格式
     */
    public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 年月日时分，时间格式
     */
    public final static String DATETIME_NO_SEC_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 年月日，时间格式
     */
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 月日，时间格式
     */
    public final static String DATE_FORMAT_NO_YEAR = "MM/dd";

    /**
     * 时分秒，时间格式
     */
    public final static String TIME_FORMAT = "HH:mm:ss";

    public final static String[] DATE_FORMAT_PATTERNS = new String[]
    { "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyyMMdd", "yyyy-MM-dd", "yyyy年MM月dd日", "yyyy/MM/dd", "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyyMMddHHmm", "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm", "yyyy-MM-dd HH:mm:ss.SSS" };

    public static Date parseDate(String dateStr)
    {
        if (StringUtils.isEmpty(dateStr))
        {
            return null;
        }
        try
        {
            return DateUtils.parseDate(dateStr, DATE_FORMAT_PATTERNS);
        }
        catch (Exception e)
        {
            logger.error(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String format(Date date, String pattern)
    {
        if (date == null || StringUtil.isBlank(pattern))
        {
            return "";
        }
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 得到当前日期，格式'yyyy-MM-dd'
     */
    public static String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        return format(calendar.getTime(), DATE_FORMAT);
    }

    /**
     * 得到当前日期时间，格式'yyyy-MM-dd'
     */
    public static String getCurrentDateTime()
    {
        Calendar calendar = Calendar.getInstance();
        return format(calendar.getTime(), DATETIME_FORMAT);
    }

    /**
     * 日期增加天数后得到新的日期
     */
    public static Date addDays(Date beforeDate, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beforeDate);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    /**
     * 日期增加N月后得到新的日期
     */
    public static Date addMonth(Date date, int month)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }

    /**
     * 得到2个时间的月份差
     */
    public static Integer getDiffMonth(Date date, Date endDate)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        int yend = endCal.get(Calendar.YEAR);
        int mend = endCal.get(Calendar.MONTH);
        return (yend - y) * 12 + (mend - m);
    }

    /**
     * 返回时间的天数差
     * 
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */
    public static Integer getDaysBetween(Date start, Date end)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        long timeStart = cal.getTimeInMillis();
        cal.setTime(end);
        Long timeEnd = cal.getTimeInMillis();
        Long between_days = (timeEnd - timeStart) / (1000 * 3600 * 24);
        return between_days.intValue();
    }
    
    /**
     * 返回时间的日期天数差
     * 
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */
    public static Integer getDateDaysBetween(Date start, Date end)
    {
    	Date startDate = parseDateToAppointedDate(start, "yyyy-MM-dd");
    	Date endDate = parseDateToAppointedDate(end, "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        long timeStart = cal.getTimeInMillis();
        cal.setTime(endDate);
        Long timeEnd = cal.getTimeInMillis();
        Long between_days = (timeEnd - timeStart) / (1000 * 3600 * 24);
        return between_days.intValue();
    }
    
    /**
     * 将日期转化为指定格式日期
     * @param date
     * @param pattern
     * @return
     */
    public static Date parseDateToAppointedDate(Date date, String pattern)
    {
    	String patternDate = format(date, pattern);
    	return parseDate(patternDate);
    }

    /*
     * 从数据库返回sqlDate得到utilDate
     */
    public static String getUtilDate(Object ts, String pattern)
    {
        if (ts == null)
        {
            return "";
        }
        Date date = new java.util.Date(((java.sql.Timestamp) ts).getTime());
        return DateUtil.format(date, pattern);
    }

    /**
     * 得到指定日期前一天，格式'yyyy-MM-dd'
     * 
     * @return
     */
    public static String getBeforeDate(String day)
    {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try
        {
            calendar.setTime(sf.parse(day));
        }
        catch (ParseException e)
        {
            logger.error(e);
            return null;
        }
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return sf.format(calendar.getTime());
    }

    /**
     * 得到当前日期前几天，格式'yyyy-MM-dd'
     * 
     * @return
     */
    public static String getBeforeDate(int day)
    {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 3);
        return sf.format(calendar.getTime());
    }

    public static String dateTime2Str(Date date)
    {
        if (date != null)
        {
            SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateTimeFormater.format(date);
        }
        else
        {
            return "";
        }
    }

    public static String date2Str(Date date)
    {
        if (date != null)
        {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormater.format(date);
        }
        else
        {
            return "";
        }
    }

    public static boolean isTheSameDay(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return isTheSameDay(cal1, cal2);
    }

    public static boolean isTheSameDay(Calendar cal1, Calendar cal2)
    {
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE))
        {
            return true;
        }
        return false;
    }

    /**
     * 返回 {date} yyyy-MM-dd:00:00:00(精确到毫秒)
     * 
     * @param date
     * @return
     */
    public static Date getFirstTimeOnDay(Date date)
    {
        if (date == null)
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal.getTime();
    }
    
    /**
     * 生成前一个月6号23时59分59秒的日期
     * 
     */
    public static Date getLastDay(Date date){
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH)-1, 6, 23, 59, 59);
        Date m = c.getTime();
        String mon = format.format(m);
		return m;
    }
}
