package com.util;


import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class URLInvoke
{
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(URLInvoke.class);


    @SuppressWarnings("deprecation")
    public static String post(String url, String msg)
    {
        String content = null;
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 15000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        HttpPost postMethod = new HttpPost(url);

        try
        {
            // 从接过过来的代码转换为UTF-8的编码
            HttpEntity stringEntity = new StringEntity(msg, "text/plain", "UTF-8");
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
            logger.error("invoke [" + url + "] failed.");
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
    
    @SuppressWarnings("deprecation")
    public static String postForJson(String url, String msg)
    {
        String content = null;
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 15000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        
        HttpPost postMethod = new HttpPost(url);
        logger.info("request " + url + " >>" + msg);
        try
        {
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
            logger.error("invoke [" + url + "] failed.");
        }
        finally
        {
            postMethod.abort();
            httpClient.getConnectionManager().closeExpiredConnections();
            httpClient.getConnectionManager().shutdown();
            httpClient.close();
        }
        logger.info("response " + url + " <<" + content);
        return content;
    }

    @SuppressWarnings("deprecation")
    public static String get(HttpServletRequest request, String url)
    {
        String content = null;
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        HttpGet getMethod = new HttpGet(url);

        try
        {
            HttpResponse response = httpClient.execute(getMethod);
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
            logger.error("invoke [" + url + "] failed.");
        }
        finally
        {
            getMethod.abort();
            httpClient.getConnectionManager().shutdown();
            httpClient.close();
        }
        return content;
    }

    @SuppressWarnings("deprecation")
    public static String postxml(String url, String msg)
    {
        String content = null;
        BasicHttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 15000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpPost postMethod = new HttpPost(url);
        postMethod.addHeader("Content-Type", "text/xml;charset=UTF-8");
        try
        {
            // 从接过过来的代码转换为UTF-8的编码
            HttpEntity stringEntity = new StringEntity(msg, "text/xml", "UTF-8");
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
            logger.error("invoke [" + url + "] failed.");
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

}
