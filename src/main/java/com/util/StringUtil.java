package com.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

/**
 * 字符串处理类
 * 
 * @author bnx
 * 
 */
public class StringUtil {
	
	public static Boolean  isOpen=true; 
	
	/**
	 * 将对象转换成不含空格的字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {
		if (obj == null)
			return "";
		return obj.toString().trim();
	}

	/**
	 * 判断是否为空串或null
	 * 
	 * @return
	 */
	public static boolean isBlank(String string) {
		if (string == null) {
			return true;
		}
		if ("".equals(string.trim())) {
			return true;
		}

		return false;
	}
	
	/**
	 * 返回不能为空的字段
	 * 
	 * @param params
	 * @return
	 */
	public static String isBlank(JSONObject paramsJson,String... params) {
		for (String key : params) {
			if ("".equals(JsonUtil.getJString(paramsJson, key))) {
				return key + " 不能为空";
			}
		}
		return "";
	}
	public static double parseDouble(double f) {
		BigDecimal   b   =   new   BigDecimal(f);  
		double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
		return f1;
	}
	
	public static String countDK(String monthlys,String nhls,int qx){
		double monthly = Double.valueOf(monthlys);
		double nhl = Double.valueOf(nhls);
		double yll = nhl*0.01 / 12;
		double contract_amt = monthly * (Math.pow((1 + yll), qx) - 1) / (yll * (Math.pow((1 + yll), qx)));
		contract_amt = Math.ceil(contract_amt);
		return contract_amt+"";
	}
	
	private static int count = 1 ;
	private static Map<String,String> countMap = new HashMap<String,String>();
	
	public static synchronized String getSeri(String curdate){
		if(null == countMap.get(curdate)){
			count = 1;
			countMap.put(curdate, "0142");
		}else{
			if(count >= 10){
				countMap.put(curdate, "0"+count);
			}else if(count >= 100){
				countMap.put(curdate, "00"+count+"");
			}else if(count >= 1000){
				countMap.put(curdate, count+"");
			}else{
				countMap.put(curdate, "000"+count);
			}
		}
		count++;
		return curdate+countMap.get(curdate);
	}
	
	/**
	 * 随心花合同号生成方式
	 * @param curdate 日期
	 * @param number 数据库自增数字
	 * @return 合同号
	 */
	public static synchronized String getSeri2(String curdate, int number) {
		String result = "";
		if (0 < number && 10 > number) {
			result = curdate + "00000" + number;
		} else if (10 <= number && 100 > number) {
			result = curdate + "0000" + number;
		} else if (100 <= number && 1000 > number) {
			result = curdate + "000" + number;
		} else if (1000 <= number && 10000 > number) {
			result = curdate + "00" + number;
		} else if (10000 <= number && 100000 > number) {
			result = curdate + "0" + number;
		} else if (10000 <= number) {
			result = curdate + number;
		}
		return result;
	}
	
