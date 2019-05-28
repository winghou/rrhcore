package com.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.config.CustomConsts;
import com.dao.CorpDeptMapper;
import com.frame.Consts;
import com.model.CorpDept;

public class Utils
{
    private static final Logger logger = Logger.getLogger(Utils.class);

    static DecimalFormat mileDF = new DecimalFormat("#0.00");
    
    /**
     * 判断字符串是否为null或是blank
     * 
     * @param content
     * @return 返回是否为上面二者的情况，属实返回true,否则返回false
     */
    public static boolean isBnull(String content)
    {
        if (content != null && !"".equals(content.trim()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * 判断字符串是否为null或是blank,如果为空则返回空字符串
     * 
     * @param content
     * @return 返回是否为上面二者的情况，属实返回true,否则返回false
     */
    public static String getStr(String content)
    {
        return Utils.isBnull(content) ? "" : content;
    }
    
    /**
     * 判断字符串是否为null或是blank,如果为空则返回空字符串
     * 
     * @param content
     * @return 返回是否为上面二者的情况，属实返回true,否则返回false
     */
    public static String getStr(Object content, String defaultStr)
    {
        return content == null ? defaultStr : String.valueOf(content);
    }
    
    /**
     * 判断字符串是否为null或是blank,如果为空则返回空字符串
     * 
     * @param content
     * @return 返回是否为上面二者的情况，属实返回true,否则返回false
     */
    public static int getInt(Object content, int defaultStr)
    {
        return content == null ? defaultStr : Integer.parseInt(String.valueOf(content));
    }
    
    /**
     * 判断字符串是否为null或是blank,如果为空则返回空字符串
     * 
     * @param content
     * @return 返回是否为上面二者的情况，属实返回true,否则返回false
     */
    public static double getDouble(Object content, double defaultStr)
    {
        return content == null ? defaultStr : Double.parseDouble(String.valueOf(content));
    }
    
    /**
     * 判断整型变量是否为null或是blank
     * 
     * @param content
     * @return 返回是否为上面二者的情况，属实返回true,否则返回false
     */
    public static boolean isBnull(Integer content)
    {
        if (content != null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * 得到当前日期，格式'yyyy-MM-dd'
     * 
     * @author bnx
     * @since 2012-04-11
     * @return
     */
    public static String getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(calendar.getTime());
    }
    
    /**
     * 得到当前日期，格式'yyyy-MM-dd'
     * 
     * @author bnx
     * @since 2012-04-11
     * @return
     */
    public static Date getCurrentDateDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            return sf.parse(sf.format(calendar.getTime()));
        }
        catch (ParseException e)
        {
            logger.error("error", e);
        }
        return null;
    }
    
    /**
     * 得到当前日期，格式'yyyy-MM-dd HH:mm:ss'
     * 
     * @author bnx
     * @since 2012-04-11
     * @return
     */
    public static Date getCurrentTimeDate()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            return sf.parse(sf.format(calendar.getTime()));
        }
        catch (ParseException e)
        {
            logger.error("error", e);
        }
        return null;
    }
    
