package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface YingYongBaoService {

    /**
    * 描述：实时向应用宝发送激活数据
    * 创建人：liulei
    * 时间：2017/11/30 13:52
    * @version
    */
    public JSONObject updateCurrentDataToYYB(JSONObject jsonObject);



    /**
    * 描述： 向应用宝上传一个月的激活数据
    * 创建人：liulei
    * 时间：2017/11/30 13:53
    * @version
    */
    public void updateLastMonthDataToYYB();


}
