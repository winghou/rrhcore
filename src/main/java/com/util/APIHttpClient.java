package com.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;


public class APIHttpClient {

	private static final CloseableHttpClient httpClient;
	public static final String CHARSET = "UTF-8";
	static String img="";
	static String imgbag="";
	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

    @SuppressWarnings("deprecation")
    public static String postForJson(String url ,String msg)
    {
    	String content = null;
    	BasicHttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
    	HttpConnectionParams.setSoTimeout(httpParams, 15000);
    	DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
    	
    	HttpPost postMethod = new HttpPost(url);
    	try
    	{
    		System.out.println(msg);
    		// 从接过过来的代码转换为UTF-8的编码
    		HttpEntity stringEntity = new StringEntity(msg, "application/json", "UTF-8");
    		postMethod.setEntity(stringEntity);
    		HttpResponse response = httpClient.execute(postMethod);
    		HttpEntity entity = response.getEntity();
    		
    		if (entity != null)
    		{
    			// 使用EntityUtils的toString方法，传递默认编码，在EntityUtils中的默认编码是ISO-8859-1
    			content = EntityUtils.toString(entity, "UTF-8");
    		}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	finally
    	{
    		postMethod.abort();
    		httpClient.getConnectionManager().closeExpiredConnections();
    		httpClient.getConnectionManager().shutdown();
    		httpClient.close();
    	}
    	return content;
    }
    
    /**
	 * HTTP Post 获取内容
	 * 
	 * @param url 请求的url地址 ?之前的地址
	 * @param json  请求的参数
	 * @param charset 编码格式
	 * @return 页面内容
	 */
	public static String doPost(String url,String json) {
		try {
			HttpPost httpPost = new HttpPost(url);
			StringEntity s = new StringEntity(json.toString(),"utf-8");
			s.setContentEncoding("utf-8");
			s.setContentType("application/json");
			httpPost.setEntity(s);
			CloseableHttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, CHARSET);
			}
			EntityUtils.consume(entity);
			response.close();
//			System.out.println("result=[" + result + "]");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url 请求的url地址 ?之前的地址
	 * @param json  请求的参数
	 * @param charset 编码格式
	 * @return 页面内容
	 */
//	public static String doPost(String url,JSONObject json,String xaccessid,String pemurl) {
//		String result = null;
//		try {
//			HttpPost httpPost = new HttpPost(url);
//			StringEntity s = new StringEntity(json.toString(),"utf-8");
//			s.setContentEncoding("utf-8");
//			s.setContentType("application/json");
//			httpPost.setEntity(s);
//			httpPost.setHeader("content-type", "application/json");
//			httpPost.setHeader("x-access-id", xaccessid);
//			String sign=EncryptionHelper.generateSignature(xaccessid, StringUtil.toString(httpPost.getEntity()), "",pemurl);
//			httpPost.setHeader("x-signature", sign);
//			CloseableHttpResponse response = httpClient.execute(httpPost);
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode != 200) {
//				httpPost.abort();
//				throw new RuntimeException("HttpClient,error status code :" + statusCode);
//			}
//			HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				result = EntityUtils.toString(entity, CHARSET);
//			}
//			EntityUtils.consume(entity);
//			response.close();
//			return result;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}

	  /**
		 * HTTP Post 获取内容
		 * 
		 * @param url 请求的url地址 ?之前的地址
		 * @param json  请求的参数
		 * @param charset 编码格式
		 * @return 页面内容
		 */
		public static String doPost(String url,JSONObject json,int time) {
			CloseableHttpClient clhttpClient;
			RequestConfig reqconfig = RequestConfig.custom().setConnectTimeout(time).setSocketTimeout(time).build();
			clhttpClient = HttpClientBuilder.create().setDefaultRequestConfig(reqconfig).build();
			try {
				HttpPost httpPost = new HttpPost(url);
				StringEntity s = new StringEntity(json.toString(),"utf-8");
				s.setContentEncoding("utf-8");
				s.setContentType("application/json");
				httpPost.setEntity(s);
				CloseableHttpResponse response = clhttpClient.execute(httpPost);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					httpPost.abort();
					throw new RuntimeException("HttpClient,error status code :" + statusCode);
				}
				HttpEntity entity = response.getEntity();
				String result = null;
				if (entity != null) {
					result = EntityUtils.toString(entity, CHARSET);
				}
				EntityUtils.consume(entity);
				response.close();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}


	/**
	 * HTTP Post 获取内容
	 *
	 * @param url 请求的url地址 ?之前的地址
	 * @param json  请求的参数
	 * @param charset 编码格式
	 * @return 页面内容
	 */
	public static String doPost(String url,int time) {
		CloseableHttpClient clhttpClient;
		RequestConfig reqconfig = RequestConfig.custom().setConnectTimeout(time).setSocketTimeout(time).build();
		clhttpClient = HttpClientBuilder.create().setDefaultRequestConfig(reqconfig).build();
		try {
			HttpPost httpPost = new HttpPost(url);
			CloseableHttpResponse response = clhttpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, CHARSET);
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
    public static void main(String[] args) {
//    	String url="http://staging.yitutech.com/face/v1/algorithm/recognition/face_pair_verification";
//    	JSONObject json=new JSONObject();
//    	json.put("database_image_content", img);
//    	json.put("database_image_type", 2);
//    	json.put("query_image_package", imgbag);
//    	json.put("query_image_package_return_image_list", true);
//    	json.put("query_image_package_check_same_person", true);
//    	json.put("auto_rotate_for_database", true);
//    	json.put("true_negative_rate", "99.99");
//    	String xaccessid="13043";
//    	
//    	doPost(url, json, xaccessid, "pemurl");
    }

}
