package com.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.*;
import com.frame.Consts;
import com.model.*;
import com.service.intf.AppAllActivitiesService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AppAllActivitiesServiceImpl implements AppAllActivitiesService {

	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppRedBagWithdrawMapper appRedBagWithdrawMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLotteryCalculateMapper appLotteryCalculateMapper;
	@Autowired
	private AppIncreaseCreditMapper appIncreaseCreditMapper;
	@Autowired
	private APPWithDrawApplMapper appWithDrawApplMapper;
	@Autowired
	private AppIncreaseCreditRecordMapper appIncreaseCreditRecordMapper;

	/**
	 * 查询用户获得奖励金额
	 */
	@Override
	public JSONObject queryUserRewardAmt(JSONObject params){
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);

		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		//邀请成功一个用户的金额
		List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("MCH_INVITE_REWARD_AMT");
		String inviteRewardAmt = StringUtil.nvl(maps.get(0).get("ITEM_VALUE"));
		if(null != appUser){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			//个人邀请码
			String inviteCode = StringUtil.nvl(loanAppl.getInviteCode());

			List<Map<String, Object>> authonMaps = appLoanApplMapper.selectAuthonCountByInviter(inviteCode);
			//统计借款成功的用户人数
			int count = 0;
			//可提现金额
			String canWithdrawAmt = "0.00";
			AppRedBagWithdraw redBagWithdraw = appRedBagWithdrawMapper.selectByApprId(loanAppl.getId());
			if(null != redBagWithdraw && null != redBagWithdraw.getUseAmt()){
				canWithdrawAmt = redBagWithdraw.getUseAmt();
			}
			//剩余抽奖次数
			int lotteryNum = 0;
			//已用抽奖次数
//			int usedLotteryNum = 0;
			AppLotteryCalculate lotteryCalculate = appLotteryCalculateMapper.selectByApprId(loanAppl.getId());
			if(null != lotteryCalculate){
//				usedLotteryNum = null == lotteryCalculate.getConsumedNum() ? 0 : lotteryCalculate.getConsumedNum();
				lotteryNum = null == lotteryCalculate.getRemainingNum() ? 0 : lotteryCalculate.getRemainingNum();
			}
			//已获取金额
			String rewardAmt = "0.00";
			if(null != authonMaps && authonMaps.size() > 0){
				count = authonMaps.size();
				rewardAmt = new BigDecimal(inviteRewardAmt).multiply(new BigDecimal(count)).toString();
			}
			//已获取提额额度
			String totalIncreasedCredit = "0";
			//剩余可提现额度
			String remainingIncreasedCredit = "0";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("apprId", loanAppl.getId());
			map.put("type", "0");
			AppIncreaseCredit increaseCredit = appIncreaseCreditMapper.selectByApprIdAndType(map);
			if(null != increaseCredit){
				totalIncreasedCredit = increaseCredit.getTotalIncreasedCredit() + "";
				remainingIncreasedCredit = increaseCredit.getRemainingIncreasedCredit() + "";
			}
			//好友动态
			JSONArray jsonArray = new JSONArray();
			List<AppLoanAppl> loanAppls = appLoanApplMapper.selectByInviter(inviteCode);
			if (null != loanAppls && loanAppls.size() > 0) { // 有已注册的用户
				/*AppLoanCtm loanCtm = null;
				JSONObject jsonObject = null;
				boolean isLoan = false;
				for (AppLoanAppl appl : loanAppls) {
					isLoan = false;
					jsonObject = new JSONObject();
					jsonObject.put("invitedUserPhone", appl.getItemCode().substring(0, 3) + "****" + appl.getItemCode().substring(7));
					if (null != inviteMaps && inviteMaps.size() > 0) { // 有放款成功的用户
						for (Map<String, Object> inviteMap : inviteMaps) {
							if (appl.getId().intValue() == Integer.parseInt(StringUtil.nvl(inviteMap.get("appr_id")))) {
								jsonObject.put("invitedUserStatus", "交易成功");
								isLoan = true;
								break;
							}
						}
					}
					if(!isLoan){
						loanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
						if ("0".equals(loanCtm.getSchedule_status())) {
							jsonObject.put("invitedUserStatus", "已注册");
						} else if("8".equals(loanCtm.getSchedule_status()) && null != loanCtm.getBind_card_time()){
							jsonObject.put("invitedUserStatus", "已认证");
						} else{
							jsonObject.put("invitedUserStatus", "认证中");
						}
					}
					jsonArray.add(jsonObject);
				}*/

				AppLoanCtm loanCtm = null;
				JSONObject jsonObject = null;
				for (AppLoanAppl appl : loanAppls) {
					jsonObject = new JSONObject();
					jsonObject.put("invitedUserPhone", appl.getItemCode().substring(0, 3) + "****" + appl.getItemCode().substring(7));
					loanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
					if("2".equals(appl.getStatus()) && null != appl.getAuthenTime()){
						jsonObject.put("invitedUserStatus", "认证成功");
					}else if("0".equals(appl.getStatus()) && "0".equals(loanCtm.getSchedule_status())){
						jsonObject.put("invitedUserStatus", "已注册");
					}else{
						jsonObject.put("invitedUserStatus", "认证中");
					}
					jsonArray.add(jsonObject);
				}
			}
			detail.put("inviteCode", inviteCode); 								//邀请码
			detail.put("canWithdrawAmt", canWithdrawAmt);						//可提现金额
			detail.put("inviteRewardAmt", inviteRewardAmt);						//邀请成功一个用户的金额
			detail.put("rewardAmt", rewardAmt);									//已获取金额
			detail.put("totalIncreasedCredit", totalIncreasedCredit);			//已获取提额额度
			detail.put("remainingIncreasedCredit", remainingIncreasedCredit);	//剩余可提现额度
			detail.put("loanUserCount", count + "");							//统计认证成功的用户人数
			detail.put("lotteryNum", lotteryNum + "");									//剩余抽奖次数
			detail.put("inviteFriends", jsonArray);								//好友动态
		}else{
			detail.put("inviteCode", "");								//邀请码
			detail.put("canWithdrawAmt", "");							//可提现金额
			detail.put("inviteRewardAmt", inviteRewardAmt);				//邀请成功一个用户的金额
			detail.put("rewardAmt", "");								//已获取金额
			detail.put("totalIncreasedCredit", "");						//已获取提额额度
			detail.put("remainingIncreasedCredit", "");					//剩余可提现额度
			detail.put("loanUserCount", "");							//统计认证成功的用户人数
			detail.put("lotteryNum", "");								//剩余抽奖次数
			detail.put("inviteFriends", new ArrayList<>());				//好友动态
		}
		return detail;
	}

	/**
	 * 展示抽奖转盘
	 */
	@Override
	public JSONObject showLotteryTable(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);

		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			int remainingLotteryNum = 0;
			int increasedCredit = 0;
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppLotteryCalculate lotteryCalculate = appLotteryCalculateMapper.selectByApprId(loanAppl.getId());
			//有抽奖记录
			if(null != lotteryCalculate){
				remainingLotteryNum = null == lotteryCalculate.getRemainingNum() ? 0 : lotteryCalculate.getRemainingNum();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("apprId", loanAppl.getId());
				map.put("type", "0");
				AppIncreaseCredit increaseCredit = appIncreaseCreditMapper.selectByApprIdAndType(map);
				if(null != increaseCredit){
					increasedCredit = null == increaseCredit.getTotalIncreasedCredit() ? 0 : increaseCredit.getTotalIncreasedCredit();
				}
			}
			detail.put("remainingLotteryNum", remainingLotteryNum);
			detail.put("increasedCredit", increasedCredit);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录,请先登录");
		}
		return detail;
	}

	/**
	 * 抽奖
	 *
	 * 1：未授信用户
	 a:仅注册手机号码，可参与活动并且可提现，抽奖提额为0元
	 b:完善资料未授信，可参与活动并且可提现，抽奖提额为0元
	 2：授信用户
	 a:正在逾期用户，还款后方可提现，抽奖可以提额0元
	 b:未逾期且无还款记录用户，抽奖提额10元(最高提额200元)
	 c:未逾期且有还款记录用户，抽奖提额20元(最高提额200元)
	 d:当用户抽奖金额为190元时再次抽奖获得10元
	 e:b/c/d中奖几率为60%
	 */
	@Override
	public JSONObject lotteryActivity(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppLotteryCalculate lotteryCalculate = appLotteryCalculateMapper.selectByApprId(loanAppl.getId());
			int getLotteryAmt = 0;
			boolean firstTime = false;
			if(null != lotteryCalculate){ //获得过抽奖机会
				if(lotteryCalculate.getRemainingNum() > 0){ //有抽奖次数
					if("2".equals(loanAppl.getStatus()) && null != loanAppl.getAuthenTime()){
						List<APPWithDrawAppl> drawAppls = appWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
						if (null != drawAppls && drawAppls.size() > 0) {
							// 逾期订单
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("apprId", loanAppl.getId());
							map.put("status", "2");
							map.put("loanStatus", "3");
							List<APPWithDrawAppl> overdueAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
							// 结清订单
							map = new HashMap<String, Object>();
							map.put("apprId", loanAppl.getId());
							map.put("status", "3");
							map.put("loanStatus", "3");
							List<APPWithDrawAppl> settleAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
							if (null == overdueAppls || overdueAppls.size() == 0) { // 无逾期
								int probability = StringUtil.lotteryProbability(60);
								if(null != settleAppls && settleAppls.size() > 0){
									//抽奖提额为20
									getLotteryAmt = probability * 20;
								}else{
									//抽奖提额为10
									getLotteryAmt = probability * 10;
								}
							}
						}
					}
				}else{ //无抽奖次数
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您的抽奖次数用完啦，请邀请好友获取抽奖机会");
					return detail;
				}
			}else{ //第一次获得抽奖机会
				lotteryCalculate = new AppLotteryCalculate();
				lotteryCalculate.setApprId(loanAppl.getId());
				firstTime = true;
			}
			//扣除抽奖次数
			lotteryCalculate.setConsumedNum(lotteryCalculate.getConsumedNum() + 1);
			if(lotteryCalculate.getRemainingNum() - 1 <= 0){
				lotteryCalculate.setRemainingNum(0);
			}else{
				lotteryCalculate.setRemainingNum(lotteryCalculate.getRemainingNum() - 1);
			}
			if(firstTime){
				appLotteryCalculateMapper.insertSelective(lotteryCalculate);
			}
			appLotteryCalculateMapper.updateByPrimaryKeySelective(lotteryCalculate);
			//计算获取额度
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("apprId", loanAppl.getId());
			m.put("type", "0");
			AppIncreaseCredit increaseCredit = appIncreaseCreditMapper.selectByApprIdAndType(m);
			if(null != increaseCredit){
				int totalIncreasedCredit = null == increaseCredit.getTotalIncreasedCredit() ? 0 : increaseCredit.getTotalIncreasedCredit();
				if((totalIncreasedCredit + getLotteryAmt) > 200){
					getLotteryAmt = 200 - totalIncreasedCredit;
				}
				increaseCredit.setTotalIncreasedCredit(totalIncreasedCredit + getLotteryAmt);
				increaseCredit.setRemainingIncreasedCredit(increaseCredit.getRemainingIncreasedCredit() + getLotteryAmt);
				appIncreaseCreditMapper.updateByPrimaryKeySelective(increaseCredit);
			}else{
				increaseCredit = new AppIncreaseCredit();
				increaseCredit.setApprId(loanAppl.getId());
				increaseCredit.setType("0");
				increaseCredit.setTotalIncreasedCredit(getLotteryAmt);
				increaseCredit.setConsumedIncreasedCredit(0);
				increaseCredit.setRemainingIncreasedCredit(getLotteryAmt);
				appIncreaseCreditMapper.insertSelective(increaseCredit);
			}
			AppIncreaseCreditRecord creditRecord = new AppIncreaseCreditRecord();
			creditRecord.setApprId(loanAppl.getId());
			creditRecord.setIncreasedCredit(getLotteryAmt);
			creditRecord.setType("0");
			creditRecord.setCreateTime(new Date());
			appIncreaseCreditRecordMapper.insertSelective(creditRecord);
			String creditResult = "";
			switch (getLotteryAmt) {
				case 0:
					creditResult = "0";
					break;
				case 10:
					creditResult = "1";
					break;
				case 20:
					creditResult = "2";
					break;
			}
			detail.put("creditResult", creditResult);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录,请先登录");
		}
		return detail;
	}

	@Override
	public JSONObject loverActivity(JSONObject params) {
		JSONObject detail = new JSONObject();
		// 活动开始时间
		String start_time = StringUtil.isBlank(params.getString("start_time")) ? "2018-02-01 00:00:01" : params.getString("start_time");
		// 活动结束时间
		String end_time = StringUtil.isBlank(params.getString("end_time")) ? "2018-02-08 23:59:59" : params.getString("end_time");
		Map<String, Object> map = new HashMap<>();
		map.put("start_time", start_time);
		map.put("end_time", end_time);
		// 活动时间段内的用户借款统计
		List<Map<String, Object>> orderlist = appWithDrawApplMapper.selectActivityOrderRecords(map);
		int size = 10;
		if (orderlist != null && orderlist.size() > 0) {
			if (orderlist.size() < 10) {
				size = orderlist.size();
			}
		} else {
			return detail;
		}
		// 取出前10名
		List<Map<String, Object>> list = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			list.add(orderlist.get(i));
		}
		detail.put("list", list);
		// 计算当前用户的排名及借款金额
		String version = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);// 获取真实userid
		AppUser appUser = appUserMapper.selectByMchVersion(version);
		String borrow_amt = "0";
		String rank = "";
		// 用户未登录
		if (appUser == null) {
			detail.put("borrow_amt", 0);
			detail.put("rank", "");
			return detail;
		}
		String user_id = String.valueOf(appUser.getUserid());
		for (int i = 0; i < orderlist.size(); i++) {
			Map<String, Object> userMap = orderlist.get(i);
			if (String.valueOf(userMap.get("user_id")).equals(user_id)) {
				borrow_amt = String.valueOf(userMap.get("borrow_amt"));
				rank = String.valueOf(i + 1);
				break;
			}
		}
		int borrow_amt2 = (int) Double.parseDouble(borrow_amt);
		detail.put("borrow_amt", borrow_amt2);
		detail.put("rank", rank);
		return detail;
	}

}
