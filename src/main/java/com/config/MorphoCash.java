package com.config;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import com.alibaba.fastjson.JSONObject;
import com.util.JsonUtil;

/**
* @ClassName: Morpho
* @Description: 闪蝶平台参数和工具类
* @author zhaheng
* @date 2017年9月29日 下午5:47:46
*
*/ 
/**
* @ClassName: MorphoCash
* @Description: 接口文档版本：闪蝶平台数据查询接口文档_V1.0.6   闪蝶平台数据上报接口文档_V1.0.12
* @author zhaheng
* @date 2017年10月10日 上午11:48:51
*
*/ 
public class MorphoCash {

	/**
	 * 测试账号、正式帐号
	 */
	public static String auth="jun_duan@yuecaijf.com:Yc1234567890?";
	
	/**
	 * 测试环境  闪蝶
	 */
	public static final String morphoTestUrl="https://test2.intellicredit.cn/riskcontrol/api/v1/applicant/risk?source=morpho";
	
	/**
	 * 贷款批核上报  测试环境
	 */
	public static final String CashTestUrl= "https://test2.intellicredit.cn/riskcontrol/api/v1/loan/issue";
	
	
	/**
	 * 借贷人账户状态变动更新  测试环境
	 */
	public static final String CashChangeTestUrl= "https://test2.intellicredit.cn/riskcontrol/api/v1/loan/track";
	
	
	/**
	 * 生产环境  闪蝶
	 */
	public static final String morphoUrl="https://riskcontrol.intellicredit.cn/api/v1/applicant/risk?source=morpho";
	
	/**
	 * 生产环境  贷款批核上报
	 */
	public static final String CashUrl="https://riskcontrol.intellicredit.cn/api/v1/loan/issue";
	
	/**
	 * 生产环境  借贷人账户状态变动更新
	 */
	public static final String CashChangeUrl="https://riskcontrol.intellicredit.cn/api/v1/loan/track";
	
	
	public static String toDateYMD(Date date) {
		if(date!=null) {
			return DateFormatUtils.format(date, "yyyy-MM-dd");
		}else {
			return "";
		}
	}
	
	
	public static String toDateTime(Date date) {
		if(date!=null) {
			return DateFormatUtils.format(date, "yyyy-MM-dd'T'HH:mm:ss");
		}else {
			return "";
		}
	}
	
	

	
	
	/**
	* @Title: getOverDays
	* @Description: 逾期状态标签
	    DX 逾期X天 D5表示逾期5天
		M1 逾期1－30天
		M2 逾期31-60天
		M3 逾期61-90天
		M4 逾期91-120天
		M5 逾期121-150天
		M6 逾期151-180天
		MN 逾期181天以上（含)
	* @param days
	* @return
	* @return String
	* @throws
	*/ 
	public static String getOverDays(Integer days) {
		if(days>0) {
		/*	if(days<10) {
				return "D"+days;
			}*/
			if(days<=30){
				return "M1";
			}
			if(days>=31 && days<=60) {
				return "M2";
			}
			if(days>=61 && days<=90) {
				return "M3";
			}
			if(days>=91 && days<=120) {
				return "M4";
			}
			if(days>=121 && days<=150) {
				return "M5";
			} 
			if(days>=151 && days<=180) {
				return "M6";
			} 
			return "MN";
		}else {
			return "";
		}
	}
	
	
}
