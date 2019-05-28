package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.AppSysSessionMapper;
import com.dao.AppUserMapper;
import com.dao.AppWxBindMapper;
import com.dao.AppWxSessionMapper;
import com.model.AppSysSession;
import com.model.AppUser;
import com.model.AppWxBind;
import com.model.AppWxSession;
import com.service.intf.CheckTokenService;
import com.util.DateUtil;
import com.util.StringUtil;

@Service
public class CheckTokenServiceImpl implements CheckTokenService {
	@Autowired
	private AppSysSessionMapper appSysSessionMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppWxSessionMapper appWxSessionMapper;
	@Autowired
	private AppWxBindMapper appWxBindMapper;
	
	@Override
	public String checkToken(Map<String, Object> map) {
		AppUser appUser = appUserMapper.selectByMchVersion(StringUtil.nvl(map.get("userId")));
		String userId = "-10";
		if(null != appUser){
			userId = appUser.getUserid() + "";
		}
		map.put("userId", userId);
		AppSysSession session = appSysSessionMapper.selectByToken(map);
		AppSysSession se = appSysSessionMapper.selectByUserId(Integer.parseInt(userId));
		if(session !=null){
			return "1";
		}else{
			if(se !=null){
				return DateUtil.format(se.getStartDate(), "yyyy-MM-dd HH:mm:ss");
			}else{
				return "0";
			}
		}
	}
	
	@Override
	public String checkwxToken(Map<String, Object> map) {
		List<AppWxBind> AppWxBind = appWxBindMapper.selectByUuid(StringUtil.nvl(map.get("apprId")));
		String userId = "-10";
		if(AppWxBind.size()>0){
			AppWxBind wx=AppWxBind.get(0);
			userId = wx.getApprId() + "";
		}
		map.put("wx_apprid", userId);
		AppWxSession session = appWxSessionMapper.selectBywxToken(map);
		AppWxSession se = appWxSessionMapper.selectBywxUserId(Integer.parseInt(userId));
		if(session !=null){
			return "1";
		}else{
			if(se !=null){
				return DateUtil.format(se.getLgTime(), "yyyy-MM-dd HH:mm:ss");
			}else{
				return "0";
			}
		}
	}

}
