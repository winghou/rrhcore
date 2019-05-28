package com.util;

import java.util.HashMap;
import java.util.Map;

/**
 * API错误码类 错误码统一接口 错误码的分布定义如下 成功为0, 错误信息，正常信息，警告信息 存储在一个hashMap中，便于快速的获取错误码对应的消息
 * 
 * @author bill
 */
public class ErrorCode
{
    public final static Map<Integer, String> msg = new HashMap<Integer, String>();
    
    // 成功码
    public final static int SUCCESS = 0x000;
    
    // 顶级错误码，业务不相关
    public final static int FAILED = 0x001; // 通用错误码
    
    public final static int TIMEOUT = 0x002;
    
    public final static int REQUEST_FORMAT_ERROR = 0x003;
    
    public final static int COMMAND_NOT_SUPPORT = 0x004;
    
    public final static int PARAMETER_IS_EMPTY = 0x005;
    
    public final static int LACK_OF_CMD_ELEMENT = 0x006;
    
    public final static int LACK_OF_PARAMS_ELEMENT = 0x007;
    
    public final static int USER_NOT_SIGNIN = 0x008;
    
    public final static int ACCESS_DB_FAILED = 0x009;
    
    //token校验
    public final static int TOKEN_FAILED = -0x001;
    
    // 以上区域的码值不能修改
    
    // 业务逻辑相关错误码
    public final static int NOT_EXISTS_RECORD = 0x00A;
    
    public final static int NO_ACCESS_PRIVILEGE = 0x00B;
    
    public final static int ACCESS_TIMEOUT = 0x00C;
    
    public final static int NO_EXISTS_USER = 0x00E;
    
    public final static int INVALID_ORDER = 0x00F;
    
    public final static int NOT_EXISTS_VEHICLE = 0x010;
    
    public final static int EXISTS_USER = 0x011;
    
    public final static int PASSWORD_IS_WRONG = 0x012;
    
    public final static int BINDED_USER_CANNOTDELETE = 0x013;
    
    public final static int BINDED_VEHICLE_CANNOTDELETE = 0x014;
    
    public final static int ORDER_CANCEL = 0x015;
    
    public final static int USERINFO_INVALID = 0x016;
    
    public final static int USER_NOT_AUDIT = 0x017;
    
    public final static int DEPOSIT_NO = 0x018;
    
    public final static int HAVE_EXISTS_RECORD = 0x019;
    
    public final static int NOT_LOGIN = 0x020;

    public final static String ERROR_NOTE_TEMPLATE = "请求报文中缺少${filedName}参数";
    
    static
    {
        // success message
        msg.put(SUCCESS, "操作成功");
        
        // error message
        msg.put(FAILED, "系统繁忙,稍后重试");
        msg.put(TIMEOUT, "请求超时");// token失效
        msg.put(REQUEST_FORMAT_ERROR, "请求报文JSON格式不正确");
        msg.put(COMMAND_NOT_SUPPORT, "命令不支持");
        msg.put(PARAMETER_IS_EMPTY, "请求的参数不能为空");
        msg.put(LACK_OF_CMD_ELEMENT, "缺少 cmd命令标签");
        msg.put(LACK_OF_PARAMS_ELEMENT, "缺少 params命令标签");
        msg.put(USER_NOT_SIGNIN, "用户未登录");
        msg.put(ACCESS_DB_FAILED, "数据库连接失败");
        
        msg.put(EXISTS_USER, "用户已经存在");
        msg.put(NOT_EXISTS_RECORD, "不存在此记录");
        msg.put(NO_ACCESS_PRIVILEGE, "无访问权限");
        msg.put(ACCESS_TIMEOUT, "访问超时或已在其它地方登录，请重新登录");
        msg.put(NO_EXISTS_USER, "用户不存在");
        msg.put(INVALID_ORDER, "此订单不存在");
        msg.put(PASSWORD_IS_WRONG, "对不起，您的密码有错，请重新输入。");
        
        msg.put(NOT_EXISTS_VEHICLE, "车辆不存在");
        
        msg.put(BINDED_USER_CANNOTDELETE, "人车绑定状态的用户无法被删除！");
        msg.put(BINDED_VEHICLE_CANNOTDELETE, "人车绑定状态的车辆无法被删除！");
        
        msg.put(ORDER_CANCEL, "订单已经关闭！");
        
        msg.put(USERINFO_INVALID, "用户信息已失效，请重新登录！");
        msg.put(USER_NOT_AUDIT, "您还未完善个人信息审核通过");
        msg.put(DEPOSIT_NO, "您还未缴纳足额车辆押金和违章保证金");
        
        msg.put(HAVE_EXISTS_RECORD, "已存在此记录");
        msg.put(NOT_LOGIN, "您还未登录");
    }
    
    public static String msg(int errorCode)
    {
        if (errorCode < 0 || errorCode > 0x1ff)
        {
            return "系统返回异常";
        }
        return msg.get(errorCode);
    }
    
}
