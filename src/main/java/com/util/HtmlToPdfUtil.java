package com.util;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
public class HtmlToPdfUtil {

	//wkhtmltopdf在系统中的路径  "c:\\Program Files\\wkhtmltopdf\\bin\\wkhtmltopdf.exe";  
    public static void convertToPdf(String htmlName , String pdfName){  
    	
        File file = new File(pdfName);  
        File parent = file.getParentFile();  
        //如果pdf保存路径不存在，则创建路径  
        if(!parent.exists()){  
            parent.mkdirs();  
        }  
        // 判断当前操作系统是不是window
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        String comm = "";
        if (os.toUpperCase().indexOf("WINDOWS")>=0) 
        {
        	comm =  "D:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe " + htmlName + " " + pdfName;  
        }else{
        	//Linux(Landscape  横向导出  ； Portrait 纵向导出 -O Portrait --margin-top 3 --margin-bottom 3 )
        	comm = " /usr/local/bin/wkhtmltopdf " + htmlName + " " + pdfName;  
        }
        try {
			Runtime.getRuntime().exec(comm);
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }      
	
	//wkhtmltopdf在系统中的路径  "C:\Users\Administrator\Downloads\wkhtmltox-0.13.0-alpha-7b36694_msvc2013-win64\bin\wkhtmltoimage.exe";  
   public static String convertToPic(String htmlName , String picName){  
    	
        File file = new File(picName);  
        File parent = file.getParentFile();  
        //如果pdf保存路径不存在，则创建路径  
        if(!parent.exists()){  
            parent.mkdirs();  
        }  
        // 判断当前操作系统是不是window
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (os.toUpperCase().indexOf("WINDOWS")>=0) 
        {
        	 return "D:/Program Files/wkhtmltopdf/bin/wkhtmltoimage.exe " + htmlName + " " + picName;  
        }else{
        	//Linux
        	String comm = " /usr/local/bin/wkhtmltoimage -O Landscape --margin-top 3 --margin-bottom 3 " + htmlName + " " + picName;  
            System.out.println("linux comm : " + comm);
            return comm;
        }
    }
	
}
