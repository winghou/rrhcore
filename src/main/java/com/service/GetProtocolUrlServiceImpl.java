package com.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.APPContractTempleteMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppRedBagWithdrawMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPContractTemplete;
import com.model.APPWithDrawAppl;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppUser;
import com.service.intf.GetProtocolUrlService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
@Service
public class GetProtocolUrlServiceImpl implements GetProtocolUrlService {

	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private APPContractTempleteMapper appContractTempleteMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private APPWithDrawApplMapper appWithDrawApplMapper;
	@Override
	public JSONObject getProtocolUrl(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String protocolCode = JsonUtil.getJStringAndCheck(params, "protocolCode", null, true, detail);
		String withDrawId = JsonUtil.getJStringAndCheck(params, "withDrawId", null, false, detail);
		String borrowAmt = JsonUtil.getJStringAndCheck(params, "borrowAmt", null, false, detail);
		String borrowPerion = JsonUtil.getJStringAndCheck(params, "borrowPerion", null, false, detail);
		String purposeCode = JsonUtil.getJStringAndCheck(params, "purposeCode", null, false, detail);
		String token = JsonUtil.getJStringAndCheck(params, "token", null, false, detail);
		String insuranceType = JsonUtil.getJStringAndCheck(params, "insuranceType", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null == mch) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		String contractUrlYc = "";   //悦才服务协议
		String withHoldUrl = "";    //代扣协议
		String contractUrlYx = "";  //攸县服务协议
		String contractUrlRz = "";  //融资会员服务购买协议
		String contractUrlParameter = "borrowPerion="+borrowPerion+"&borrowAmt="+borrowAmt+"&purposeCode="+purposeCode+"&token="+token+"&userId="+userId+"&withdrawId="+withDrawId;
		String contractUrlParameter2 = "token="+token+"&userId="+userId+"&withdrawId="+withDrawId;
		String contractUrlParameterRz = "token="+token+"&withdrawId="+withDrawId+"&userId="+userId+"&protocolType="+protocolCode;
		List<Map<String,Object>> ifmBaseDictList = ifmBaseDictMapper.selectBaseDict("MCH_PROTOCOL");
		String protocolName= "";
		for(Map<String,Object> map : ifmBaseDictList){
			if(protocolCode.equals(map.get("ITEM_KEY"))){
				protocolName = StringUtil.nvl(map.get("ITEM_VALUE"));
			}
		}
		detail.put("protocolName", protocolName.replaceAll("《", "").replaceAll("》", ""));
		if("".equals(withDrawId)){
			List<APPContractTemplete> list = appContractTempleteMapper.selectTem();
			for (APPContractTemplete appContractTemplete : list) {
				if ("1".equals(appContractTemplete.getContractType())) {  
					contractUrlYx = appContractTemplete.getUrl();   //攸县分期服务协议
				}
				if("4".equals(appContractTemplete.getContractType())){
					withHoldUrl = appContractTemplete.getUrl();  //代扣协议
				}
				if("5".equals(appContractTemplete.getContractType())){
					contractUrlRz = appContractTemplete.getUrl(); //融资会员服务购买协议
				}
			}
			if("1".equals(protocolCode)){
				detail.put("protocolUrl", contractUrlYx+contractUrlParameter);
			}else if("2".equals(protocolCode)){
				detail.put("protocolUrl", withHoldUrl);
			}else if("3".equals(protocolCode)){
				detail.put("protocolUrl", contractUrlRz + contractUrlParameterRz);
			}
		}else{
			APPWithDrawAppl drawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withDrawId));
			List<APPContractTemplete> appContractTempletes = appContractTempleteMapper.selectTmpUrl(drawAppl.getContractTempid());
			for (APPContractTemplete appContractTemplete : appContractTempletes) {
				if ("3".equals(appContractTemplete.getContractType())) {
					contractUrlYc = appContractTemplete.getUrl();  //悦才分期服务协议
				}
				if("4".equals(appContractTemplete.getContractType())){
					withHoldUrl = appContractTemplete.getUrl();  //代扣协议
				}
				if("1".equals(appContractTemplete.getContractType())){
					contractUrlYx = appContractTemplete.getUrl();   //攸县分期服务协议
				}
				if("5".equals(appContractTemplete.getContractType())){
					contractUrlRz = appContractTemplete.getUrl(); //融资会员服务购买协议
				}
			}
			if("1".equals(protocolCode)){
				if("1".equals(drawAppl.getLoanSource())){
					detail.put("protocolUrl", contractUrlYx+contractUrlParameter2);
				}else{
					detail.put("protocolUrl", contractUrlYc+contractUrlParameter2);
				}
			}else if("2".equals(protocolCode)){
				detail.put("protocolUrl", withHoldUrl);
			}else if("3".equals(protocolCode)){
				detail.put("protocolUrl", contractUrlRz + contractUrlParameterRz);
			}
		}
		return detail;
	}
	
	/**
	 * @author yang.wu
	 * @Description:获取协议信息
	 * @date 2018年3月14日下午4:50:24    
	 * @see com.service.intf.GetProtocolUrlService#getProtocolInfo(com.alibaba.fastjson.JSONObject)
	 * @param params
	 * @return
	 * @throws Exception
	 */  
	@Override
	public JSONObject getProtocolInfo(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String withdrawId = JsonUtil.getJStringAndCheck(params, "withdrawId", null, false, detail);
		String protocolType = JsonUtil.getJStringAndCheck(params, "protocolType", null, false, detail);
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null == appUser) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		List<Map<String,Object>> serverNameList = ifmBaseDictMapper.selectBaseDict("MCH_RZ_SERVER_NAME");
		List<Map<String,Object>> signAddresslist = ifmBaseDictMapper.selectBaseDict("MCH_RZ_SIGN_ADDRESS");
		java.text.SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
		if("3".equals(protocolType)){  //融资会员服务购买协议
			if(!"".equals(withdrawId)){
				APPWithDrawAppl appWithDrawAppl = appWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withdrawId));
				double fwfRate = Double.parseDouble(appWithDrawAppl.getFwfRate());
				detail.put("userName", appLoanCtm.getCustomName());
				detail.put("idCard", appLoanCtm.getIdentityCard());
				detail.put("mobile", appLoanAppl.getItemCode());
				detail.put("serverName", serverNameList.get(0).get("ITEM_VALUE"));
				detail.put("signAddress", signAddresslist.get(0).get("ITEM_VALUE"));
				detail.put("signDate", formatter.format(appWithDrawAppl.getBorrowTime()));
				detail.put("cutRate", new BigDecimal(fwfRate*100).setScale(0,BigDecimal.ROUND_HALF_UP)+"%");
				detail.put("contractNo", "SXHHT"+appWithDrawAppl.getContract_no());
			}else{
				detail.put("userName", appLoanCtm.getCustomName());
				detail.put("idCard", appLoanCtm.getIdentityCard());
				detail.put("mobile", appLoanAppl.getItemCode());
				detail.put("serverName", serverNameList.get(0).get("ITEM_VALUE"));
				detail.put("signAddress", signAddresslist.get(0).get("ITEM_VALUE"));
				detail.put("signDate", "");
				detail.put("cutRate", "");
				detail.put("contractNo", "");
			}
		}
		return detail;
	}

}
