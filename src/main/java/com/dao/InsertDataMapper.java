package com.dao;

import java.util.Map;

public interface InsertDataMapper {
	void insertAppOrderAppl(Map<String,Object> map);
	void insertAppOrderAttch(Map<String,Object> map);
	void insertAppOrderBook(Map<String,Object> map);
	void insertAppOrderCtmCnt(Map<String,Object> map);
	void insertAppOrderCtmship(Map<String,Object> map);
	void insertAppOrderCustom(Map<String,Object> map);
	void insertAppOrderZhimaScore(Map<String,Object> map);

}
