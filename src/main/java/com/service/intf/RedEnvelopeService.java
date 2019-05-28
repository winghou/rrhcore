package com.service.intf;

import com.alibaba.fastjson.JSONObject;

/**
 * 红包管理 service
 * 
 * @author fum
 *
 */
public interface RedEnvelopeService {
    
    /**
     * 查询红包明细列表
     * @param apprId 个人唯一id
     * @return JSONObject
     */
    public JSONObject qryRedEnvelopeDetail(JSONObject params);
    
    
    /**
     * 查询个人红包情况
     * @param apprId 个人唯一id
     * @return JSONObject 
     */
    public JSONObject qryRedEnvelope(JSONObject params);
    
    /**
     * 红包提现service
     * @param cashWithdrawal 红包提现金额-
     * @param apprId 个人唯一id
     * @return JSONObject
     */
    public JSONObject withdrawalsRedEnvelope(JSONObject params);
    
    

}
