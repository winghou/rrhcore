package com.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppMessageMapper;
import com.dao.AppRedBagWithdrawLogMapper;
import com.dao.AppRedBagWithdrawMapper;
import com.dao.AppRedBagWithdrawOrderMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPWithDrawAppl;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppMessage;
import com.model.AppRedBagWithdraw;
import com.model.AppRedBagWithdrawLog;
import com.model.AppRedBagWithdrawOrder;
import com.model.AppUser;
import com.service.intf.RedEnvelopeService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.PhoneValicodeUtil;
import com.util.StringUtil;

@Service
public class RedEnvelopeServiceImpl implements RedEnvelopeService {

    @Autowired
    private AppRedBagWithdrawLogMapper appRedBagLogMapper;
    @Autowired
    private AppRedBagWithdrawMapper appRedBagMapper;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private AppLoanApplMapper appLoanApplMapper;
    @Autowired
    private IfmBaseDictMapper ifmBaseDictMapper;
    @Autowired
    private AppLoanCtmMapper appLoanCtmMapper;
    @Autowired
    private AppRedBagWithdrawOrderMapper appRedBagWithdrawOrderMapper;
    @Autowired
    private AppMessageMapper appMessageMapper;
    @Autowired
    private APPWithDrawApplMapper appWithDrawApplMapper;

