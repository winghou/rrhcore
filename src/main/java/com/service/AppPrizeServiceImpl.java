package com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppPrizeAcountMapper;
import com.dao.AppPrizeFakeListMapper;
import com.dao.AppPrizeMapper;
import com.dao.AppPrizePersonInfoMapper;
import com.dao.AppPrizeRecordMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppPrize;
import com.model.AppPrizeAcount;
import com.model.AppPrizeFakeList;
import com.model.AppPrizePersonInfo;
import com.model.AppPrizeRecord;
import com.model.AppUser;
import com.service.intf.AppPrizeService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

/**
 * 抽奖活动类
 * 
 * @author Administrator
 *
 */
@Service
public class AppPrizeServiceImpl implements AppPrizeService {

	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppPrizeMapper appPrizeMapper;
	@Autowired
	private AppPrizeAcountMapper appPrizeAcountMapper;
	@Autowired
	private AppPrizeRecordMapper appPrizeRecordMapper;
	@Autowired
	private AppPrizePersonInfoMapper appPrizePersonInfoMapper;
	@Autowired
	private AppPrizeFakeListMapper appPrizeFakeListMapper;

	/**
	 * 抽奖
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public synchronized JSONObject prizeLottery(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			// 11.03 18:00:00日活动下线
			Date endDate = DateUtil.parseDate("2017-11-03 18:00:00");
			long endTime = endDate.getTime();
			long nowTime = new Date().getTime();
			// 超过活动期限活动自动下线
			if(nowTime > endTime){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "本活动已经结束啦，请关注随心花其他活动！");
				return detail;
			}
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			// 验证抽检次数
			AppPrizeAcount prizeAcount = appPrizeAcountMapper.selectByapprId(loanAppl.getId());
			if (null != prizeAcount) {
				int remainNum = null == prizeAcount.getRemainingLotteryNum() ? 0 : prizeAcount.getRemainingLotteryNum().intValue();
				if (remainNum >= 1) {
					// 开始抽奖
					// 统计奖品
					List<AppPrize> appPrizes = appPrizeMapper.selectAll();
					// 奖品抽完了就不再抽奖,默认谢谢参与
					int count = 0;
					for(AppPrize appPrize : appPrizes){
						int amount = null == appPrize.getRemianingAmount() ? 0 : appPrize.getRemianingAmount().intValue();
						if(7 != appPrize.getId() && amount > 0){
							count++;
						}
					}
					int num = 7;
					if (count > 0) {
						// 抽奖
						num = getPrizeIndex(appPrizes, loanAppl);
						if (8 == num) {
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "抽奖异常，请重试！");
							return detail;
						}
					}
					// 重新计算用户抽奖次数
					appPrizeAcountMapper.updateRemainningNum(prizeAcount.getId());
					// 记录抽中奖品,包括未抽中奖品
					AppPrizeRecord prizeRecord = new AppPrizeRecord();
					prizeRecord.setApprId(loanAppl.getId());
					prizeRecord.setCreateDate(new Date());
					prizeRecord.setPrizeId(num);
					appPrizeRecordMapper.insertSelective(prizeRecord);
					//展示用户填写地址
					AppPrizePersonInfo appPrizePersonInfo = appPrizePersonInfoMapper.selectByApprId(loanAppl.getId());
					String customName = "";
					String customPhone = "";
					String customAddress = "";
					String isFullInfo = "0";
					if(null != appPrizePersonInfo){
						customName = StringUtil.nvl(appPrizePersonInfo.getCustomName());
						customPhone = StringUtil.nvl(appPrizePersonInfo.getCustomPhone());
						customAddress = StringUtil.nvl(appPrizePersonInfo.getCustomAddress());
						if(!"".equals(customName) && !"".equals(customPhone) && !"".equals(customAddress)){
							isFullInfo = "1";
						}
					}
					detail.put("isFullInfo", isFullInfo); //0：未填写完全，1：填写完全
					detail.put("prizeNum", num + "");
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "抽检次数已使用完，提现可获取抽奖机会！");
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您还没有抽奖机会，申请借款可获取！");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 抽检方法 根据Math.random()产生一个double型的随机数，判断每个奖品出现的概率
	 * 
	 * @param prizes
	 * @return random：奖品列表prizes中的序列（prizes中的第random个就是抽中的奖品）
	 */
	public int getPrizeIndex(List<AppPrize> prizes, AppLoanAppl loanAppl) {
		// 默认谢谢参与
		int random = 7;
		try {
			// 计算总权重
			double sumWeight = 0;
			for (AppPrize p : prizes) {
				sumWeight += p.getWeight();
			}

			// 产生随机数
			double randomNumber;
			randomNumber = Math.random();

			// 根据随机数在所有奖品分布的区域并确定所抽奖品
			double d1 = 0;
			double d2 = 0;
			AppPrize prize = null;
			for (int i = 0; i < prizes.size(); i++) {
				prize = prizes.get(i);
				d2 += Double.parseDouble(String.valueOf(prizes.get(i).getWeight())) / sumWeight;
				if (i == 0) {
					d1 = 0;
				} else {
					d1 += Double.parseDouble(String.valueOf(prizes.get(i - 1).getWeight())) / sumWeight;
				}
				// 满足概率区间
				if (randomNumber >= d1 && randomNumber < d2) {
					if (7 != prize.getId() && prize.getRemianingAmount() > 0) { // 抽中奖项,并且奖品未抽完
						// 抽中IPX
						if (6 == prize.getId()) {
							// 需校验用户授信信息
							if ("2".equals(StringUtil.nvl(loanAppl.getStatus())) && null != loanAppl.getAuthenTime()) {
								random = i + 1;
								// 抽中一次奖项减去一次
								appPrizeMapper.updatePrizeNum(prize.getId());
							}
							// 未授信的用户不能抽到奖品
						} else { // 抽中其他奖品
							random = i + 1;
							appPrizeMapper.updatePrizeNum(prize.getId());
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("生成抽奖随机数出错，出错原因：" + e.getMessage());
			random = 8;
		}
		return random;
	}

	/**
	 * 展示抽奖转盘
	 */
	@Override
	public JSONObject prizeLotteryTable(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		List<AppPrizeFakeList> prizeFakeLists = appPrizeFakeListMapper.selectListsRandly(21); //取其中21条
		List<String> fakeLists = new ArrayList<String>();
		if(null != prizeFakeLists && prizeFakeLists.size() > 0){
			for(AppPrizeFakeList prizeFakeList : prizeFakeLists){
				fakeLists.add(StringUtil.nvl(prizeFakeList.getContent()));
			}
		}
		String isLottery = "0";
		if (null != appUser) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppPrizeAcount acount = appPrizeAcountMapper.selectByapprId(loanAppl.getId());
			List<AppPrizeRecord> appPrizeRecords = appPrizeRecordMapper.selectByapprIdWithoutThx(loanAppl.getId());
			int lotteryNum = 0;
			if(null != acount){
				lotteryNum = acount.getRemainingLotteryNum();
			}else{
				detail.put("lotteryNum", "0");
				detail.put("isLottery", isLottery);
				detail.put("lotteryRecord", fakeLists);
				return detail;
			}
			if(null != appPrizeRecords && appPrizeRecords.size() > 0){
				isLottery = "1";
			}
			detail.put("lotteryNum", lotteryNum + "");
			detail.put("isLottery", isLottery);
			detail.put("lotteryRecord", fakeLists);
		} else {
			detail.put("lotteryNum", "0");
			detail.put("isLottery", isLottery);
			detail.put("lotteryRecord", fakeLists);
		}
		return detail;
	}

	/**
	 * 查询中奖记录
	 */
	@Override
	public JSONObject showPrizeRecord(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			List<Map<String, Object>> prizeRecords = appPrizeRecordMapper.selectByapprIdGroupByPrizeAndDate(loanAppl.getId());
			if (null != prizeRecords && prizeRecords.size() > 0) {
				JSONArray jsonArray = new JSONArray();
				JSONObject object = null;
				List<AppPrize> prizes = appPrizeMapper.selectAll();
				for (Map<String, Object> prizeMap : prizeRecords) {
					object = new JSONObject();
					String prizeName = "";
					int prizeId = Integer.parseInt(StringUtil.nvl(prizeMap.get("prize_id")));
					for(AppPrize p : prizes){
						if(prizeId == p.getId().intValue()){
							prizeName = p.getPrizeName();
							break;
						}
					}
					object.put("prizeName", prizeName);
					object.put("prizeDate", DateUtil.format(DateUtil.parseDate(StringUtil.nvl(prizeMap.get("date"))), "MM/dd"));
					object.put("prizeNum", StringUtil.nvl(prizeMap.get("num")));
					jsonArray.add(object);
				}
				//展示用户填写地址
				AppPrizePersonInfo appPrizePersonInfo = appPrizePersonInfoMapper.selectByApprId(loanAppl.getId());
				String customName = "";
				String customPhone = "";
				String customAddress = "";
				String isFullInfo = "0";
				if(null != appPrizePersonInfo){
					customName = StringUtil.nvl(appPrizePersonInfo.getCustomName());
					customPhone = StringUtil.nvl(appPrizePersonInfo.getCustomPhone());
					customAddress = StringUtil.nvl(appPrizePersonInfo.getCustomAddress());
					if(!"".equals(customName) && !"".equals(customPhone) && !"".equals(customAddress)){
						isFullInfo = "1";
					}
				}
				detail.put("customName", customName);
				detail.put("customPhone", customPhone);
				detail.put("customAddress", customAddress);
				detail.put("isFullInfo", isFullInfo); //0：未填写完全，1：填写完全
				detail.put("prizeArray", jsonArray);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您还没有抽中奖品，请继续努力！");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请登录后查看中奖信息！");
		}
		return detail;
	}
	
	/**
	 * 修改中奖资料
	 */
	@Override
	public JSONObject updatePrizePersonInfo(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String customName = JsonUtil.getJStringAndCheck(params, "customName", null, true, detail);
		String customPhone = JsonUtil.getJStringAndCheck(params, "customPhone", null, true, detail);
		String customAddress = JsonUtil.getJStringAndCheck(params, "customAddress", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppPrizePersonInfo prizePersonInfo = appPrizePersonInfoMapper.selectByApprId(loanAppl.getId());
			if(null != prizePersonInfo){
				prizePersonInfo.setCustomName(customName);
				prizePersonInfo.setCustomPhone(customPhone);
				prizePersonInfo.setCustomAddress(customAddress);
				prizePersonInfo.setType("0");
				appPrizePersonInfoMapper.updateByPrimaryKeySelective(prizePersonInfo);
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put(Consts.RESULT_NOTE, "修改信息成功！");
			}else{
				prizePersonInfo = new AppPrizePersonInfo();
				prizePersonInfo.setApprId(loanAppl.getId());
				prizePersonInfo.setCustomName(customName);
				prizePersonInfo.setCustomPhone(customPhone);
				prizePersonInfo.setCustomAddress(customAddress);
				prizePersonInfo.setType("0");
				appPrizePersonInfoMapper.insertSelective(prizePersonInfo);
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put(Consts.RESULT_NOTE, "保存信息成功！");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请登录后修改中奖信息！");
		}
		return detail;
	}

}
