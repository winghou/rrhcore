package com.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * 融360身份证信息下载保存
 * （写好备用，其他方法请勿调用）
 * @author songchenwei
 *
 */
public class RongDownLoadIdCard {
	private static final Logger logger =Logger.getLogger(RongDownLoadIdCard.class);
	/**
	 * 
	 * @param url 下载地址
 	 * @param filePath 文件保存路径
	 * @param orderNo  用户订单号
	 * @return
	 * @throws Exception
	 */
	public static String downloadIdCard(String url,String filePath,String orderNo) {
		
		try {
			//new一个URL对象  
			URL idCardUrl = new URL(url);  
			//打开链接  
			HttpURLConnection conn = (HttpURLConnection)idCardUrl.openConnection();  
			//设置请求方式为"GET"  
			conn.setRequestMethod("GET");  
			//超时响应时间为5秒  
			conn.setConnectTimeout(5 * 1000);  
			//通过输入流获取图片数据  
			InputStream inStream = conn.getInputStream();  
			//得到图片的二进制数据，以二进制封装得到数据，具有通用性  
			byte[] data = readInputStream(inStream);  
			//new一个文件对象用来保存图片，默认保存当前工程根目录  
			File imageFile=new File(filePath);
			File imageFileName = File.createTempFile(null, null);  
			//创建输出流  
			FileOutputStream outStream = new FileOutputStream(imageFileName);  
			//写入数据  
			outStream.write(data);  
			//关闭输出流  
			outStream.close();  
			System.out.println(imageFile.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return filePath;
	}  
		
	

public static byte[] readInputStream(InputStream inStream) throws Exception{  
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
    //创建一个Buffer字符串  
    byte[] buffer = new byte[1024];  
    //每次读取的字符串长度，如果为-1，代表全部读取完毕  
    int len = 0;  
    //使用一个输入流从buffer里把数据读取出来  
    while( (len=inStream.read(buffer)) != -1 ){  
        //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
        outStream.write(buffer, 0, len);  
    }  
    //关闭输入流  
    inStream.close();  
    //把outStream里的数据写入内存  
    return outStream.toByteArray();  
}  
}  