	/** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static String md5Sign(Map<String, Object> sArray,boolean isEncode,String privateKey) {
        return MD5(createLinkStringNoNull(sArray,isEncode,privateKey)).toLowerCase();
    }
    /** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static String md5Sign(Map<String, Object> sArray,boolean isEncode) {
    	return MD5(createLinkString(sArray,isEncode)).toLowerCase();
    }
	 /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @param isEncode 是否要对value进行URLEncoder
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, Object> params,boolean isEncode) {
    	params.remove("attchs");
    	params.remove("cnts");
    	params.remove("ships");
//    	params.remove("param");
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";
        try {
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = nvl(params.get(key));
	            if(isEncode){
	            	if (i == keys.size() - 1) {//鎷兼帴鏃讹紝涓嶅寘鎷渶鍚庝竴涓�&瀛楃
						prestr = prestr + key + "=" + java.net.URLEncoder.encode(value,"utf-8");
	                } else {
	                    prestr = prestr + key + "=" + java.net.URLEncoder.encode(value,"utf-8") + "&";
	                }
	            }else{
	            	if (i == keys.size() - 1) {//鎷兼帴鏃讹紝涓嶅寘鎷渶鍚庝竴涓�&瀛楃
	                    prestr = prestr + key + "=" + value;
	                } else {
	                    prestr = prestr + key + "=" + value + "&";
	                }
	            }
	        }
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        System.out.println("prestr="+prestr);
        return prestr;
    }
    
    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @param isEncode 是否要对value进行URLEncoder
     * @return 拼接后字符串
     */
    public static String createLinkStringNoNull(Map<String, Object> params,boolean isEncode,String privateKey) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";
        try {
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = nvl(params.get(key));
	            /*if("".equals(value)){
	            	continue;
	            }*/
	            if(isEncode){
	            	if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
						prestr = prestr + key + "=" + java.net.URLEncoder.encode(value,"utf-8");
	                } else {
	                    prestr = prestr + key + "=" + java.net.URLEncoder.encode(value,"utf-8") + "&";
	                }
	            }else{
	            	if(value.indexOf("&") > 0 || value.indexOf("@") > 0){
	            		value = java.net.URLEncoder.encode(value,"utf-8");
	            	}
	            	if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
	                    prestr = prestr + key + "=" + value;
	                } else {
	                    prestr = prestr + key + "=" + value + "&";
	                }
	            }
	        }
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        System.out.println(prestr+privateKey);
        return prestr+privateKey;
    }
    
    /** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static String md5Sign1(LinkedHashMap<String, Object> sArray,boolean isEncode) {
        return MD5(createLinkStringNoNull02(sArray,isEncode)).toLowerCase();
    }
    
    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“|”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @param isEncode 是否要对value进行URLEncoder
     * @return 拼接后字符串
     */
    public static String createLinkStringNoNull02(LinkedHashMap<String, Object> params,boolean isEncode) {
    	LinkedList<String> keys = new LinkedList<String>(params.keySet());
        String prestr = "";
        try {
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = nvl(params.get(key));
	            if(isEncode){
	            	if (i == keys.size() - 1) {//拼接时，不包括最后一个|字符
						prestr = prestr + key + "=" + java.net.URLEncoder.encode(value,"utf-8");
	                } else {
	                    prestr = prestr + key + "=" + java.net.URLEncoder.encode(value,"utf-8") + "|";
	                }
	            }else{
	            	if(value.indexOf("|") > 0 || value.indexOf("@") > 0){
	            		value = java.net.URLEncoder.encode(value,"utf-8");
	            	}
	            	if (i == keys.size() - 1) {//拼接时，不包括最后一个|字符
	                    prestr = prestr + key + "=" + value;
	                } else {
	                    prestr = prestr + key + "=" + value + "|";
	                }
	            }
	        }
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        /*System.out.println(prestr);*/
        return prestr;
    }
    
    public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    /**
     * 短期阶段
     * @param borrow_amt  到期还款
     * @param nhls        年化
     * @param days        借款天数
     * @param fwf        服务费
     * @return
     */
    public static String countDQJD(String borrow_amt,String nhls,int days,String fwf){
     double monthly = Double.valueOf(borrow_amt);
     double nhl = Double.valueOf(nhls);
     double yll = nhl*0.01 / 360;
     Double contract_amt = Math.ceil(monthly / (1 + yll*days));
     return contract_amt+"";
    }
    public static String nvl(Object obj) {
		String str = "";
		if (null != obj)
			return obj.toString();
		return str;
	}
    
    public static void main(String[] args) {
    	System.out.println(countDQJD("1045","16",15,""));
	}
    
    /**
     * 保留num位有效数字
     * val 需要格式化的值
     * num 保留的小数位数
     */
    public static String formatNumberToDecimals(String val, int num){
    	String result = "";
    	if(null == val || "".equals(val)){
    		result = "0.00";
    	}else{
    		BigDecimal decimal = new BigDecimal(val);
    		result = decimal.setScale(num, BigDecimal.ROUND_HALF_UP).toString();
    	}
    	return result;
    }
    
    /**
     * 将字符串转换为Double，转换失败返回defaultValue
     */
    public static Double parseDouble(String str, Double defaultValue) {
        if(str == null) return null;
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            
        }
        return defaultValue;
    }
    
    /**
     * 使用java正则表达式去掉多余的.与0
     * @param s
     * @return 
     */
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
    
    /**
     * 抽奖概率
     * @param n 概率（整数）
     * @return 概率
     */
	public static int lotteryProbability(int n) {
		if (n >= 1 && n <= 100) {
			int a = (int) (Math.random() * 100) + 1;
			if (a <= n) {
				return 1;
			}
		}
		return 0;
	}
	
	/**
	 * 获取IP地址
	 * @param request
	 * @return
	 */
	public static String getAddrIp(HttpServletRequest request) {

		String ip = nvl(request.getHeader("X-Real-IP"));
		if (!ip.equals("") && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = nvl(request.getHeader("X-Forwarded-For"));
		if (!ip.equals("") && !"unknown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		} else {
			return "".equals(nvl(request.getHeader("X-Real-IP"))) ? request.getRemoteAddr()
					: nvl(request.getHeader("X-Real-IP"));
		}

	}
	
	/**
	 * 计算String数据的结果
	 * @param value1 String类型值1
	 * @param value2 String类型值2
	 * @param type 计算类型 0:加法,1:减法,2:乘法,3:除法
	 * @return double
	 */
	public static Double getCalculatorResult(String value1, String value2, int type) {
		if(0 == type || 1 == type || 2 == type || 3 == type){
			double result = 0;
			value1 = null == value1 ? "0" : value1;
			value2 = null == value2 ? "0" : value2;
			BigDecimal decimal1 = new BigDecimal(value1);
			BigDecimal decimal2 = new BigDecimal(value2);
			BigDecimal decimalTotal = new BigDecimal("0");
			switch (type) {
			case 0: //加法
				decimalTotal = decimal1.add(decimal2);
				break;
			case 1: //减法
				decimalTotal = decimal1.subtract(decimal2);
				break;
			case 2: //乘法
				decimalTotal = decimal1.multiply(decimal2);
				break;
			case 3: //除法
				if(decimal2.compareTo(new BigDecimal("0")) == 1){
					return null;
				}
				decimalTotal = decimal1.divide(decimal2);
				break;
			}
			result = decimalTotal.doubleValue();
			return result;
		}
		return null;
	}
	
	/**
	 * 计算double数据的结果
	 * @param value1 double类型值1
	 * @param value2 double类型值2
	 * @param type 计算类型 0:加法,1:减法,2:乘法,3:除法
	 * @return double
	 */
	public static Double getCalculatorResult(double value1, double value2, int type) {
		if(0 == type || 1 == type || 2 == type || 3 == type){
			double result = 0;
			BigDecimal decimal1 = new BigDecimal(Double.toString(value1));
			BigDecimal decimal2 = new BigDecimal(Double.toString(value2));
			BigDecimal decimalTotal = new BigDecimal("0");
			switch (type) {
			case 0: //加法
				decimalTotal = decimal1.add(decimal2);
				break;
			case 1: //减法
				decimalTotal = decimal1.subtract(decimal2);
				break;
			case 2: //乘法
				decimalTotal = decimal1.multiply(decimal2);
				break;
			case 3: //除法
				if(decimal2.compareTo(new BigDecimal("0")) == 1){
					return null;
				}
				decimalTotal = decimal1.divide(decimal2);
				break;
			}
			result = decimalTotal.doubleValue();
			return result;
		}
		return null;
	}
	
	/** 
    * @param str要匹配的邮箱
    * @param reg 正则
    */
   public static String mailRegular(String str, String reg) {
    Pattern p=Pattern.compile(reg);  
       Matcher m=p.matcher(str);  
       String mail = "";
       while(m.find()){
           mail = m.group();//打印所有邮箱
           break;
       }
       return mail;
   }
    
   public static String decodeUnicode(String theString) {
	      
       char aChar;

       int len = theString.length();

       StringBuffer outBuffer = new StringBuffer(len);

       for (int x = 0; x < len;) {

           aChar = theString.charAt(x++);

           if (aChar == '\\') {

               aChar = theString.charAt(x++);

               if (aChar == 'u') {

                   // Read the xxxx

                   int value = 0;

                   for (int i = 0; i < 4; i++) {

                       aChar = theString.charAt(x++);

                       switch (aChar) {

                       case '0':

                       case '1':

                       case '2':

                       case '3':

                       case '4':

                       case '5':

                       case '6':
                       case '7':
                       case '8':
                       case '9':
                           value = (value << 4) + aChar - '0';
                           break;
                       case 'a':
                       case 'b':
                       case 'c':
                       case 'd':
                       case 'e':
                       case 'f':
                           value = (value << 4) + 10 + aChar - 'a';
                           break;
                       case 'A':
                       case 'B':
                       case 'C':
                       case 'D':
                       case 'E':
                       case 'F':
                           value = (value << 4) + 10 + aChar - 'A';
                           break;
                       default:
                           throw new IllegalArgumentException(
                                   "Malformed   \\uxxxx   encoding.");
                       }

                   }
                   outBuffer.append((char) value);
               } else {
                   if (aChar == 't')
                       aChar = '\t';
                   else if (aChar == 'r')
                       aChar = '\r';

                   else if (aChar == 'n')

                       aChar = '\n';

                   else if (aChar == 'f')

                       aChar = '\f';

                   outBuffer.append(aChar);

               }

           } else

               outBuffer.append(aChar);

       }

       return outBuffer.toString();

   }
   
   /**       
    * 描述:获取 post 请求的 byte[] 数组 
    * <pre> 
    * 举例： 
    * </pre> 
    * @param request 
    * @return 
    * @throws IOException       
    */  
   private static byte[] getRequestPostBytes(HttpServletRequest request)  
           throws IOException {  
       int contentLength = request.getContentLength();  
       if(contentLength<0){  
           return null;  
       }  
       byte buffer[] = new byte[contentLength];  
       for (int i = 0; i < contentLength;) {  
 
           int readlen = request.getInputStream().read(buffer, i,  
                   contentLength - i);  
           if (readlen == -1) {  
               break;  
           }  
           i += readlen;  
       }  
       return buffer;  
   }  
     
   /**       
    * 描述:获取 post 请求内容 
    * <pre> 
    * 举例： 
    * </pre> 
    * @param request 
    * @return 
    * @throws IOException       
    */  
   public static String getRequestPostStr(HttpServletRequest request)  
           throws IOException {  
       byte buffer[] = getRequestPostBytes(request);  
       String charEncoding = request.getCharacterEncoding();  
       if (charEncoding == null) {  
           charEncoding = "UTF-8";  
       }  
       return new String(buffer, charEncoding);  
   }
   
}
