package com.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FTPUtil
{
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(FTPUtil.class);

    private static final String PATH_SPILIT = "/";
    
    public static String nginxServer = "http://115.28.39.61";
    
    private static String url = "115.28.39.61"; // FTP地址
    
    private static int port = 21; // FTP端口
    
    private static String username = "upload"; // FTP登录名
    
    private static String password = "upload"; // FTP密码
    
    private static final String LINE = "_";
    
    private static final String DOT = ".";
    
    private static final Vector<String> txtFileType = new Vector<String>();
    
    /*
     * 刷新FTP参数
     */
    public static void refreshFtpProperties(String ftpUrl, int ftpPort, String ftpUsername, String ftpPassword,
        String nginxServerUrl)
    {
        url = ftpUrl;
        port = ftpPort;
        username = ftpUsername;
        password = ftpPassword;
        nginxServer = nginxServerUrl;
        txtFileType.add("html");
    }
    
    /*
     * 文件存储目录策略，以天为单位保存
     */
    private static String filePathStrategy()
    {
        return new SimpleDateFormat("yyyyMMdd").format(new Date()) + PATH_SPILIT;
    }
    
    /**
     * Description: 向FTP服务器上传文件
     * 
     * @param filePath FTP服务器保存目录
     * @param fileName 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回HTTP访问路径
     */
    public static String uploadFile(String filePath, String fileName, String fileType, InputStream input)
    {
        String previousDir = filePath;
        if (input == null)
        {
            logger.error("InputStream is null");
            return null;
        }
        if (!StringUtil.isBlank(fileName) && !fileName.contains(DOT))
        {
            logger.error("fileName is invalid!");
            return null;
        }
        
        if (StringUtil.isBlank(fileName))
        {
            String autoFileName = String.valueOf(UUID.generate());
            if (!StringUtil.isBlank(fileType))
            {
                fileName = autoFileName + DOT + fileType;
            }
            else
            {
                // 此处需要从byte中判断文件类型，暂未实现
                fileName = autoFileName;
            }
        }
        
        FTPClient ftpClient = new FTPClient();
        try
        {
            ftpClient.connect(url, port);// 连接FTP服务器
            // 登录
            if (!ftpClient.login(username, password))
            {
                logger.error("fail to login ftp server!");
                return null;
            }
            
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftpClient.disconnect();
                logger.error("fail to get reply code from ftp server!");
                return null;
            }
            ftpClient.enterLocalPassiveMode();// 设置被动模式
            logger.debug("login ftp server sucess!");
            filePath = filePathStrategy();
            String newfilePath = previousDir + (StringUtil.isBlank(previousDir) ? "" : File.separator) + filePath;
            
            // 设置以二进制流的方式传输
            if (txtFileType.contains(fileType))
            {
                ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            }
            else
            {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            }
            
            if (!ftpClient.changeWorkingDirectory(new String(newfilePath.getBytes("GBK"), "iso-8859-1")))
            {
                
                if (!StringUtil.isBlank(previousDir)
                    && !ftpClient.changeWorkingDirectory(new String(previousDir.getBytes("GBK"), "iso-8859-1")))
                {
                    ftpClient.makeDirectory(new String(previousDir.getBytes("GBK"), "iso-8859-1"));
                    if (!ftpClient.changeWorkingDirectory(new String(previousDir.getBytes("GBK"), "iso-8859-1")))
                    {
                        logger.error("fail to change working previous directory on ftp server!");
                        return null;
                    }
                }
                
                ftpClient.makeDirectory(new String(filePath.getBytes("GBK"), "iso-8859-1"));
                if (!ftpClient.changeWorkingDirectory(new String(filePath.getBytes("GBK"), "iso-8859-1")))
                {
                    logger.error("fail to change working directory on ftp server!");
                    return null;
                }
            }
            
            if (!ftpClient.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), input))
            {
                logger.error("fail to store file on ftp server!");
                return null;
            }
            input.close();
            ftpClient.logout();
            return nginxServer + newfilePath + URLEncoder.encode(fileName, "gb2312");
        }
        catch (Exception e)
        {
            logger.error(e);
        }
        finally
        {
            if (ftpClient.isConnected())
            {
                try
                {
                    ftpClient.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return null;
    }
    
    /**
     * 删除文件
     * 
     * @param filePath
     */
    public static int deleteFile(String filePath)
    {
        if (StringUtil.isBlank(filePath))
        {
            logger.error("null or empty parameters!");
            return 1;
        }
        
        FTPClient ftpClient = new FTPClient();
        try
        {
            ftpClient.connect(url, port);// 连接FTP服务器
            // 登录
            if (!ftpClient.login(username, password))
            {
                logger.error("fail to login ftp server!");
                return 1;
            }
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftpClient.disconnect();
                logger.error("fail to get reply code from ftp server!");
                return 1;
            }
            
            if (ftpClient.deleteFile(filePath))
            {
                return 0;
            }
            else
            {
                return 1;
            }
            
        }
        catch (Exception e)
        {
            logger.error(e);
            return 1;
        }
        finally
        {
            if (ftpClient != null)
            {
                try
                {
                    ftpClient.logout();
                }
                catch (IOException ioe)
                {
                    logger.error(ioe);
                }
            }
            
            if (ftpClient.isConnected())
            {
                try
                {
                    ftpClient.disconnect();
                }
                catch (IOException ioe)
                {
                    logger.error(ioe);
                }
            }
        }
    }
    
    /**
     * Description: 从FTP服务器下载文件
     * 
     * @Version1.0 Jul 27, 2008 5:32:36 PM by 崔红保（cuihongbao@d-heaven.com）创建
     * @param url FTP服务器hostname
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName 要下载的文件名
     * @param localPath 下载后保存到本地的路径
     * @return
     */
    public static boolean downFile(String url, int port, String username, String password, String remotePath,
        String fileName, String localPath)
    {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try
        {
            int reply;
            ftp.connect(url, port);
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs)
            {
                if (ff.getName().equals(fileName))
                {
                    File localFile = new File(localPath + PATH_SPILIT + ff.getName());
                    
                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }
            
            ftp.logout();
            success = true;
        }
        catch (IOException e)
        {
            logger.error(e);
        }
        finally
        {
            if (ftp.isConnected())
            {
                try
                {
                    ftp.disconnect();
                }
                catch (IOException ioe)
                {
                }
            }
        }
        return success;
    }
    
    public static void main(String[] args)
    {
        FTPClient ftpClient = new FTPClient();
        try
        {
            ftpClient.connect(url, port);// 连接FTP服务器
            // 登录
            if (!ftpClient.login(username, password))
            {
                logger.error("fail to login ftp server!");
                return;
            }
            
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply))
            {
                ftpClient.disconnect();
                logger.error("fail to get reply code from ftp server!");
                return;
            }
            ftpClient.enterLocalPassiveMode();// 设置被动模式
            logger.debug("login ftp server sucess!");
            
            // 设置以二进制流的方式传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            String filePath = "/upload/bnx/20131016/";
            String previousDir = "/upload/bnx/";
            if (!ftpClient.changeWorkingDirectory(new String(filePath.getBytes("GBK"), "iso-8859-1")))
            {
                if (!ftpClient.changeWorkingDirectory(new String(previousDir.getBytes("GBK"), "iso-8859-1")))
                {
                    boolean dd = ftpClient.makeDirectory(new String(previousDir.getBytes("GBK"), "iso-8859-1"));
                    System.out.println(dd);
                    if (!ftpClient.changeWorkingDirectory(new String(previousDir.getBytes("GBK"), "iso-8859-1")))
                    {
                        logger.error("fail to change working previous directory on ftp server!");
                        return;
                    }
                }
                
                boolean dd1 = ftpClient.makeDirectory(new String(filePath.getBytes("GBK"), "iso-8859-1"));
                System.out.println(dd1);
                if (!ftpClient.changeWorkingDirectory(new String(filePath.getBytes("GBK"), "iso-8859-1")))
                {
                    logger.error("fail to change working directory on ftp server!");
                    return;
                }
            }
        }
        catch (Exception e)
        {
            logger.error(e);
        }
    }
    
}
