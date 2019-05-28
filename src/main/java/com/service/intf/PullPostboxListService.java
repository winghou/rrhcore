package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface PullPostboxListService {
	//拉取邮箱列表
	JSONObject pullPostboxList(JSONObject params);
	//获取邮箱信息给爬虫进行校验
	JSONObject postboxListValidate(JSONObject params);

}
