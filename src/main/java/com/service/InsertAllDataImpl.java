package com.service;

import java.util.Map;

import com.dao.InsertDataMapper;
public class InsertAllDataImpl implements Runnable{
	private InsertDataMapper insertDataMapper;
	private Map<String, Object> map;
	
	public InsertAllDataImpl(InsertDataMapper insertDataMapper,Map<String,Object> map){
		this.insertDataMapper=insertDataMapper;
		this.map=map;
		
	}
	

	@Override
	public void run() {
		try {
			//System.out.println(apprId);
//			insertDataMapper.insertAppOrderAppl(map);
//			insertDataMapper.insertAppOrderAttch(map);
//			insertDataMapper.insertAppOrderBook(map);
//			insertDataMapper.insertAppOrderCtmCnt(map);
//			insertDataMapper.insertAppOrderCtmship(map);
//			insertDataMapper.insertAppOrderCustom(map);
//			insertDataMapper.insertAppOrderZhimaScore(map);
			System.out.println("我是线程的方法1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("我是线程的方法结束");
		
	}

}
