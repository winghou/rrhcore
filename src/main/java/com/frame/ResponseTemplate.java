package com.frame;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.util.ErrorCode;

/**
 * 返回结构模板类，规范返回的格式
 * 
 * @author bill
 */
public class ResponseTemplate
{
    
    public String command = null;
    
    public JSONObject detail = null;
    
    public int pageNo = 0;
    
    public int pages = 1;
    
    public int result = 0;
    
    public String resultNote = "";
    
    public int totalRecordNum = 1;
    
    public ResponseTemplate(JSONObject reqJson, JSONObject detail)
    {
        if (reqJson == null)
        {
            setCommand("unknown");
        }
        else
        {
            setCommand(reqJson.getString(Consts.CMD_LABEL));
        }
        
        setDetail(detail);
    }
    
    public ResponseTemplate(JSONObject reqJson)
    {
        if (reqJson == null)
        {
            setCommand("unknown");
        }
        else
        {
            setCommand(reqJson.getString(Consts.CMD_LABEL));
        }
        
        JSONObject detail = new JSONObject();
        detail.put(Consts.RESULT, ErrorCode.FAILED);
        setDetail(detail);
    }

    public ResponseTemplate(String cmd, JSONObject detail)
    {
        setCommand(cmd);
        setDetail(detail);
    }
    
    public String getCommand()
    {
        return command;
    }
    
    public JSONObject getDetail()
    {
        return detail;
    }
    
    public int getPageNo()
    {
        return pageNo;
    }
    
    public int getPages()
    {
        return pages;
    }
    
    public int getResult()
    {
        return result;
    }
    
    public String getResultNote()
    {
        return resultNote;
    }
    
    /**
     * 根据成员变量构造返回结构体
     * 
     * @return 返回结构体
     */
    public JSONObject getReturn()
    {
        JSONObject detail = new JSONObject();
        detail.put(Consts.CMD_LABEL, getCommand());
        detail.put(Consts.RESULT, getResult());
        detail.put(Consts.RESULT_NOTE, getResultNote());
        detail.put(Consts.DETAIL, getDetail());
        
        // 消除null "null"的value
        String modifyDetail = detail.toString()
        // 替换网页中的null
            .replaceAll(">null<", "><")
            // 替换"null"
            .replaceAll("\"null\"", "\"\"")
            // 替换null
            .replaceAll(":null", ":\"\"");
        return JSON.parseObject(modifyDetail);
    }
    
    public int getTotalRecordNum()
    {
        return totalRecordNum;
    }
    
    public void setCommand(String command)
    {
        this.command = command;
    }
    
    public void setDetail(JSONObject detail)
    {
        if (detail == null)
        {
            this.detail = new JSONObject();
            return;
        }
        if (detail.containsKey(Consts.CMD_LABEL))
        {
            detail.remove(Consts.CMD_LABEL);
        }
        if (detail.containsKey(Consts.RESULT))
        {
            setResult(detail.getIntValue(Consts.RESULT));
            detail.remove(Consts.RESULT);
        }
        
        if (detail.containsKey(Consts.RESULT_NOTE))
        {
            String note = detail.getString(Consts.RESULT_NOTE);
            if (!note.equals(""))
            {
                setResultNote(note);
            }
            detail.remove(Consts.RESULT_NOTE);
        }
        else
        {
            // 没有返回说明就返回默认值成功
            if (getResult() == ErrorCode.SUCCESS)
            {
                setResultNote("Success");
            }
            else
            {
                // 没有设置错误信息，则取本系统的错误码应对的信息
                setResultNote(ErrorCode.msg(getResult()));
            }
        }
        this.detail = detail;
    }
    
    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }
    
    public void setPages(int pages)
    {
        this.pages = pages;
    }
    
    public void setResult(int result)
    {
        this.result = result;
        if (result != ErrorCode.SUCCESS)
        {
            this.totalRecordNum = 0;
        }
    }
    
    public void setResultNote(String resultNote)
    {
        this.resultNote = resultNote;
    }
    
    public void setTotalRecordNum(int totalRecordNum)
    {
        this.totalRecordNum = totalRecordNum;
    }
    
    public String toString()
    {
        return getReturn().toString();
    }
    
    public static void main(String[] args)
    {
    }
}
