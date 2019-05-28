package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppPrizeService {

	/**
	 * 抽奖
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject prizeLottery(JSONObject params) throws Exception;

	/**
	 * 展示抽奖转盘
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject prizeLotteryTable(JSONObject params);

	/**
	 * 查询中奖记录
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject showPrizeRecord(JSONObject params);
	
	/**
	 * 修改中奖信息
	 * @param params
	 * @return
	 */
	public JSONObject updatePrizePersonInfo(JSONObject params);
	
	

}
