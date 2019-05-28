package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppChannelPageStatisticMapper;
import com.model.AppChannelPageStatisticWithBLOBs;
import com.service.intf.WebPageStatisticsService;
import com.util.DateUtil;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class WebPageStatisticsServiceImpl implements WebPageStatisticsService {

	@Autowired
	private AppChannelPageStatisticMapper appChannelPageStatisticMapper;
	
	@Override
	public JSONObject pageStatistics(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String channel=JsonUtil.getJStringAndCheck(params, "channel",null,false, detail);
		try{
			if(!"".equals(channel)){
				List<Map<String, Object>> aMaps = appChannelPageStatisticMapper.selectByChannelLogin(channel);
				String status = "0";
				if(aMaps==null||aMaps.size()<=0){
					status = "1";;
				}
				//渠道总统计量
				Map<String, Object> map = new HashMap<>();
				map.put("type", "0");
				map.put("channel", channel);
				map.put("date", null);
				List<AppChannelPageStatisticWithBLOBs> aPageStatistics = appChannelPageStatisticMapper.selectByChannelType(map);
				if(aPageStatistics!=null&&aPageStatistics.size()>0){
					AppChannelPageStatisticWithBLOBs aPageStatistic= aPageStatistics.get(0);
					aPageStatistic.setTotal(StringUtil.nvl(Long.parseLong(aPageStatistic.getTotal())+1));
					aPageStatistic.setUpdateDate(new Date());
					appChannelPageStatisticMapper.updateByPrimaryKeySelective(aPageStatistic);
				}else{
					AppChannelPageStatisticWithBLOBs aPageStatistic= new AppChannelPageStatisticWithBLOBs();
					aPageStatistic.setChannel(channel);
					aPageStatistic.setType("0");
					aPageStatistic.setStatus(status);
					aPageStatistic.setTotal("1");
					aPageStatistic.setDayStatistic("0");
					aPageStatistic.setCreateDate(new Date());
					aPageStatistic.setUpdateDate(new Date());
					appChannelPageStatisticMapper.insertSelective(aPageStatistic);
				}
				//渠道每天统计量
				Map<String, Object> map1 = new HashMap<>();
				map1.put("type", "1");
				map1.put("channel", channel);
				map1.put("date", DateUtil.format(new Date(), "yyyy-MM-dd"));
				List<AppChannelPageStatisticWithBLOBs> aChannelPageStatistics = appChannelPageStatisticMapper.selectByChannelType(map1);
				if(aChannelPageStatistics!=null&&aChannelPageStatistics.size()>0){
					AppChannelPageStatisticWithBLOBs aChannelPageStatistic = aChannelPageStatistics.get(0);
					aChannelPageStatistic.setDayStatistic(StringUtil.nvl(Long.parseLong(aChannelPageStatistic.getDayStatistic())+1));
					aChannelPageStatistic.setUpdateDate(new Date());
					appChannelPageStatisticMapper.updateByPrimaryKeySelective(aChannelPageStatistic);
				}else{
					AppChannelPageStatisticWithBLOBs aChannelPageStatistic = new AppChannelPageStatisticWithBLOBs();
					aChannelPageStatistic.setChannel(channel);
					aChannelPageStatistic.setType("1");
					aChannelPageStatistic.setStatus(status);
					aChannelPageStatistic.setDayStatistic("1");
					aChannelPageStatistic.setTotal("0");
					aChannelPageStatistic.setCreateDate(new Date());
					aChannelPageStatistic.setUpdateDate(new Date());
					appChannelPageStatisticMapper.insertSelective(aChannelPageStatistic);
				}
			}
		}catch (Exception e) {
		}
		return detail;
	}

}
