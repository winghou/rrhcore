package com.util;

public class GetInfoFromIDUtil {

	/**
	 * 从身份证获取出生年份 15位：310108800902023 18位：310108198009020231
	 * 
	 * @author Administrator
	 *
	 */
	public static String getYear(String identityNo) {
		if ((15 != identityNo.length() && 18 != identityNo.length()) || -1 < identityNo.indexOf(" ") || "".equals(identityNo)) {
			return "身份证格式有误";
		}
		String str = "";
		if (15 == identityNo.length()) {
			str = "19" + identityNo.substring(6, 8);
		} else if (18 == identityNo.length()) {
			str = identityNo.substring(6, 10);
		}
		return str;
	}

	/**
	 * 
	 * @param identityNo
	 * @return 性别
	 */
	public static String getSex(String identityNo) {
		if ((15 != identityNo.length() && 18 != identityNo.length()) || -1 < identityNo.indexOf(" ")) {
			return "身份证格式有误";
		}
		String str = "";
		if (15 == identityNo.length()) {
			str = identityNo.substring(14, 15);
		} else if (18 == identityNo.length()) {
			str = identityNo.substring(16, 17);
		}
		String sex = "";
		int n = Integer.parseInt(str);
		if(n%2 == 0){
			sex = "女";
		}else{
			sex = "男";
		}
		return sex;
	}

	/**
	 * 根据身份证获取省份信息
	 * 
	 * @param identityNo
	 * @return 省份
	 */
	public static String getProvince(String identityNo) {
		if ((15 != identityNo.length() && 18 != identityNo.length()) || -1 < identityNo.indexOf(" ")) {
			return "身份证格式有误";
		}
		String str = identityNo.substring(0, 2);
		String province = "";
		if ("11".equals(str)) {
			province = "北京";
		} else if ("12".equals(str)) {
			province = "天津";
		} else if ("13".equals(str)) {
			province = "河北";
		} else if ("14".equals(str)) {
			province = "山西";
		} else if ("15".equals(str)) {
			province = "内蒙古";
		} else if ("21".equals(str)) {
			province = "辽宁";
		} else if ("22".equals(str)) {
			province = "吉林";
		} else if ("23".equals(str)) {
			province = "黑龙江";
		} else if ("31".equals(str)) {
			province = "上海";
		} else if ("32".equals(str)) {
			province = "江苏";
		} else if ("33".equals(str)) {
			province = "浙江";
		} else if ("34".equals(str)) {
			province = "安徽";
		} else if ("35".equals(str)) {
			province = "福建";
		} else if ("36".equals(str)) {
			province = "江西";
		} else if ("37".equals(str)) {
			province = "山东";
		} else if ("41".equals(str)) {
			province = "河南";
		} else if ("42".equals(str)) {
			province = "湖北";
		} else if ("43".equals(str)) {
			province = "湖南";
		} else if ("44".equals(str)) {
			province = "广东";
		} else if ("45".equals(str)) {
			province = "广西";
		} else if ("46".equals(str)) {
			province = "海南";
		} else if ("50".equals(str)) {
			province = "重庆";
		} else if ("51".equals(str)) {
			province = "四川";
		} else if ("52".equals(str)) {
			province = "贵州";
		} else if ("53".equals(str)) {
			province = "云南";
		} else if ("54".equals(str)) {
			province = "西藏";
		} else if ("61".equals(str)) {
			province = "陕西";
		} else if ("62".equals(str)) {
			province = "甘肃";
		} else if ("63".equals(str)) {
			province = "青海";
		} else if ("64".equals(str)) {
			province = "宁夏";
		} else if ("65".equals(str)) {
			province = "新疆";
		} else if ("71".equals(str)) {
			province = "台湾";
		} else if ("81".equals(str)) {
			province = "香港";
		} else if ("82".equals(str)) {
			province = "澳门";
		} else if ("91".equals(str)) {
			province = "国外";
		}
		return province;
	}

	public static void main(String[] args) {
		//System.out.println(getYear("341181199303194417"));
		System.out.println(getSex("341181199303194417"));
	}
}
