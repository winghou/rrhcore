package com.util;

import java.util.Comparator;

import com.alibaba.fastjson.JSONObject;

public class JSONComparator implements Comparator<JSONObject>
{
    
    String field = "";// 排序字段名
    
    String order = "";// 排序：desc降序 asc 升序
    
    String fileType = ""; // 字段类型：string ,integer
    
    public JSONComparator(String field, String order, String fileType)
    {
        this.field = field;
        this.order = order.toLowerCase();
        this.fileType = fileType.toLowerCase();
    }
    
    @Override
    public int compare(JSONObject json1, JSONObject json2)
    {
        if ("string".equals(fileType))
        {
            String date1 = JsonUtil.getJString(json1, field);
            String date2 = JsonUtil.getJString(json2, field);

            if (date1.compareTo(date2) < 0)
            {
                if ("desc".equals(order)){
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
            else if (date1.compareTo(date2) > 0)
            {
                if ("desc".equals(order))
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            return 0;
        }
        else
        {
            int date1 = JsonUtil.getJInt(json1, field);
            int date2 = JsonUtil.getJInt(json2, field);
            if (date1 < date2)
            {
                if ("desc".equals(order))
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
            else if (date1 > date2)
            {
                if ("desc".equals(order))
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
            return 0;
        }
    }

}
