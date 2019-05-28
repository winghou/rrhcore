package com.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppDownloadUrlMapper;
import com.frame.Consts;
import com.model.AppDownloadUrl;
import com.service.intf.AppDownloadUrlService;
import com.util.ErrorCode;
import com.util.StringUtil;
@Service
public class AppDownloadUrlServiceImpl implements AppDownloadUrlService {

	@Autowired
	private AppDownloadUrlMapper appDownloadUrlMapper;
	
	/**
	 * 下载地址
	 */
	@Override
	public JSONObject downloadUrl() {
		JSONObject detail = new JSONObject();
		List<AppDownloadUrl> appDownloadUrls = appDownloadUrlMapper.selectByDownloadType("1");//iOS
		if(appDownloadUrls!=null&&appDownloadUrls.size()>0){
			int i = (int) (Math.random() * (appDownloadUrls.size()));
			AppDownloadUrl appDownloadUrl = appDownloadUrls.get(i);
			String downloadUrl = StringUtil.nvl(appDownloadUrl.getDownloadUrl());
			Long visitNum = Long.parseLong("".equals(StringUtil.nvl(appDownloadUrl.getVisitNum()))?"0":StringUtil.nvl(appDownloadUrl.getVisitNum()));
			appDownloadUrl.setUpdateDate(new Date());
			appDownloadUrl.setVisitNum(StringUtil.nvl(visitNum+1));
			appDownloadUrlMapper.updateByPrimaryKeySelective(appDownloadUrl);
			detail.put("downloadUrliOS", downloadUrl);
		}
		List<AppDownloadUrl> downloadUrls = appDownloadUrlMapper.selectByDownloadType("2");//Android
		if(downloadUrls!=null&&downloadUrls.size()>0){
			int i = (int) (Math.random() * (downloadUrls.size()));
			AppDownloadUrl downloadUrl = downloadUrls.get(i);
			String downloadUrlAndriod = StringUtil.nvl(downloadUrl.getDownloadUrl());
			Long visitNum = Long.parseLong("".equals(StringUtil.nvl(downloadUrl.getVisitNum()))?"0":StringUtil.nvl(downloadUrl.getVisitNum()));
			downloadUrl.setUpdateDate(new Date());
			downloadUrl.setVisitNum(StringUtil.nvl(visitNum+1));
			appDownloadUrlMapper.updateByPrimaryKeySelective(downloadUrl);
			detail.put("downloadUrlAndriod", downloadUrlAndriod);
		}
		return detail;
	}

}
