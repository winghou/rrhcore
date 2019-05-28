package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppPostbox;

public interface AppPostboxMapper {
	//查询邮箱列表
	List<AppPostbox> selectPostboxList();
	//查询邮箱对象
	AppPostbox selectappPostboxType(String postboxType);

}