    @Override
	public JSONObject qryRedEnvelopeDetail(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			JSONArray array = new JSONArray();
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			List<AppRedBagWithdrawLog> withdrawLogs = appRedBagLogMapper.qryRedEnvelopeDetail(loanAppl.getId());
			if(null != withdrawLogs && withdrawLogs.size() > 0){
				JSONObject object = null;
				for(AppRedBagWithdrawLog withdrawLog : withdrawLogs){
					object = new JSONObject();
					object.put("id", StringUtil.nvl(withdrawLog.getId()));
					object.put("title", StringUtil.nvl(withdrawLog.getTitle()));
					object.put("createDate", DateUtil.format(withdrawLog.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
					if("0".equals(withdrawLog.getType())){ //返现
						object.put("amt", "+" + StringUtil.formatNumberToDecimals(StringUtil.nvl(withdrawLog.getAmt()), 2));
					}else{//提现
						object.put("amt", "-" + StringUtil.formatNumberToDecimals(StringUtil.nvl(withdrawLog.getAmt()), 2));
					}
					array.add(object);
				}
			}
			detail.put("items", array);
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

    @Override
	public JSONObject qryRedEnvelope(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppRedBagWithdraw redBagWithdraw = appRedBagMapper.selectByApprId(loanAppl.getId());
			String useAmt = "0.00";
			String totalMakeAmt = "0.00";
			if (null != redBagWithdraw) {
				useAmt = StringUtil.formatNumberToDecimals(redBagWithdraw.getUseAmt(), 2);
				totalMakeAmt = StringUtil.formatNumberToDecimals(redBagWithdraw.getTotalMakeAmt(), 2);
			}
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
			String bankCardAb = "最新绑定的银行卡";
			/*if(null != appLoanCtm.getBankCard() && null != appLoanCtm.getBind_card_time()){
				List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("BANK_NAME");
				if(null != maps && maps.size() > 0){
					for(Map<String, Object> map : maps){
						if(appLoanCtm.getBank().equals(StringUtil.nvl(map.get("ITEM_KEY")))){
							bankCardAb = StringUtil.nvl(map.get("ITEM_VALUE")) + "(****" + appLoanCtm.getBankCard().substring(appLoanCtm.getBankCard().length() - 4) + ")";
							break;
						}
					}
				}
			}*/
			String checkCardStatus = null == appLoanCtm.getBind_card_time() ? "0" : "1";
			detail.put("useAmt", useAmt);
			detail.put("totalMakeAmt", totalMakeAmt);
			detail.put("bankCardAb", bankCardAb);
			detail.put("checkCardStatus", checkCardStatus);
			List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("MCH_WITHDRAW_RULE_URL");
			String withdrawRuleUrl = "";
			if (null != maps && maps.size() > 0) {
				withdrawRuleUrl = StringUtil.nvl(maps.get(0).get("ITEM_VALUE"));
			}
			detail.put("withdrawRuleUrl", withdrawRuleUrl);
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

    @Override
    @Transactional(rollbackFor = Exception.class)
	public JSONObject withdrawalsRedEnvelope(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String cashWithdrawal = JsonUtil.getJStringAndCheck(params, "cashWithdrawal", null, false, detail);
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("USE_CMT_SUM");
			String result = StringUtil.nvl(maps.get(0).get("ITEM_VALUE"));
			String minWithdraw = StringUtil.nvl(maps.get(0).get("ITEM_KEY"));
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
//			if(!"".equals(StringUtil.nvl(loanAppl.getAccountStatus()))){
			if("2".equals(StringUtil.nvl(loanAppl.getAccountStatus())) || "0".equals(StringUtil.nvl(loanAppl.getAccountStatus()))){ //账户异常
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "很抱歉，你的账户存在异常，赶快联系微信公众号随心花吧！她会帮你解决哒！");
				return detail;
			}else if("3".equals(StringUtil.nvl(loanAppl.getAccountStatus()))){ //账户关闭
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
				return detail;
			}else if("1".equals(StringUtil.nvl(loanAppl.getAccountStatus())) || "".equals(StringUtil.nvl(loanAppl.getAccountStatus()))){ //可提现红包
				double withdrawAmt = StringUtil.parseDouble(cashWithdrawal, 0.0);
				List<APPWithDrawAppl> withDrawAppls = appWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
				if (null != withDrawAppls && withDrawAppls.size() > 0) {
					int overduleOrder = 0;
					for (APPWithDrawAppl withDrawAppl : withDrawAppls) {
						// 逾期
						if ("2".equals(withDrawAppl.getStatus()) && "3".equals(withDrawAppl.getLoanStatus())) {
							overduleOrder++;
						}
					}
					if (overduleOrder > 0) {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您当前有逾期订单，暂不可提现");
						return detail;
					}
				}
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
				if(null != appLoanCtm.getBind_card_time()){
					if (withdrawAmt >= Double.parseDouble(minWithdraw)) {
						AppRedBagWithdraw redBagWithdraw = appRedBagMapper.selectByApprId(loanAppl.getId());
						if(null != redBagWithdraw){
							double itemValue = StringUtil.parseDouble(result, 0.0);
							double txzje = appRedBagLogMapper.qryUseTxSum(loanAppl.getId());
							if (StringUtil.parseDouble(redBagWithdraw.getUseAmt(), 0.0) >= withdrawAmt) {
								if ( 1 != (new BigDecimal(txzje).add(new BigDecimal(withdrawAmt))).compareTo(new BigDecimal(itemValue))) {// 提现的总金额提现金额大于设定的总金额
									//扣除红包可用金额
									String totalConsumeAmt = "".equals(StringUtil.nvl(redBagWithdraw.getTotalConsumeAmt())) ? "0" : StringUtil.nvl(redBagWithdraw.getTotalConsumeAmt());
									String useAmt = "".equals(StringUtil.nvl(redBagWithdraw.getUseAmt())) ? "0" : StringUtil.nvl(redBagWithdraw.getUseAmt());
									redBagWithdraw.setTotalConsumeAmt(new BigDecimal(totalConsumeAmt).add(new BigDecimal(withdrawAmt)).toString());
									redBagWithdraw.setUseAmt(new BigDecimal(useAmt).subtract(new BigDecimal(withdrawAmt)).toString());
									String withAmt = StringUtil.formatNumberToDecimals(withdrawAmt + "", 2);
									// 创建 提现订单
									AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
									AppRedBagWithdrawOrder withdrawOrder = new AppRedBagWithdrawOrder();
									withdrawOrder.setApprId(loanAppl.getId());
									withdrawOrder.setBankCode(loanCtm.getBank());
									withdrawOrder.setBankCard(loanCtm.getBankCard());
									withdrawOrder.setLoanStatus("0");
									withdrawOrder.setWithdrawAmt(withAmt);
									withdrawOrder.setWithdrawTime(new Date());
									appRedBagWithdrawOrderMapper.insertSelective(withdrawOrder);
									appRedBagMapper.updateByPrimaryKeySelective(redBagWithdraw);
									// 保存提现明细
									AppRedBagWithdrawLog appLog = new AppRedBagWithdrawLog();
									appLog.setAmt(withAmt);
									appLog.setApprId(loanAppl.getId());
									appLog.setTitle("提现中");
									appLog.setType("2");
									appLog.setCreateDate(new Date());
									appLog.setRedBagId(withdrawOrder.getId());
									appRedBagLogMapper.insertSelective(appLog);
									// 添加提醒消息
									AppMessage message = new AppMessage();
									message.setApprId(loanAppl.getId());
									message.setTitle("红包消息");
									message.setComtent("您当月已提现一笔" + withAmt + "元现金。");
									message.setMessageType("4");
									appMessageMapper.insertSelective(message);
									detail.put(Consts.RESULT_NOTE, "提现申请成功");
									// 发送短信
									/*List<Map<String, Object>> ifmBaseDicts = ifmBaseDictMapper.selectBaseDict("OUT_WEB_SMS");
									Map<String, Object> map = ifmBaseDicts.get(0);
									String url = StringUtil.toString(map.get("ITEM_VALUE"));
									Map<String, Object> m = new HashMap<String, Object>();
									m.put("withdrawAmt", withAmt);
									JSONObject object = PhoneValicodeUtil.sendMessage(loanAppl.getItemCode(), "49", url, m);
									if("false".equals(StringUtil.nvl(object.get("success")))){
										System.out.println("发送给：" + loanAppl.getItemCode() + "的红包提现消息失败！");
									}*/
								} else {
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "每月提现金额限定金额为" + itemValue + "元内。");
								}
							} else {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "您的红包金额低于可提现最低金额，请继续加油赚取更多红包");
							}
						}else{
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "您暂时还没有红包");
						}
					} else {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "提现金额最低" + minWithdraw + "元，请重新确认提现金额");
					}
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "未绑定到帐银行卡，请认证绑定。");
				}
			}
//			}else{
//				detail.put(Consts.RESULT, ErrorCode.FAILED);
//				detail.put(Consts.RESULT_NOTE, "当前账户未授信，请先授信");
//			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

}
