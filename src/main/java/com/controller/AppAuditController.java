package com.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.service.intf.AppAuditService;
import com.util.StringUtil;

import net.sf.json.JSONObject;

@Controller
public class AppAuditController {
	private static final Logger logger = Logger.getLogger(AppAuditController.class);
	
	@Autowired
	private AppAuditService appAuditService;
	
	/**
     * APP iOS审核
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/review")
    @ResponseBody
    public void appAlipayNotify(HttpServletRequest request, HttpServletResponse response) {
        //获取支付宝POST过来反馈信息
    	String reqstr;
		try {
			reqstr = StringUtil.getRequestPostStr(request);
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) JSON.parse(reqstr);
			JSONObject jsonObject = JSONObject.fromObject(map); 
			JSONObject auditSwitch = appAuditService.appAudit(jsonObject);
			response.getWriter().write(auditSwitch.toString());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
    }
    
}
