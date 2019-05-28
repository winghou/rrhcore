package com.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppMessageMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppMessage;
import com.model.AppUser;
import com.service.intf.QueryAnnounceService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class QueryAnnounceServiceImpl implements QueryAnnounceService {

	@Autowired
	private AppMessageMapper appMessageMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;

	/**
	 * 查询公告信息
	 */
	@Override
	public JSONObject queryAnnounce(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String phoneId = JsonUtil.getJStringAndCheck(params, "phoneId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			userId = mch.getUserid() + "";
			AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
			if (null != user) {
				AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(user.getUserName());
				int apprId = appLoanAppl.getId();

				// 查询轮播图、活动公告
				Map<String, Object> map = new HashMap<>();
				map.put("apprId", StringUtil.nvl(apprId));
				map.put("type", "1");
				List<Map<String, Object>> maps = appMessageMapper.selectByapprIdAndTypeIsRead2(map);
				JSONArray dateList1 = new JSONArray();
				JSONArray dataList2 = new JSONArray();
				if (null != maps && maps.size() > 0) {
					JSONObject jo = null;
					JSONObject jo2 = null;
					String content = "";
					String shareStatus = "";
					for (Map<String, Object> m : maps) {
						jo = new JSONObject();
						jo2 = new JSONObject();
						String shareUrl = StringUtil.toString(m.get("share_url"));
						if(null == shareUrl || "".equals(shareUrl)){
							shareStatus = "0";
						}else{
							shareStatus = "1";
						}
						jo.put("messageId", StringUtil.toString(m.get("id")));
						jo.put("title", StringUtil.toString(m.get("title")));
						jo.put("picUrl", StringUtil.toString(m.get("pic_url")));
						jo.put("h5Url", StringUtil.toString(m.get("h5_url")));
						jo.put("shareStatus", shareStatus);
						dateList1.add(jo);
						content = StringUtil.toString(m.get("comtent"));
						if(!"".equals(content) && null != content){
							jo2.put("messageId", StringUtil.toString(m.get("id")));
							jo2.put("publishTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m.get("publish_time")));
							jo2.put("title", StringUtil.toString(m.get("title")));
							jo2.put("content", StringUtil.toString(m.get("comtent")));
							jo2.put("h5Url", StringUtil.toString(m.get("h5_url")));
							jo2.put("isRead", StringUtil.toString(m.get("isRead")));
							jo2.put("shareStatus", shareStatus);
							dataList2.add(jo2);
						}
					}
				}
				detail.put("picAnnounce", dateList1);
				detail.put("activAnnounce", dataList2);

				// 查询系统公告
				Map<String, Object> map2 = new HashMap<>();
				map2.put("apprId", StringUtil.nvl(apprId));
				map2.put("type", "0");
				List<Map<String, Object>> maps2 = appMessageMapper.selectByapprIdAndTypeIsRead2(map2);
				JSONArray dataList3 = new JSONArray();
				if (null != maps2 && maps2.size() > 0) {
					JSONObject jo = null;
					String shareStatus = "";
					for (Map<String, Object> m : maps2) {
						jo = new JSONObject();
						String shareUrl = StringUtil.toString(m.get("share_url"));
						if(null == shareUrl || "".equals(shareUrl)){
							shareStatus = "0";
						}else{
							shareStatus = "1";
						}
						jo.put("messageId", StringUtil.toString(m.get("id")));
						jo.put("publishTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m.get("publish_time")));
						jo.put("title", m.get("title"));
						jo.put("content", m.get("comtent"));
						jo.put("isRead", m.get("isRead"));
						jo.put("shareStatus", shareStatus);
						dataList3.add(jo);
					}
				}
				detail.put("systemAnnounce", dataList3);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "未登录");
			}
		} else {
			// 查询轮播图、活动公告
			Map<String, Object> map = new HashMap<>();
			map.put("phoneId", phoneId);
			map.put("type", "1");
			List<Map<String, Object>> maps = appMessageMapper.selectByapprIdAndTypeIsRead3(map);
			JSONArray dateList1 = new JSONArray();
			JSONArray dataList2 = new JSONArray();
			String content = "";
			String shareStatus = "";
			if (null != maps && maps.size() > 0) {
				JSONObject jo = null;
				JSONObject jo2 = null;
				for (Map<String, Object> m : maps) {
					jo = new JSONObject();
					jo2 = new JSONObject();
					String shareUrl = StringUtil.toString(m.get("share_url"));
					if(null == shareUrl || "".equals(shareUrl)){
						shareStatus = "0";
					}else{
						shareStatus = "1";
					}
					jo.put("messageId", StringUtil.toString(m.get("id")));
					jo.put("title", StringUtil.toString(m.get("title")));
					jo.put("picUrl", StringUtil.toString(m.get("pic_url")));
					jo.put("h5Url", StringUtil.toString(m.get("h5_url")));
					jo.put("shareStatus", shareStatus);
					dateList1.add(jo);
					content = StringUtil.toString(m.get("comtent"));
					if(!"".equals(content) && null != content){
						jo2.put("messageId", StringUtil.toString(m.get("id")));
						jo2.put("publishTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m.get("publish_time")));
						jo2.put("title", StringUtil.toString(m.get("title")));
						jo2.put("content", StringUtil.toString(m.get("comtent")));
						jo2.put("h5Url", StringUtil.toString(m.get("h5_url")));
						jo2.put("isRead", StringUtil.toString(m.get("isRead")));
						jo2.put("shareStatus", shareStatus);
						dataList2.add(jo2);
					}
				}
			}
			detail.put("picAnnounce", dateList1);
			detail.put("activAnnounce", dataList2);

			// 查询系统公告
			Map<String, Object> map2 = new HashMap<>();
			map2.put("phoneId", phoneId);
			map2.put("type", "0");
			List<Map<String, Object>> maps2 = appMessageMapper.selectByapprIdAndTypeIsRead3(map2);
			JSONArray dataList3 = new JSONArray();
			if (null != maps2 && maps2.size() > 0) {
				JSONObject jo = null;
				for (Map<String, Object> m : maps2) {
					jo = new JSONObject();
					String shareUrl = StringUtil.toString(m.get("share_url"));
					if(null == shareUrl || "".equals(shareUrl)){
						shareStatus = "0";
					}else{
						shareStatus = "1";
					}
					jo.put("messageId", StringUtil.toString(m.get("id")));
					jo.put("publishTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(m.get("publish_time")));
					jo.put("title", m.get("title"));
					jo.put("content", m.get("comtent"));
					jo.put("isRead", m.get("isRead"));
					jo.put("shareStatus", shareStatus);
					dataList3.add(jo);
				}
			}
			detail.put("systemAnnounce", dataList3);
		}
		return detail;
	}

	@Override
	public JSONObject queryShareContent(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String messageId = JsonUtil.getJStringAndCheck(params, "messageId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppMessage appMessage = appMessageMapper.selectByPrimaryKey(Integer.parseInt(messageId));
			if (null != appMessage) {
				detail.put("title", StringUtil.nvl(appMessage.getTitle()));
				detail.put("content", StringUtil.nvl(appMessage.getComtent()));
				detail.put("littlePicUrl", StringUtil.nvl(appMessage.getLittleLogoUrl()));
				detail.put("shareUrl", StringUtil.nvl(appMessage.getShareUrl()));
//				String messId = "1021334"; //19测试环境
				String messId = "1040166"; //生产环境
				if(messageId.equals(messId)){ //来自拉新活动的分享
					detail.put("inviteCode", loanAppl.getInviteCode());
				}else{ //其他活动分享没有邀请码奖励
					detail.put("inviteCode", loanAppl.getInviteCode());
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该消息不存在");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录,请先登录");
		}
		return detail;
	}
}
