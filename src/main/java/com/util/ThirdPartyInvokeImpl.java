package com.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.util.LocationConvert.GeoPoint;

public class ThirdPartyInvokeImpl
{

    private static final Logger logger = Logger.getLogger(ThirdPartyInvokeImpl.class);

    //反向地址解析URL
	private static String addressURL = "http://api.map.baidu.com/geocoder?location=${lat,lon}&output=json";


	/**
	 * 根据经纬度反地址解析出位置
	 * 
	 * @param params
	 * @param request
	 * @return
	 */
	public static String getAddress(GeoPoint correctGp, HttpServletRequest request)
	{
//		JSONObject detail = new JSONObject();
		double longitude = correctGp.lng;
		double latitude = correctGp.lat;
		String formatted_address = "";
		try
		{
			String newAddressURL = "";
			
			if (longitude > 0.00 && latitude > 0.00)
			{
				newAddressURL = addressURL.replace("${lat,lon}", latitude + "," + longitude);
				String resultMsg = URLInvoke.get(request, newAddressURL);
				JSONObject jo = JSONObject.parseObject(resultMsg);
				JSONObject result = jo.getJSONObject("result");
				formatted_address = JsonUtil.getJString(result, "formatted_address");
//				detail.put("address", formatted_address);
			}
		}
		catch (Exception e)
		{
			logger.error("error", e);
			logger.error("getAddress failed:" + e.getMessage());
//			detail.put(Consts.RESULT, ErrorCode.FAILED);
//			return detail;
		}

//		detail.put(Consts.RESULT, ErrorCode.SUCCESS);
		return formatted_address;

	}
	
	public static void main(String[] args) {
		JSONObject params =new JSONObject();
		
		GeoPoint gp = new GeoPoint(32.082749, 118.765647);
//		JSONObject detail = new JSONObject();
		String address = getAddress(gp,null);
		System.out.print(address);
	}


}
