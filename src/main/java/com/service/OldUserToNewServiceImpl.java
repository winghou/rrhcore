package com.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppMessageMapper;
import com.dao.AppWithDrawLogMapper;
import com.model.APPCredit;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppMessage;
import com.model.AppWithDrawLog;
import com.service.intf.OldUserToNewService;
import com.util.DateUtil;
import com.util.InvitationCodeUtil;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class OldUserToNewServiceImpl implements OldUserToNewService {
	
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private APPWithDrawApplMapper appWithDrawApplMapper;
	@Autowired
	private AppWithDrawLogMapper appWithDrawLogMapper;
	@Autowired
	private APPCreditMapper appCreditMapper;
	@Autowired
	private APPPayPlanMapper appPayPlanMapper;
	@Autowired
	private AppMessageMapper appMessageMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject oldUserToNew(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String startId = JsonUtil.getJStringAndCheck(params, "startId", null, true, detail);  //起始apprId
		String endId = JsonUtil.getJStringAndCheck(params, "endId", null, true, detail);  //结束apprId
		
		int start_id = Integer.parseInt(startId);
		int end_id = Integer.parseInt(endId);
		
		if(start_id <= end_id){
//			AppLoanAppl loanAppl = null;
			AppLoanCtm loanCtm = null;
			APPWithDrawAppl withDrawAppl = null;
			List<APPWithDrawAppl> withDrawAppls = null;
			List<APPWithDrawAppl> signWithDrawAppls = null;
			List<APPWithDrawAppl> verifyWithDrawAppls = null;
			List<APPWithDrawAppl> overdueWithDrawAppls = null;
			List<APPPayPlan> payPlans = null;
			AppWithDrawLog withDrawLog = null;
			AppMessage appMessage = null;
			APPCredit appCredit = null;
			APPPayPlan appPayPlan = null;
			Map<String, Object> m = new HashMap<String,Object>();
			m.put("startId", start_id);
			m.put("endId", end_id);
			List<AppLoanAppl> loanAppls = appLoanApplMapper.selectByStartIdAndEndId(m);
			
			StringBuffer tmp = new StringBuffer();
			if(null != loanAppls && loanAppls.size() > 0){
				for(AppLoanAppl loanAppl : loanAppls){
					tmp.append(",").append(loanAppl.getId());
				}
			}
			String _appr_id = tmp.toString().substring(1);
			_appr_id = "("+_appr_id+")";
			//TODO 需要修改
			withDrawAppls = appWithDrawApplMapper.selectAllByapprId(1);
			if(null != withDrawAppls && withDrawAppls.size() > 0){
				int verifyOrders = 0; //审核中订单数
				int refuseOrders = 0; //拒绝订单数
				int signOrders = 0; //待签约的订单数
				int objectOrders = 0; //待发标订单数
				int overdueOrders = 0; //逾期订单数
				for(APPWithDrawAppl drawAppl : withDrawAppls){
					// 审核中订单
					if("0".equals(drawAppl.getStatus()) && "0".equals(drawAppl.getLoanStatus())){
						verifyOrders++;
					}
					// 拒绝订单
					if("4".equals(drawAppl.getStatus()) && "4".equals(drawAppl.getLoanStatus())){
						refuseOrders++;
					}
					// 待签约
					if("0".equals(drawAppl.getStatus()) && "5".equals(drawAppl.getLoanStatus())){
						signOrders++;
					}
					// 待发标
					if("0".equals(drawAppl.getStatus()) && "1".equals(drawAppl.getLoanStatus())){
						objectOrders++;
					}
					// 逾期
					if("2".equals(drawAppl.getStatus()) && "3".equals(drawAppl.getLoanStatus())){
						overdueOrders++;
					}
				}
			}
			
			if(null != loanAppls && loanAppls.size() > 0){
				for(AppLoanAppl loanAppl : loanAppls){
					int apprId = loanAppl.getId();
					withDrawAppls = appWithDrawApplMapper.selectAllByapprId(apprId);
					loanCtm = appLoanCtmMapper.selectByapprId(apprId);
					if(null != withDrawAppls && withDrawAppls.size() > 0){ // 有订单
						int verifyOrders = 0; //审核中订单数
						int refuseOrders = 0; //拒绝订单数
						int signOrders = 0; //待签约的订单数
						int objectOrders = 0; //待发标订单数
						int overdueOrders = 0; //逾期订单数
						for(APPWithDrawAppl drawAppl : withDrawAppls){
							// 审核中订单
							if("0".equals(drawAppl.getStatus()) && "0".equals(drawAppl.getLoanStatus())){
								verifyOrders++;
							}
							// 拒绝订单
							if("4".equals(drawAppl.getStatus()) && "4".equals(drawAppl.getLoanStatus())){
								refuseOrders++;
							}
							// 待签约
							if("0".equals(drawAppl.getStatus()) && "5".equals(drawAppl.getLoanStatus())){
								signOrders++;
							}
							// 待发标
							if("0".equals(drawAppl.getStatus()) && "1".equals(drawAppl.getLoanStatus())){
								objectOrders++;
							}
							// 逾期
							if("2".equals(drawAppl.getStatus()) && "3".equals(drawAppl.getLoanStatus())){
								overdueOrders++;
							}
						}
						Map<String, Object> map = null;
						// 用户没有审核通过的订单
						if(verifyOrders + refuseOrders >= withDrawAppls.size()){
							loanCtm.setSchedule_status("0");
							loanAppl.setStatus("0");
							loanAppl.setLevel("");
							map = new HashMap<String, Object>();
							map.put("apprId", apprId);
							map.put("status", "0");
							map.put("loanStatus", "0");
							verifyWithDrawAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
							if(null != verifyWithDrawAppls && verifyWithDrawAppls.size() > 0){
								for(APPWithDrawAppl verifyWithDrawAppl : verifyWithDrawAppls){
									verifyWithDrawAppl.setStatus("4");
									verifyWithDrawAppl.setLoanStatus("4");
									appWithDrawApplMapper.updateByPrimaryKeySelective(verifyWithDrawAppl);
									withDrawLog = new AppWithDrawLog();
									withDrawLog.setWithdrawId(verifyWithDrawAppl.getId());
									withDrawLog.setNodeContent("订单审核不通过");
									withDrawLog.setDetail("您的授信未通过，请重新授信");
									appWithDrawLogMapper.insertSelective(withDrawLog);
									appMessage = new AppMessage();
									appMessage.setApprId(apprId);
									appMessage.setMessageType("2");
									appMessage.setTitle("审核拒绝消息");
									appMessage.setComtent("您提交的" + verifyWithDrawAppl.getBorrowAmt() + "元借款申请失败，请重新前往授信！");
									appMessage.setWithdrawId(verifyWithDrawAppl.getId());
									appMessageMapper.insertSelective(appMessage);
									appCredit = appCreditMapper.selectByApprId(apprId);
									appCredit.setUseAmt(appCredit.getUseAmt() + Double.parseDouble(verifyWithDrawAppl.getBorrowAmt()));
									appCreditMapper.updateByPrimaryKeySelective(appCredit);
								}
							}
							appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
							appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
						}else{ //包含审核通过的订单
							// 有待签约订单
							if(signOrders > 0){
								map = new HashMap<String, Object>();
								map.put("apprId", apprId);
								map.put("status", "0");
								map.put("loanStatus", "5");
								signWithDrawAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
								for(APPWithDrawAppl signWithDrawAppl : signWithDrawAppls){
									signWithDrawAppl.setStatus("4");
									signWithDrawAppl.setLoanStatus("4");
									appWithDrawApplMapper.updateByPrimaryKeySelective(signWithDrawAppl);
									withDrawLog = new AppWithDrawLog();
									withDrawLog.setWithdrawId(signWithDrawAppl.getId());
									withDrawLog.setNodeContent("订单审核拒绝");
									withDrawLog.setDetail("订单无需签约，请重新提交订单");
									appWithDrawLogMapper.insertSelective(withDrawLog);
									appMessage = new AppMessage();
									appMessage.setApprId(apprId);
									appMessage.setMessageType("2");
									appMessage.setTitle("审核拒绝消息");
									appMessage.setComtent("好消息！随心花已经升级改造啦,你的账户已开通一键借款，无需签约,轻松秒到账！");
									appMessage.setWithdrawId(signWithDrawAppl.getId());
									appMessageMapper.insertSelective(appMessage);
									appCredit = appCreditMapper.selectByApprId(apprId);
									appCredit.setUseAmt(appCredit.getUseAmt() + Double.parseDouble(signWithDrawAppl.getBorrowAmt()));
									appCreditMapper.updateByPrimaryKeySelective(appCredit);
								}
							}
							// 为用户创建额度和等级
							boolean flag0 = false;
							if(overdueOrders > 0){
								map = new HashMap<String, Object>();
								map.put("apprId", apprId);
								map.put("status", "2");
								map.put("loanStatus", "3");
								overdueWithDrawAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
								withDrawAppl = overdueWithDrawAppls.get(overdueWithDrawAppls.size() - 1);
								payPlans = appPayPlanMapper.selectAllByWithDrawId(withDrawAppl.getId());
								if(null != payPlans && payPlans.size() > 0){
									appPayPlan = payPlans.get(payPlans.size() - 1);
									if(DateUtil.getDaysBetween(appPayPlan.getRepayDate(), new Date()) <= 3){
										flag0 = true;
									}
								}
							}
							// 欺诈订单用户
							boolean flag1 = false;
							map = new HashMap<String, Object>();
							map.put("apprId", apprId);
							map.put("repayMiss", "欺诈");
							List<Map<String, Object>> maps = appLoanApplMapper.selectOverPhoneByApprIdAndMiss(map);
							if(null == maps || maps.size() <= 0){
								flag1 = true;
							}
							if(!(overdueOrders > 0 || !flag0 || !flag1) && "C".equals(StringUtil.nvl(loanAppl.getLevel()))){
								appCredit = appCreditMapper.selectByApprId(apprId);
								if(null != appCredit){
									if(new BigDecimal(appCredit.getCreditAmt()).compareTo(new BigDecimal(0))  == 1 && new BigDecimal(appCredit.getCreditAmt()).compareTo(new BigDecimal(1200)) != 1) {
										// 提升至B等级，授信额度提升为1300
										loanAppl.setStatus("2");
										loanAppl.setAuthenTime(new Date());
										loanAppl.setCreditAmt("1300.00");
										appCredit.setCreditAmt(1300.00);
										appCredit.setCreditTime(new Date());
										appCredit.setUseAmt(new BigDecimal(1300).subtract(new BigDecimal(appCredit.getCreditAmt())).add(new BigDecimal(appCredit.getUseAmt())).doubleValue());
										loanCtm.setOperatorTime(new Date());
										appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
										appCreditMapper.updateByPrimaryKeySelective(appCredit);
										appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
									}
								}
							}else {
								// 提升等级和额度
								appCredit = appCreditMapper.selectByApprId(apprId);
								if(null != appCredit){
									int creditAmt = new Double(appCredit.getCreditAmt()).intValue();
									if(creditAmt >= 300 && creditAmt <= 500) {
										loanAppl.setLevel("D");
									}
									if(creditAmt >= 600 && creditAmt <= 1200) {
										loanAppl.setLevel("C");
									}
									if(creditAmt >= 1300 && creditAmt <= 2500) {
										loanAppl.setLevel("B");
									}
									if(creditAmt >= 2600 && creditAmt <= 4000) {
										loanAppl.setLevel("A");
									}
									loanAppl.setStatus("2");
									loanAppl.setAuthenTime(new Date());
									appCredit.setCreditTime(new Date());
									loanCtm.setOperatorTime(new Date());
									appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
									appCreditMapper.updateByPrimaryKeySelective(appCredit);
									appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
								}
							}
						}
					}else{ //无订单,资料回退
						loanCtm.setSchedule_status("0");
						loanAppl.setStatus("0");
						loanAppl.setLevel("");
						appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
						appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
					}
				}
			}
		}
		return null;
//			for(int apprId = start_id; apprId <= end_id; apprId++){
//				loanAppl = appLoanApplMapper.selectByPrimaryKey(apprId);
//				if(null != loanAppl){
//					withDrawAppls = appWithDrawApplMapper.selectAllByapprId(apprId);
//					loanCtm = appLoanCtmMapper.selectByapprId(apprId);
//					if(null != withDrawAppls && withDrawAppls.size() > 0){ // 有订单
//						int verifyOrders = 0; //审核中订单数
//						int refuseOrders = 0; //拒绝订单数
//						int signOrders = 0; //待签约的订单数
//						int objectOrders = 0; //待发标订单数
//						int overdueOrders = 0; //逾期订单数
//						for(APPWithDrawAppl drawAppl : withDrawAppls){
//							// 审核中订单
//							if("0".equals(drawAppl.getStatus()) && "0".equals(drawAppl.getLoanStatus())){
//								verifyOrders++;
//							}
//							// 拒绝订单
//							if("4".equals(drawAppl.getStatus()) && "4".equals(drawAppl.getLoanStatus())){
//								refuseOrders++;
//							}
//							// 待签约
//							if("0".equals(drawAppl.getStatus()) && "5".equals(drawAppl.getLoanStatus())){
//								signOrders++;
//							}
//							// 待发标
//							if("0".equals(drawAppl.getStatus()) && "1".equals(drawAppl.getLoanStatus())){
//								objectOrders++;
//							}
//							// 逾期
//							if("2".equals(drawAppl.getStatus()) && "3".equals(drawAppl.getLoanStatus())){
//								overdueOrders++;
//							}
//						}
//						Map<String, Object> map = null;
//						// 用户没有审核通过的订单
//						if(verifyOrders + refuseOrders >= withDrawAppls.size()){
//							loanCtm.setSchedule_status("0");
//							loanAppl.setStatus("0");
//							loanAppl.setLevel("");
//							map = new HashMap<String, Object>();
//							map.put("apprId", apprId);
//							map.put("status", "0");
//							map.put("loanStatus", "0");
//							verifyWithDrawAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
//							if(null != verifyWithDrawAppls && verifyWithDrawAppls.size() > 0){
//								for(APPWithDrawAppl verifyWithDrawAppl : verifyWithDrawAppls){
//									verifyWithDrawAppl.setStatus("4");
//									verifyWithDrawAppl.setLoanStatus("4");
//									appWithDrawApplMapper.updateByPrimaryKeySelective(verifyWithDrawAppl);
//									withDrawLog = new AppWithDrawLog();
//									withDrawLog.setWithdrawId(verifyWithDrawAppl.getId());
//									withDrawLog.setNodeContent("订单审核不通过");
//									withDrawLog.setDetail("您的授信未通过，请重新授信");
//									appWithDrawLogMapper.insertSelective(withDrawLog);
//									appMessage = new AppMessage();
//									appMessage.setApprId(apprId);
//									appMessage.setMessageType("2");
//									appMessage.setTitle("审核拒绝消息");
//									appMessage.setComtent("您提交的" + verifyWithDrawAppl.getBorrowAmt() + "元借款申请失败，请重新前往授信！");
//									appMessage.setWithdrawId(verifyWithDrawAppl.getId());
//									appMessageMapper.insertSelective(appMessage);
//									appCredit = appCreditMapper.selectByApprId(apprId);
//									appCredit.setUseAmt(appCredit.getUseAmt() + Double.parseDouble(verifyWithDrawAppl.getBorrowAmt()));
//									appCreditMapper.updateByPrimaryKeySelective(appCredit);
//								}
//							}
//							appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
//							appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//						}else{ //包含审核通过的订单
//							// 有待签约订单
//							if(signOrders > 0){
//								map = new HashMap<String, Object>();
//								map.put("apprId", apprId);
//								map.put("status", "0");
//								map.put("loanStatus", "5");
//								signWithDrawAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
//								for(APPWithDrawAppl signWithDrawAppl : signWithDrawAppls){
//									signWithDrawAppl.setStatus("4");
//									signWithDrawAppl.setLoanStatus("4");
//									appWithDrawApplMapper.updateByPrimaryKeySelective(signWithDrawAppl);
//									withDrawLog = new AppWithDrawLog();
//									withDrawLog.setWithdrawId(signWithDrawAppl.getId());
//									withDrawLog.setNodeContent("订单审核拒绝");
//									withDrawLog.setDetail("订单无需签约，请重新提交订单");
//									appWithDrawLogMapper.insertSelective(withDrawLog);
//									appMessage = new AppMessage();
//									appMessage.setApprId(apprId);
//									appMessage.setMessageType("2");
//									appMessage.setTitle("审核拒绝消息");
//									appMessage.setComtent("好消息！小莫已经升级改造啦,你的账户已开通一键借款，无需签约,轻松秒到账！");
//									appMessage.setWithdrawId(signWithDrawAppl.getId());
//									appMessageMapper.insertSelective(appMessage);
//									appCredit = appCreditMapper.selectByApprId(apprId);
//									appCredit.setUseAmt(appCredit.getUseAmt() + Double.parseDouble(signWithDrawAppl.getBorrowAmt()));
//									appCreditMapper.updateByPrimaryKeySelective(appCredit);
//								}
//							}
//							// 为用户创建额度和等级
//							boolean flag0 = false;
//							if(overdueOrders > 0){
//								map = new HashMap<String, Object>();
//								map.put("apprId", apprId);
//								map.put("status", "2");
//								map.put("loanStatus", "3");
//								overdueWithDrawAppls = appWithDrawApplMapper.selectByApprIdAndStatusAndLoanStatus(map);
//								withDrawAppl = overdueWithDrawAppls.get(overdueWithDrawAppls.size() - 1);
//								payPlans = appPayPlanMapper.selectAllByWithDrawId(withDrawAppl.getId());
//								if(null != payPlans && payPlans.size() > 0){
//									appPayPlan = payPlans.get(payPlans.size() - 1);
//									if(DateUtil.getDaysBetween(appPayPlan.getRepayDate(), new Date()) <= 3){
//										flag0 = true;
//									}
//								}
//							}
//							// 欺诈订单用户
//							boolean flag1 = false;
//							map = new HashMap<String, Object>();
//							map.put("apprId", apprId);
//							map.put("repayMiss", "欺诈");
//							List<Map<String, Object>> maps = appLoanApplMapper.selectOverPhoneByApprIdAndMiss(map);
//							if(null == maps || maps.size() <= 0){
//								flag1 = true;
//							}
//							if(!(overdueOrders > 0 || !flag0 || !flag1) && "C".equals(StringUtil.nvl(loanAppl.getLevel()))){
//								appCredit = appCreditMapper.selectByApprId(apprId);
//								if(null != appCredit){
//									if(new BigDecimal(appCredit.getCreditAmt()).compareTo(new BigDecimal(0))  == 1 && new BigDecimal(appCredit.getCreditAmt()).compareTo(new BigDecimal(1200)) != 1) {
//										// 提升至B等级，授信额度提升为1300
//										loanAppl.setStatus("2");
//										loanAppl.setAuthenTime(new Date());
//										loanAppl.setCreditAmt("1300.00");
//										appCredit.setCreditAmt(1300.00);
//										appCredit.setCreditTime(new Date());
//										appCredit.setUseAmt(new BigDecimal(1300).subtract(new BigDecimal(appCredit.getCreditAmt())).add(new BigDecimal(appCredit.getUseAmt())).doubleValue());
//										loanCtm.setOperatorTime(new Date());
//										appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//										appCreditMapper.updateByPrimaryKeySelective(appCredit);
//										appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
//									}
//								}
//							}else {
//								// 提升等级和额度
//								appCredit = appCreditMapper.selectByApprId(apprId);
//								if(null != appCredit){
//									int creditAmt = new Double(appCredit.getCreditAmt()).intValue();
//									if(creditAmt >= 300 && creditAmt <= 500) {
//										loanAppl.setLevel("D");
//									}
//									if(creditAmt >= 600 && creditAmt <= 1200) {
//										loanAppl.setLevel("C");
//									}
//									if(creditAmt >= 1300 && creditAmt <= 2500) {
//										loanAppl.setLevel("B");
//									}
//									if(creditAmt >= 2600 && creditAmt <= 4000) {
//										loanAppl.setLevel("A");
//									}
//									loanAppl.setStatus("2");
//									loanAppl.setAuthenTime(new Date());
//									appCredit.setCreditTime(new Date());
//									loanCtm.setOperatorTime(new Date());
//									appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//									appCreditMapper.updateByPrimaryKeySelective(appCredit);
//									appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
//								}
//							}
//						}
//					}else{ //无订单,资料回退
//						loanCtm.setSchedule_status("0");
//						loanAppl.setStatus("0");
//						loanAppl.setLevel("");
//						appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
//						appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//					}
//				}
//			}
//		}
//		return null;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject createInviteCode(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String startId = JsonUtil.getJStringAndCheck(params, "startId", null, true, detail);  //起始apprId
		String endId = JsonUtil.getJStringAndCheck(params, "endId", null, true, detail);  //结束apprId
		int start_id = Integer.parseInt(startId);
		int end_id = Integer.parseInt(endId);
		
		if(start_id <= end_id){
			System.out.println("生成邀请码开始===============");
			int num = 0;
			long t1 = System.currentTimeMillis();
			Map<String, Object> map = new HashMap<>();
			map.put("startId", start_id);
			map.put("endId", end_id);
			List<AppLoanAppl> loanAppls = appLoanApplMapper.selectByStartIdAndEndId(map);
			if(null != loanAppls && loanAppls.size() > 0){
				for(AppLoanAppl appl : loanAppls){
					if(null == appl.getInviteCode()){
						appl.setInviteCode(InvitationCodeUtil.toSerialCode((long)appl.getUserId()));
						appLoanApplMapper.updateByPrimaryKeySelective(appl);
						num++;
					}
				}
//				num = appLoanApplMapper.updateUserInviteCode(loanAppls);
			}
//			for(int apprId = start_id; apprId <= end_id; apprId++){
//				loanAppl = appLoanApplMapper.selectByPrimaryKey(apprId);
//				if(null != loanAppl && null == loanAppl.getInviteCode()){
//					int userId = loanAppl.getUserId();
//					String inviteCode = InvitationCodeUtil.toSerialCode((long)userId);  //邀请码
//					loanAppl.setInviteCode(inviteCode);
//					appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//					num++;
//				}
//			}
			System.out.println("生成邀请码结束===============");
			long t2 = System.currentTimeMillis();
			System.out.println("用时" + (t2 - t1) + "毫秒");
			System.out.println("更新了" + num + "条");
		}
		
		
		
		
		return null;
	}

}
