package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppAgreementPayBankMapper;
import com.dao.AppBankCardBinMapper;
import com.dao.AppCheckcardRecordMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppAgreementPayBank;
import com.model.AppBankCardBin;
import com.model.AppCheckcardRecord;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppPhoneValicode;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.AgreementToPayService;
import com.service.intf.GetValiCodeFromPhoneService;
import com.util.APIHttpClient;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class AgreementToPayServiceImpl implements AgreementToPayService {
	
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppCheckcardRecordMapper appCheckcardRecordMapper;
	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private AppBankCardBinMapper appBankCardBinMapper;
	@Autowired
	private GetValiCodeFromPhoneService getValiCodeFromPhoneService;
	@Autowired
	private AppAgreementPayBankMapper appAgreementPayBankMapper;
	
	/**
	 * @author zw 
	 * 协议支付预绑卡(预验卡 获取验证码)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject preBindingCard(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String bankCard = JsonUtil.getJStringAndCheck(params, "bankCard", null, true, detail);    //银行卡号
		String bankPhone = JsonUtil.getJStringAndCheck(params, "bankPhone", null, true, detail);    //银行卡绑定手机号
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, true, detail);    //验证码
		if(!"".equals(userId)){
			if("".equals(bankCard)||"".equals(bankPhone)){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "请完善以上所有信息后再次获取");
				return detail;
			}
			List<AppBankCardBin> appBankCardBinList = appBankCardBinMapper.selectByBankCode(bankCard.substring(0, 6),bankCard.substring(0, 8));
			if(appBankCardBinList.size() <= 0){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该银行卡不支持验证,请换一张");
				return detail;
			}
			String bankName = StringUtil.nvl(appBankCardBinList.get(0).getBank_name());
			AppUser appUser = appUserMapper.selectByMchVersion(userId);
			if (null != appUser) {
				AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
				if(null != appLoanAppl.getAccountStatus() && appLoanAppl.getAccountStatus() == 3){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "你的账户已关闭");
					return detail;
				}
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
				if("".equals(appLoanCtm.getIdentityCard())||"".equals(appLoanCtm.getCustomName())){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "请先进行授权");
					return detail;
				}
				
				List<AppCheckcardRecord> bankCardChecks = appCheckcardRecordMapper.selectAppCheckcardRecordThisDay(StringUtil.nvl(appLoanCtm.getIdentityCard()));
				int size = 0;
				int size1 = 0;
				if(bankCardChecks!=null&&bankCardChecks.size()>0){
					for (AppCheckcardRecord appCheckcardRecord : bankCardChecks) {
						if(!"2".equals(StringUtil.nvl(appCheckcardRecord.getExtCode()))){
							size++;
						}
						boolean f = false;
						if(appCheckcardRecord.getCheckTime()!=null&&!"".equals(appCheckcardRecord.getCheckTime())){
							f = DateUtil.format(appCheckcardRecord.getCheckTime(), "yyyy-MM-dd").equals(DateUtil.format(new Date(), "yyyy-MM-dd"));
						}
						if(f&&!"3".equals(StringUtil.nvl(appCheckcardRecord.getExtCode()))){
							size1++;
						}
					}
				}
				
				AppAgreementPayBank aPayBank = appAgreementPayBankMapper.selectByName(appBankCardBinList.get(0).getBank_name());
				if(aPayBank==null){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "该银行卡不支持验证");
					return detail;
				}
				if(!bankName.equals(appBankCardBinList.get(0).getBank_name())){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "开户行与银行卡不匹配");
					return detail;
				}
				
				
				Map<String , Object> map = new HashMap<>();
				map.put("phone", bankPhone);
				map.put("status", "9");
				AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map);
				if(appPhoneValicode!=null&&new Date().getTime()-appPhoneValicode.getCreatTime().getTime()<=60000){
					JSONObject jo = new JSONObject();
					jo.put("outOrderNo", appPhoneValicode.getValicode());
					jo.put("userId", userId);
					jo.put("bankPhone", bankPhone);
					jo.put("valiCode", valiCode);
					detail.putAll(preBindingCardPhoneValicode(jo));
				}else{
					List<IfmBaseDict> ifmBaseDicts = ifmBaseDictMapper.fetchDictsByType("CHECK_CARD_NUMBER");
					String num = StringUtil.nvl(ifmBaseDicts.get(0).getItemValue());
					if(size>=Integer.parseInt("".equals(StringUtil.nvl(num))?"3":StringUtil.nvl(num))){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您今日绑卡已达上线，请次日重新验证绑卡信息。");
						return detail;
					}
					if(size1>=5){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "验证码已达上限，请次日再试");
						return detail;
					}
					
					if(appPhoneValicode!=null){
						appPhoneValicodeMapper.deleteByPrimaryKey(appPhoneValicode.getId());
					}
					Map<String , Object> map2 = new HashMap<>();
					map2.put("phone", bankPhone);
					map2.put("status", "7");
					AppPhoneValicode appPhoneVali = appPhoneValicodeMapper.selectByPhoneAndStatus(map2);
					if(appPhoneVali!=null){
						appPhoneValicodeMapper.deleteByPrimaryKey(appPhoneVali.getId());
					}
					Map<String , Object> map1 = new HashMap<>();
					map1.put("phone", bankPhone);
					map1.put("status", "8");
					AppPhoneValicode appPhone = appPhoneValicodeMapper.selectByPhoneAndStatus(map1);
					if(appPhone!=null){
						appPhoneValicodeMapper.deleteByPrimaryKey(appPhone.getId());
					}
					AppCheckcardRecord appCheckcardRecord = new AppCheckcardRecord();
					appCheckcardRecord.setApprId(appLoanAppl.getId());
					appCheckcardRecord.setBankCard(bankCard);
					appCheckcardRecord.setIdCard(StringUtil.nvl(appLoanCtm.getIdentityCard()));
					appCheckcardRecord.setMobile(bankPhone);
					appCheckcardRecord.setName(StringUtil.nvl(appLoanCtm.getCustomName()));
					String uuid = StringUtil.nvl(UUID.randomUUID()).replace("-", "");
					appCheckcardRecord.setCheckTime(new Date());
					appCheckcardRecord.setUuid(uuid);
					List<IfmBaseDict> baseDicts = ifmBaseDictMapper.fetchDictsByType("AGREEMENT_TO_PAY_URL");
					String url = StringUtil.nvl(baseDicts.get(0).getItemValue());
					JSONObject jo = new JSONObject();
					jo.put("appr_id", StringUtil.nvl(appLoanAppl.getId()));//
					jo.put("bank_card", StringUtil.nvl(bankCard));//银行卡号
					jo.put("bank",  StringUtil.nvl(aPayBank.getBankCode()));//银行卡编码
					jo.put("identity_card",  StringUtil.nvl(appLoanCtm.getIdentityCard()));//身份证
					jo.put("user_name",  StringUtil.nvl(appLoanCtm.getCustomName()));//姓名
					jo.put("mobileno",  StringUtil.nvl(bankPhone));//手机号
					jo.put("signType", "3");//签约产品类型 (2：莫愁花协议支付,3：随心花协议支付，4：现金树协议支付)
					jo.put("bankCardType", "101");//银行卡类型 101 借记卡 102 信用卡
					JSONObject cut = new JSONObject();
					cut.put("cmd", "fastBoofSign");
					cut.put("token", "245Y7BSfDHIWEie34");
					cut.put("version", "1");
					cut.put("params", jo);
					String res = APIHttpClient.doPost(url+"/fastBoofSign", cut , 60000);
					if(res==null||"".equals(res)){
						appCheckcardRecord.setExtMessage("请求异常，请稍后重试(预绑卡失败)");
						appCheckcardRecord.setExtCode("3");
						appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "请求异常，请稍后重试");
					}else{
						JSONObject object = (JSONObject) JSON.parse(res);
						String result = StringUtil.nvl(object.get("result"));//0：失败;1成功
						String resultNote = StringUtil.nvl(object.get("resultNote"));//返回结果说明
						String det = StringUtil.nvl(object.get("detail"));//返回结果说明
						JSONObject deta = (JSONObject) JSON.parse(det);
						//绑卡订单号，确认绑卡时需要传入(//和ifm_yxt_sign表中merchOrderNo值一致 和ifm_yxt_withhold表中 merchSignOrderNo值一致)
						String merchOrderNo = StringUtil.nvl(deta.get("merchOrderNo"));
						if("1".equals(result)){
							appPhoneValicode = new AppPhoneValicode();
							appPhoneValicode.setCreatTime(new Date());
							appPhoneValicode.setPhone(bankPhone);
							appPhoneValicode.setValicode(merchOrderNo);
							appPhoneValicode.setStatus("9");
							appPhoneValicode.setCount(0);
							appPhoneValicodeMapper.insertSelective(appPhoneValicode);
							
							appPhoneValicode = new AppPhoneValicode();
							appPhoneValicode.setCreatTime(new Date());
							appPhoneValicode.setPhone(bankPhone);
							appPhoneValicode.setValicode(uuid);
							appPhoneValicode.setStatus("7");
							appPhoneValicode.setCount(0);
							appPhoneValicodeMapper.insertSelective(appPhoneValicode);
							
							appCheckcardRecord.setExtMessage(resultNote+"(预绑卡成功)");
							appCheckcardRecord.setExtCode("2");
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "请输入收到的验证码");
							detail.put("outOrderNo", uuid);
							appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
						}else if("5".equals(result)){//该银行卡已经绑过
							
							appPhoneValicode = new AppPhoneValicode();
							appPhoneValicode.setCreatTime(new Date());
							appPhoneValicode.setPhone(bankPhone);
							appPhoneValicode.setValicode(uuid);
							appPhoneValicode.setStatus("7");
							appPhoneValicode.setCount(0);
							appPhoneValicodeMapper.insertSelective(appPhoneValicode);
							
							appPhoneValicode = new AppPhoneValicode();
							appPhoneValicode.setCreatTime(new Date());
							appPhoneValicode.setPhone(bankPhone);
							appPhoneValicode.setValicode(merchOrderNo);
							appPhoneValicode.setStatus("9");
							appPhoneValicode.setCount(0);
							appPhoneValicodeMapper.insertSelective(appPhoneValicode);
							
							JSONObject jo1 = new JSONObject();
							jo1.put("phone", bankPhone);
							jo1.put("type", "8");
							JSONObject de = getValiCodeFromPhoneService.getValiCodeFromPhone(jo1);
							String res1 = StringUtil.nvl(de.get("result"));
							if("0".equals(res1)){
								appCheckcardRecord.setMobile(bankPhone);
								appCheckcardRecord.setCheckTime(new Date());
								appLoanCtm.setMerch_order_no(merchOrderNo);
								appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm);
								appCheckcardRecord.setExtMessage("已验证过此卡(预绑卡成功)");
								appCheckcardRecord.setExtCode("2");
								detail.put("outOrderNo", uuid);
								appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
								
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "请输入收到的验证码");
							}else{
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "操作频繁，请稍后重试");
							}
						}else{
							appCheckcardRecord.setExtMessage(resultNote+"(预绑卡失败)");
							appCheckcardRecord.setExtCode("3");
							appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, resultNote);
						}
					}
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

	 /**
     * @author zw 
     * 确认绑卡协议支付接口(验卡)
     * @param params
     * @return
     * @throws Exception
     */
	@Override
	public JSONObject preBindingCardPhoneValicode(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String bankPhone = JsonUtil.getJStringAndCheck(params, "bankPhone", null, false, detail);    //银行卡绑定手机号
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, false, detail);    //短信验证码
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		if("".equals(bankPhone)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请完善以上所有信息后再次绑定");
			return detail;
		}
		if("".equals(valiCode)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请输入短息验证码");
			return detail;
		}
		if(!"".equals(userId)){
			AppUser appUser = appUserMapper.selectByMchVersion(userId);
			if (null != appUser) {
				AppLoanAppl appl = appLoanApplMapper.selectByUserId(appUser.getUserid());
				AppLoanCtm ctm = appLoanCtmMapper.selectByapprId(appl.getId());
				
				JSONObject jo = new JSONObject();
				Map<String , Object> map = new HashMap<>();
				map.put("phone", bankPhone);
				map.put("status", "7");
				AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map);
				if(appPhoneValicode==null){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "短信验证码校验失败");
					return detail;
				}
				String outOrderNo = appPhoneValicode.getValicode();    //外部订单号
				AppCheckcardRecord bankCardCheck = appCheckcardRecordMapper.selectIfmBankCardCheckByUuid(outOrderNo);
				if(bankCardCheck == null){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "短信验证码校验失败");
					return detail;
				}
				//已验证过此卡
				Map<String , Object> map1 = new HashMap<>();
				map1.put("phone", bankPhone);
				map1.put("status", "8");
				AppPhoneValicode appPhone = appPhoneValicodeMapper.selectByPhoneAndStatus(map1);
				if(appPhone!=null){
					if(!valiCode.equals(appPhone.getValicode())){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "短信验证码校验失败");
						return detail;
					}
					if(new Date().getTime()-appPhone.getCreatTime().getTime()>180000){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "验证码超时");
						return detail;
					}
					bankCardCheck.setCheckTime(new Date());
					bankCardCheck.setExtCode("1");
					bankCardCheck.setExtMessage("已验证过此卡(绑卡成功)");
					appCheckcardRecordMapper.updateByPrimaryKeySelective(bankCardCheck);
					detail.put("outOrderNo", outOrderNo);
					Map<String , Object> map2 = new HashMap<>();
					map2.put("phone", bankPhone);
					map2.put("status", "7");
					AppPhoneValicode appPhoneVali = appPhoneValicodeMapper.selectByPhoneAndStatus(map2);
					if(appPhoneVali!=null){
						appPhoneValicodeMapper.deleteByPrimaryKey(appPhoneVali.getId());
					}
					Map<String , Object> map4 = new HashMap<>();
					map4.put("phone", bankPhone);
					map4.put("status", "9");
					AppPhoneValicode appPhoneVa = appPhoneValicodeMapper.selectByPhoneAndStatus(map4);
					if(appPhoneVa!=null){
						appPhoneValicodeMapper.deleteByPrimaryKey(appPhoneVa.getId());
					}
					appPhoneValicodeMapper.deleteByPrimaryKey(appPhone.getId());
					return detail;
				}
				
				List<AppCheckcardRecord> bankCardChecks = appCheckcardRecordMapper.selectAppCheckcardRecordThisDay(StringUtil.nvl(ctm.getIdentityCard()));
				int size = 0;
				if(bankCardChecks!=null&&bankCardChecks.size()>0){
					for (AppCheckcardRecord appCheckcardRecord : bankCardChecks) {
						if(!"2".equals(StringUtil.nvl(appCheckcardRecord.getExtCode()))){
							size++;
						}
					}
				}
				List<IfmBaseDict> ifmBaseDicts = ifmBaseDictMapper.fetchDictsByType("CHECK_CARD_NUMBER");
				String num = StringUtil.nvl(ifmBaseDicts.get(0).getItemValue());
				if(size>=Integer.parseInt("".equals(StringUtil.nvl(num))?"3":StringUtil.nvl(num))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您今日绑卡已达上线，请次日重新验证绑卡信息。");
					return detail;
				}

				Map<String , Object> map4 = new HashMap<>();
				map4.put("phone", bankPhone);
				map4.put("status", "9");
				AppPhoneValicode appPhoneVa = appPhoneValicodeMapper.selectByPhoneAndStatus(map4);
				if(appPhoneVa==null){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "短信验证码校验失败");
					return detail;
				}
				jo.put("merchOrderNo", StringUtil.nvl(appPhoneVa.getValicode()));//预绑卡请求成功以后返回的merchOrderNo值
				jo.put("signType", "3");//签约产品类型 (2：莫愁花协议支付,3：随心花协议支付，4：现金树协议支付)
				jo.put("authCode", valiCode);//短信验证码
				jo.put("appr_id", appl.getId()+"");//用户id
				JSONObject cut = new JSONObject();
				cut.put("cmd", "fastBoofSignConfirm");
				cut.put("token", "245Y7BSfDHIWEie34");
				cut.put("version", "1");
				cut.put("params", jo);
				List<IfmBaseDict> baseDicts = ifmBaseDictMapper.fetchDictsByType("AGREEMENT_TO_PAY_URL");
				String url = StringUtil.nvl(baseDicts.get(0).getItemValue());
				String res = APIHttpClient.doPost(url+"/fastBoofSignConfirm", cut , 60000);
				if(res==null||"".equals(res)){
					bankCardCheck.setCheckTime(new Date());
					bankCardCheck.setExtCode("0");
					bankCardCheck.setExtMessage("请求异常，请稍后重试(绑卡失败)");
					appCheckcardRecordMapper.updateByPrimaryKeySelective(bankCardCheck);
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "请求异常，请稍后重试");
				}else{
					JSONObject object = (JSONObject) JSON.parse(res);
					String result = StringUtil.nvl(object.get("result"));//0：失败;1成功
					String resultNote = StringUtil.nvl(object.get("resultNote"));//返回结果说明
					String det = StringUtil.nvl(object.get("detail"));//返回结果说明
					JSONObject deta = (JSONObject) JSON.parse(det);
					//绑卡订单号，确认绑卡时需要传入(//和ifm_yxt_sign表中merchOrderNo值一致 和ifm_yxt_withhold表中 merchSignOrderNo值一致)
					String merchOrderNo = StringUtil.nvl(deta.get("merchOrderNo"));//绑卡订单号，确认绑卡时需要传入
					bankCardCheck.setCheckTime(new Date());
					if(!"1".equals(result)){
						result = "0";
					}
					bankCardCheck.setExtCode(result);
					if("1".equals(result)){
						//删除验证码
						appPhoneValicodeMapper.deleteByPrimaryKey(appPhoneVa.getId());
						bankCardCheck.setExtMessage(resultNote+"(绑卡成功)");
						ctm.setMerch_order_no(merchOrderNo);
						appLoanCtmMapper.updateByPrimaryKeySelective(ctm);
						detail.put("outOrderNo", outOrderNo);
					}else{
						bankCardCheck.setExtMessage(resultNote+"(绑卡失败)");
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, resultNote);
					}
					appCheckcardRecordMapper.updateByPrimaryKeySelective(bankCardCheck);
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
     * @author zw 
     * 查询协议支付的银行
     * @param params
     * @return
     * @throws Exception
     */
	@Override
	public JSONObject queryAgreementPayBank(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		List<AppAgreementPayBank> list = appAgreementPayBankMapper.selectByAgreementPay();
		JSONArray supportBankCard = new JSONArray();
		for (AppAgreementPayBank map : list) {
			JSONObject bankList = new JSONObject();
			bankList.put("bankName", map.getBankName()); //银行卡名称
			bankList.put("bankCode", map.getBankCode()); //银行卡编号
			supportBankCard.add(bankList);
		}
		detail.put("supportBankCard",supportBankCard);
		return detail;
	}

}