    /**
     * 得到当前日期，格式'yyyy-MM-dd HH:mm:ss'
     * 
     * @author bnx
     * @since 2012-04-11
     * @return
     */
    public static String getCurrentDateTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(calendar.getTime());
    }
    
    public static Date str2DateTime(String str)
    {
        if (str != null && !str.equals(""))
        {
            try
            {
                SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = dateTimeFormater.parse(str);
                return d;
                // SimpleDateFormat sf = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss",Locale.ENGLISH);
                // Date d = sf.parse(str);
                // return d;
            }
            catch (ParseException e)
            {
                logger.error("error", e);
                return new Date();
            }
        }
        else
        {
            return null;
        }
    }
    
    public static Date str2Date(String str)
    {
        if (str != null && !str.equals(""))
        {
            try
            {
                SimpleDateFormat dateTimeFormater = new SimpleDateFormat("yyyy-MM-dd");
                Date d = dateTimeFormater.parse(str);
                return d;
            }
            catch (ParseException e)
            {
                logger.error("error", e);
                return new Date();
            }
        }
        else
        {
            return null;
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
    
    /**
     * 将对象转换成不含空格的字符串
     * 
     * @author bnx
     * @since 2012-04-11
     * @since 2012-03-16
     * @param obj
     * @return
     */
    public static String convertToString(Object obj)
    {
        if (obj == null)
            return "";
        return obj.toString().trim();
    }
    
    
    
    
    /**
     * 生成随机字符串. <br>
     * 随机字符串的内容包含[0-9,a-z]的字符. <br>
     * 
     * @param randomLength 随机字符串的长度
     * @return 随机字符串.
     */
    public static String randomChars(int randomLength)
    {
        char[] randoms = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random random = new Random();
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < randomLength; i++)
        {
            ret.append(randoms[random.nextInt(randoms.length)]);
        }
        random = null;
        return ret.toString();
    }
    
    /**
     * 根据两点的经纬度求两点间距离（地球半径设为6371300米）
     * 
     * @param lng1
     * @param lat1
     * @param lng2
     * @param lat2
     * @return
     */
    private static final int EARTH_RADIUS = 6378137;
    
    public static double distanceOfTwoPosition(double lng1, double lat1, double lng2, double lat2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s =
            2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }
    
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    
    /**
     * 使用java正则表达式去掉多余的.与0
     * 
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s)
    {
        if (s.indexOf(".") > 0)
        {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }
    
    /**
     * 格式化经纬度
     * 
     * @param ltude 经度或是纬度
     * @return 格式化后的格式六位小数长度
     */
    public static double rglrTude(Double ltude)
    {
        if (ltude == null)
        {
            return 0;
        }
        DecimalFormat df = new DecimalFormat("0.000000");
        return Double.valueOf(df.format(ltude.doubleValue()));
    }
    
    
    /**
     * 取优先级高的救援类型： 比较救援类型，按拖车【s005拖车牵引（地面）、s006拖车牵引（地库）、s007架小轮拖车、s008紧急脱困】 优先于按事件计费【s001
     * 接电服务、s002紧急送油、s003紧急加水、s004更换轮胎 】 拖车或按事件计费同时多个取第一个。
     * 
     * @return
     */
    public static String getPriorityRescureTypeId(String serviceTypeIds)
    {
        if (StringUtil.isBlank(serviceTypeIds))
        {
            return "";
        }
        String[] idArray = serviceTypeIds.replace("，", ",").split(",");
        for (int j = 0; j < idArray.length; j++)
        {
            String id = idArray[j];
            if (id.contains("s005") || id.contains("s006") || id.contains("s007") || id.contains("s008"))
            {
                return id;
            }
        }
        return idArray[0];
    }
    
    /**
     * 是否拖车类型
     * 
     * @param serviceTypeId
     * @return
     */
    public static Boolean isTrailCar(String serviceTypeId)
    {
        if ("s005".equals(serviceTypeId) || "s006".equals(serviceTypeId) || "s007".equals(serviceTypeId)
            || "s008".equals(serviceTypeId))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 将秒转成分钟单位
     * 
     * @param seconds
     * @return
     */
    public static String formateSeconds(int seconds)
    {
        int minutes = seconds / 60;
        int leftSeconds = seconds % 60;
        return minutes + ":" + leftSeconds;
    }
    
    
    public static JSONArray treeMenuList(JSONArray menuList, int parentId)
    {
        JSONArray childMenu = new JSONArray();
        
        for (int i = 0; i < menuList.size(); i++)
        {
            JSONObject jsonMenu = menuList.getJSONObject(i);
            
            int menuId = jsonMenu.getIntValue("id");
            int pid = jsonMenu.getIntValue("parentId");
            if (parentId == pid)
            {
                JSONArray c_node = treeMenuList(menuList, menuId);
                if (c_node.size() > 0)
                {
                    jsonMenu.put("childNode", c_node);
                }
                childMenu.add(jsonMenu);
            }
        }
        
        return childMenu;
    }
    
    public static JSONArray treeDeptList(JSONArray allDeptList, String parentId)
    {
        JSONArray childDept = new JSONArray();
        
        for (int i = 0; i < allDeptList.size(); i++)
        {
            JSONObject jsonDept = allDeptList.getJSONObject(i);
            
            String id = jsonDept.getString("id");
            String pid = jsonDept.getString("parentId");
            if (parentId.equals(pid))
            {
                JSONArray c_node = treeDeptList(allDeptList, id);
                if (c_node.size() > 0)
                {
                    jsonDept.put("children", c_node);
                }
                
                childDept.add(jsonDept);
            }
        }
        
        return childDept;
    }
    
    public static void main(String[] args)
        throws UnsupportedEncodingException
    {
    	System.out.println(getDuration(8715));
    }
    
    /**
     * 校验是否是指定权限角色 企业管理员：corpAdmin 部门管理员：deptAdmin 客服：customServer 财务人员：accountPerson 司机：driver
     * 
     * @param uriList
     * @param validRight
     * @return
     */
    // public static boolean isValidRight(List<UserRightInfo> uriList, String
    // validRight)
    // {
    // if (Utils.isBnull(validRight))
    // {
    // return false;
    // }
    //
    // boolean isRight = false;
    // for (UserRightInfo uri : uriList)
    // {
    // if (uri.getRightType() == 1 && validRight.equals(uri.getUserRightId()))
    // {
    // isRight = true;
    // }
    // }
    //
    // return isRight;
    // }
    
    /**
     * 取得给定汉字串的首字母串,即声母串 Title: ChineseCharToEn 注：只支持GB2312字符集中的汉字
     */
    private final static int[] li_SecPosValue = {1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212,
        3472, 3635, 3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590};
    
    private final static String[] lc_FirstLetter = {"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n",
        "o", "p", "q", "r", "s", "t", "w", "x", "y", "z"};
    
    /**
     * 取得给定汉字的首字母,即声母
     * 
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese)
    {
        if (chinese == null || chinese.trim().length() == 0)
        {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");
        if (chinese.length() > 1) // 判断是不是汉字
        {
            int li_SectorCode = (int)chinese.charAt(0); // 汉字区码
            int li_PositionCode = (int)chinese.charAt(1); // 汉字位码
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590)
            {
                for (int i = 0; i < 23; i++)
                {
                    if (li_SecPosCode >= li_SecPosValue[i] && li_SecPosCode < li_SecPosValue[i + 1])
                    {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            }
            else
            // 非汉字字符,如图形符号或ASCII码
            {
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }
        return chinese;
    }
    
    /**
     * 字符串编码转换
     * 
     * @param str 要转换编码的字符串
     * @param charsetName 原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName)
    {
        try
        {
            str = new String(str.getBytes(charsetName), toCharsetName);
        }
        catch (UnsupportedEncodingException ex)
        {
            System.out.println("字符串编码转换异常：" + ex.getMessage());
        }
        return str;
    }
    
    /**
     * 格式化经纬度
     */
    static DecimalFormat df = new DecimalFormat("#0.000000");
    
    public static String formatLonAndLat(BigDecimal bd)
    {
        if (bd == null)
        {
            return "0.0";
        }
        return df.format(bd.doubleValue());
    }
    
    
    /**
     * 检查传入的字符串，生成字符串的查询条件 处理可能的' '等
     * 
     * @return
     */
    private static String getSQLStringQueryCondition(String s)
    {
        // s为 查询条件的一部分，如 asd 1234d 'aaaa'
        // 注意可能本身前后包含'' 需要进行处理
        if (s == null)
            s = "";
        s = s.trim();
        boolean addQm = false;// 需要添加引号
        int qmc = 0;// 引号数目
        // 如果s以奇数个数的'开始 就认为已经加上了引号，不需要重复添加引号
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == '\'')
            {
                qmc++;
            }
            else
            {
                break;
            }
        }
        if (qmc % 2 == 0)
        {
            addQm = true;
        }
        else
        {
            addQm = false;
        }
        if (addQm)
        {
            s = "'" + s + "'";
        }
        return s;
    }
    
    /**
     * 将List<String>转化为in的查询条件形如：（'a','b','c'）
     */
    public static String ListToStr(List<String> list)
    {
        StringBuffer str = new StringBuffer("(");
        for (int i = 0; i < list.size(); i++)
        {
            String s = list.get(i);
            s = getSQLStringQueryCondition(s);
            if (i == 0)
            {
                str.append(s);
            }
            else
            {
                str.append(",").append(s);
            }
        }
        if ("(".equals(str.toString()))
            str.append("' ')");
        else
            str.append(")");
        return str.toString();
    }
    
    public static String[] ListToArray(List<String> list)
    {
        
        String[] ids = new String[list.size()];
        
        for (int i = 0; i < list.size(); i++)
        {
            
            ids[i] = list.get(i);
            
        }
        
        return ids;
        
    }
    
    
    
    public static int vehicleStatus(Integer online, Integer move, Integer location, Integer alarm)
    {
        
        // int online = devicePositionInfo.getOnlineStatus();
        // int move = devicePositionInfo.getTravelStatus();
        // int location = devicePositionInfo.getGpsAccuracy();
        // int alarm = devicePositionInfo.getAlarmType();

        if (online == null || online.intValue() != 1)
        {
            online = 0;
        }
        
        if (move == null)
        {
            move = 0;
        }
        
        if (location == null)
        {
            location = 0;
        }
        
        if (alarm == null || alarm.intValue() == 0)
        {
            alarm = 0;
        }else {
        	alarm = 1;
		}
        
        // 1车辆在线行驶不报警
        if (online.intValue() == 1 && move.intValue() == 1 && alarm.intValue() == 0)
        {
            return 1;
        }
        
        // 2车辆在线停车不报警
        if (online.intValue() == 1 && move.intValue() == 0 && alarm.intValue() == 0)
        {
            return 2;
        }
        
        // 3车辆在线不定位不报警
        if (online.intValue() == 1 && location.intValue() == 0 && alarm.intValue() == 0)
        {
            return 3;
        }
        
        // 4车辆在线行驶报警
        if (online.intValue() == 1 && move.intValue() == 1 && alarm.intValue() == 1)
        {
            return 4;
        }
        
        // 5车辆在线停车报警
        if (online.intValue() == 1 && move.intValue() == 0 && alarm.intValue() == 1)
        {
            return 5;
        }
        
        // 6 车辆在线不定位报警
        if (online.intValue() == 1 && location.intValue() == 0 && alarm.intValue() == 1)
        {
            return 6;
        }
        
        // 7 车辆不在线行驶
        if (online.intValue() == 0 && move.intValue() == 1)
        {
            return 7;
        }
        
        // 8车辆不在线停车
        if (online.intValue() == 0 && move.intValue() == 0)
        {
            return 8;
        }
        
        // 9 车辆不在线不定位
        if (online.intValue() == 0 && location.intValue() == 0)
        {
            return 9;
        }

        return 9;
        
    }

    /**
     * 格式化位置km 数据库单位是m
     * 
     */
    public static String formatMile(Object obj)
    {
        if (obj == null)
        {
            return "";
        }
        Integer bg = (Integer)obj;
        return mileDF.format(bg.doubleValue() / 1000.0);
    }
    
    /**
     * 格式化累计工作时长,返回xx时xx分
     * 
     */
    public static String formatSumWorkTime(Object obj)
    {
        if (obj == null)
        {
            return "";
        }
        Integer bg = (Integer)obj;
        return getDuration(bg);
    }

	public static String getDuration(int durationSeconds){
		int hours = durationSeconds /(60*60);
		int leftSeconds = durationSeconds % (60*60);
		int minutes = leftSeconds / 60;
		int seconds = leftSeconds % 60;
		
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(addZeroPrefix(hours));
		sBuffer.append(":");
		sBuffer.append(addZeroPrefix(minutes));
		sBuffer.append(":");
		sBuffer.append(addZeroPrefix(seconds));
		
		return sBuffer.toString();
	}
	
	public static String addZeroPrefix(int number){
		if(number < 10){
			return "0"+number;
		}else{
			return ""+number;
		}

	}
}
