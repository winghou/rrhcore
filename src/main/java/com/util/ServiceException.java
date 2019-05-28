package com.util;

/**
 * 业务层异常基类
 * 
 * @author meiguiyang
 * @version [版本号, Dec 29, 2012]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ServiceException extends RuntimeException
{
    private static final long serialVersionUID = -1050624789625303799L;

    /**
     * 异常码
     */
    private String errorCode = null;

    /**
     * <默认构造函数>
     */
    public ServiceException()
    {
        super();
    }

    /**
     * 构造函数
     * 
     * @param message 错误信息
     */
    public ServiceException(String errorCode)
    {
        super(errorCode);
        setErrorCode(errorCode);
    }

    /**
     * 构造函数
     * 
     * @param message 错误信息
     * @param errorCode 错误码
     */
    public ServiceException(String message, String errorCode)
    {
        super(message);
        setErrorCode(errorCode);
    }

    /**
     * 构造函数
     * 
     * @param t Throwable
     */
    public ServiceException(Throwable t)
    {
        super(t);
    }

    /**
     * 构造函数
     * 
     * @param t Throwable
     * @param errorCode 错误码
     */
    public ServiceException(Throwable t, String errorCode)
    {
        super(t);
        setErrorCode(errorCode);
    }

    /**
     * 构造函数
     * 
     * @param message 错误
     * @param t Throwable
     */
    public ServiceException(String message, Throwable t)
    {
        super(message, t);
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }
}
