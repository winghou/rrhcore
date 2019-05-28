package com.dao;

import java.util.List;
import java.util.Map;

public interface PersonalDataMapper {
	List<Map<String,Object>> queryPersonalData(Integer userId);
	Map<String,Object> queryYituParams();
	int saveImgBase(Map<String,Object> map);
	Map<String,Object> queryImgBase(Integer apprId);
	int deleteImgBase(String id);
	
	void updateContract(); 
	
	public List<Map<String, Object>> selectNotwithDrawId();
	
	public List<Map<String, Object>> selectRewardAmt(Integer userId);

}
