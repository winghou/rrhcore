package com.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bestsign.config.BestSignConfig;
import com.bestsign.config.EnumDpi;
import com.bestsign.model.ContractData;
import com.bestsign.model.YueCaiContract;
import com.bestsign.utils.NumberToCnUtils;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLevelMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppSSQContractMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPWithDrawAppl;
import com.model.AppLevel;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppSSQContract;
import com.model.AppUser;
import com.service.intf.AppBestSignService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.HttpClientUtil;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class AppBestSignServiceImpl implements AppBestSignService {
	
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLevelMapper appLevelMapper;
	@Autowired
	private AppSSQContractMapper appSSQContractMapper;
	@Autowired
	private APPWithDrawApplMapper appWithDrawApplMapper;
	/*** 上传并创建合同 ***/
	@Override
	public JSONObject uploadAndCreateContract(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String fname = JsonUtil.getJStringAndCheck(params, "fname", null, true, detail);
		String title = JsonUtil.getJStringAndCheck(params, "title", null, true, detail);
		String fpages = JsonUtil.getJStringAndCheck(params, "fpages", null, true, detail);
		String description = JsonUtil.getJStringAndCheck(params, "description", null, true, detail);
		String daiKouContractPath = JsonUtil.getJStringAndCheck(params, "daiKouContractPath", null, true, detail);
		// daiKouContractPath=daiKouContractPath.replace("\\", "\\\\");
		// YueCaiContract ycc=new
		// YueCaiContract("代扣授权书","代扣授权书","2","悦才代扣授权书合同","src\\main\\webapp\\contract\\代扣授权书.pdf");
		YueCaiContract ycc = new YueCaiContract(fname, title, fpages, description, daiKouContractPath);
		String contractId = BestSignConfig.getContractId(ycc);
		detail.put(Consts.RESULT, ErrorCode.SUCCESS);
		detail.put("contractId", contractId);
		return detail;
	}

	/** 生成用户签名/印章图片 暂时不用 **/
	@Override
	public JSONObject CreateSign() {
		return null;
	}

	/**
	 * 上传用户签名/印章图片 这个是盖章
	 * 
	 * @throws Exception
	 **/
	@Override
	public JSONObject uploadSign() throws Exception {
		JSONObject detail = new JSONObject();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("account", BestSignConfig.ACCOUNT);
		data.put("imageData", BestSignConfig.imageDataSign);
		data.put("imageName", BestSignConfig.signName);
		String postUrlByRsa = BestSignConfig.getPostUrlByRsa(data, BestSignConfig.uploadSign);
		String dataString = JSONObject.toJSONString(data);
		String doPostJson = HttpClientUtil.doPostJson(postUrlByRsa, dataString);
		JSONObject parseObject = JSON.parseObject(doPostJson);
		if (parseObject != null) {
			if (parseObject.get("errno") != null && "0".equals(parseObject.get("errno").toString())) {
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put(Consts.RESULT_NOTE, "上传成功");
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "上传失败");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "网络超时");
		}
		return detail;
	}

	/**
	 * 下载用户签名/印章图片
	 * 
	 * @throws Exception
	 **/
	@Override
	public String downLoadSign() throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		String urlByRsa = BestSignConfig.getUrlByRsa(BestSignConfig.ACCOUNT, BestSignConfig.signName, data,
				BestSignConfig.downLoadSign);
		return urlByRsa;
	}

	/** 上传合同文件 **/
	@Override
	public String uploadContract(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String fname = JsonUtil.getJStringAndCheck(params, "fname", null, true, detail);
		String title = JsonUtil.getJStringAndCheck(params, "title", null, true, detail);
		String fpages = JsonUtil.getJStringAndCheck(params, "fpages", null, true, detail);
		String description = JsonUtil.getJStringAndCheck(params, "description", null, true, detail);
		String daiKouContractPath = JsonUtil.getJStringAndCheck(params, "daiKouContractPath", null, true, detail);

		YueCaiContract ycc = new YueCaiContract(fname, title, fpages, description, daiKouContractPath);
		String fid = BestSignConfig.getFid(ycc);
		return fid;
	}

	/** 为PDF文件添加元素 产生新的文件编号 **/
	@Override
	public JSONObject addDataToPdf(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String fname = JsonUtil.getJStringAndCheck(params, "fname", null, true, detail);
		String title = JsonUtil.getJStringAndCheck(params, "title", null, true, detail);
		String fpages = JsonUtil.getJStringAndCheck(params, "fpages", null, true, detail);
		String description = JsonUtil.getJStringAndCheck(params, "description", null, true, detail);
		String daiKouContractPath = JsonUtil.getJStringAndCheck(params, "daiKouContractPath", null, true, detail);
		String elements = JsonUtil.getJStringAndCheck(params, "elements", null, true, detail);
		JSONArray elementsArray = JSONArray.parseArray(elements);
		YueCaiContract ycc = new YueCaiContract(fname, title, fpages, description, daiKouContractPath);
		String fid = BestSignConfig.getFid(ycc);

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("account", BestSignConfig.ACCOUNT);
		data.put("fid", fid);
		data.put("elements", elementsArray);
		String postUrlByRsa = BestSignConfig.getPostUrlByRsa(data, BestSignConfig.addDataToPdf);
		String dataString = JSONObject.toJSONString(data);
		String doPostJson = HttpClientUtil.doPostJson(postUrlByRsa, dataString);
		JSONObject parseObject = JSON.parseObject(doPostJson);
		if (parseObject != null) {
			if (parseObject.get("errno") != null && "0".equals(parseObject.get("errno").toString())) {
				String newfid = parseObject.getJSONObject("data").get("fid").toString();
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("newfid", newfid);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "添加元素失败");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "网络超时");
		}
		return detail;
	}

	/**
	 * 创建合同
	 * 
	 * @throws IOException
	 **/
	@Override
	public JSONObject createContract(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String fname = JsonUtil.getJStringAndCheck(params, "fname", null, true, detail);
		String title = JsonUtil.getJStringAndCheck(params, "title", null, true, detail);
		String fpages = JsonUtil.getJStringAndCheck(params, "fpages", null, true, detail);
		String description = JsonUtil.getJStringAndCheck(params, "description", null, true, detail);
		String daiKouContractPath = JsonUtil.getJStringAndCheck(params, "daiKouContractPath", null, true, detail);

		YueCaiContract ycc = new YueCaiContract(fname, title, fpages, description, daiKouContractPath);
		String fid = BestSignConfig.getFid(ycc);
		String expireTime = BestSignConfig.getExpireTime(6);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("account", BestSignConfig.ACCOUNT);
		data.put("fid", fid);
		data.put("expireTime", expireTime);
		data.put("title", title);
		data.put("description", description);

		String postUrlByRsa = BestSignConfig.getPostUrlByRsa(data, BestSignConfig.createContract);
		String dataString = JSONObject.toJSONString(data);
		String doPostJson = HttpClientUtil.doPostJson(postUrlByRsa, dataString);

		JSONObject parseObject = JSON.parseObject(doPostJson);
		if (parseObject != null) {
			if (parseObject.get("errno") != null && "0".equals(parseObject.get("errno").toString())) {
				String contractId = parseObject.getJSONObject("data").get("contractId").toString();
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("contractId", contractId);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "创建合同失败");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "网络超时");
		}
		return detail;
	}

	/**
	 * 签署并锁定合同
	 * 
	 * @throws IOException
	 **/
	// 可以越过“2.1、创建用户签名/印章图片”接口、或“2.2、上传用户签名/印章图片”接口，直接在此提供签名/印章图片即可
	@Override
	public JSONObject signAndLockContract(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String withdrawId = JsonUtil.getJStringAndCheck(params, "withdrawId", null, true, detail);
		String projectUrl = JsonUtil.getJStringAndCheck(params, "projectUrl", null, true, detail);
		
		APPWithDrawAppl withDrawAppl = appWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withdrawId));
		if(null != withDrawAppl){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByPrimaryKey(withDrawAppl.getApprId());
			AppSSQContract ssqContractBorrow = appSSQContractMapper.selectByWithdrawIdAndType(withDrawAppl.getId(), "0");
			AppSSQContract ssqContractwithhold = appSSQContractMapper.selectByWithdrawIdAndType(withDrawAppl.getId(), "1");
			if(ssqContractBorrow != null && ssqContractwithhold != null){
				String borrowContractId = ssqContractBorrow.getContractId(); //借款合同号
				String withholdContractId = ssqContractwithhold.getContractId(); //代扣合同号
				// 签署借款合同及代扣协议
				boolean borrowPersonResult = BestSignConfig.personAndCompanySign(borrowContractId, loanAppl.getItemCode(), 0, projectUrl);
				boolean borrowCompanyResult = BestSignConfig.personAndCompanySign(borrowContractId, BestSignConfig.ACCOUNT, 1, projectUrl);
				boolean withholdPersonResult = BestSignConfig.personAndCompanySign(withholdContractId, loanAppl.getItemCode(), 2, projectUrl);
				boolean withholdCompanyResult = BestSignConfig.personAndCompanySign(withholdContractId, BestSignConfig.ACCOUNT, 3, projectUrl);
				if(borrowPersonResult && borrowCompanyResult && withholdPersonResult && withholdCompanyResult){
					// 锁定并结束合同
					boolean borrowLockResult = BestSignConfig.lockContract(borrowContractId);
					boolean withholdLockResult = BestSignConfig.lockContract(withholdContractId);
					if(borrowLockResult && withholdLockResult){
						detail.put(Consts.RESULT, ErrorCode.SUCCESS);
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "锁定合同失败");
					}
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "签署合同失败");
				}
			}
		}
		return detail;
	}

	/**
	 * 获取预览页URL
	 * 
	 * @throws IOException
	 **/
	@Override
	public JSONObject getContractUrl(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String contractId = JsonUtil.getJStringAndCheck(params, "contractId", null, true, detail);
		String urlexpireTime = BestSignConfig.getExpireTime(6);// 预览URL链接的时间

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("account", BestSignConfig.ACCOUNT);
		data.put("contractId", contractId);
		data.put("dpi", EnumDpi.DPI_240.getCode());// 使用超清
		data.put("expireTime", urlexpireTime);
		String postUrlByRsa = BestSignConfig.getPostUrlByRsa(data, BestSignConfig.getContractUrl);
		String dataString = JSONObject.toJSONString(data);
		String doPostJson = HttpClientUtil.doPostJson(postUrlByRsa, dataString);

		JSONObject parseObject = JSON.parseObject(doPostJson);
		if (parseObject != null) {
			if (parseObject.get("errno") != null && "0".equals(parseObject.get("errno").toString())) {
				String url = parseObject.getJSONObject("data").get("url").toString();
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("url", url);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "预览失败");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "网络超时");
		}
		return detail;
	}

	/**
	 * 上传合同模板接口——为PDF添加元素接口——创建合同接口——签署合接口——锁定并结束合同接口——预览合同接口 一次性完成的接口，方便调试
	 * 
	 * @throws Exception
	 **/
	@Override
	public JSONObject fenQiContract(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String fname = JsonUtil.getJStringAndCheck(params, "fname", null, true, detail);
		String title = JsonUtil.getJStringAndCheck(params, "title", null, true, detail);
		String fpages = JsonUtil.getJStringAndCheck(params, "fpages", null, true, detail);
		String description = JsonUtil.getJStringAndCheck(params, "description", null, true, detail);
		String daiKouContractPath = JsonUtil.getJStringAndCheck(params, "daiKouContractPath", null, true, detail);
		String elements = JsonUtil.getJStringAndCheck(params, "elements", null, true, detail);
		String signaturePositions = JsonUtil.getJStringAndCheck(params, "signaturePositions", null, true, detail);

		JSONArray elementsArray = JSONArray.parseArray(elements);
		JSONArray signaturePositionsArray = JSONArray.parseArray(signaturePositions);
		/*** 上传合同--添加PDF元素--创建合同方法 ***/
		String contractId = uploadAndCreatefenQiContract(fname, title, fpages, description, daiKouContractPath,elementsArray);
		/** 签署并锁定合同同时生成预览URL方法 **/
		Boolean signAndLockAndScan = signAndLockAndScan(signaturePositionsArray, contractId);
		if(signAndLockAndScan) {
			String scanUrl = getScanUrl(contractId);
			detail.put(Consts.RESULT, ErrorCode.SUCCESS);
			detail.put("url", scanUrl);
		}

		return detail;
	}

	
	/**
	 * 上传并创建合同接口——签署合接口——锁定并结束合同接口——预览合同接口 一次性完成的接口，方便调试
	 **/
	@Override
	public JSONObject daiKouContract(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String fname = JsonUtil.getJStringAndCheck(params, "fname", null, true, detail);
		String title = JsonUtil.getJStringAndCheck(params, "title", null, true, detail);
		String fpages = JsonUtil.getJStringAndCheck(params, "fpages", null, true, detail);
		String description = JsonUtil.getJStringAndCheck(params, "description", null, true, detail);
		String daiKouContractPath = JsonUtil.getJStringAndCheck(params, "daiKouContractPath", null, true, detail);
		String signaturePositions = JsonUtil.getJStringAndCheck(params, "signaturePositions", null, true, detail);

		JSONArray signaturePositionsArray = JSONArray.parseArray(signaturePositions);

		/** 上传并创建合同 **/
		YueCaiContract ycc = new YueCaiContract(fname, title, fpages, description, daiKouContractPath);
		String contractId = BestSignConfig.getContractId(ycc);

		/** 签署并锁定合同同时生成预览URL方法 **/
		Boolean signAndLockAndScan = signAndLockAndScan(signaturePositionsArray, contractId);
		if(signAndLockAndScan) {
			String scanUrl = getScanUrl(contractId);
			detail.put(Consts.RESULT, ErrorCode.SUCCESS);
			detail.put("url", scanUrl);
		}
		return detail;
	}
	
	
	/*** 封装上传合同--添加PDF元素--创建合同方法 ***/
	public String uploadAndCreatefenQiContract(String fname, String title, String fpages, String description,
			String daiKouContractPath, JSONArray elementsArray) throws Exception {
		String contractId = null;
		YueCaiContract ycc = new YueCaiContract(fname, title, fpages, description, daiKouContractPath);
		/** 上传合同 **/
		String fid = BestSignConfig.getFid(ycc);
		// 文件编号不为空
		/** 添加PDF元素 **/
		if (StringUtils.isNotBlank(fid)) {
			Map<String, Object> data = new HashMap<String, Object>();

			data.put("account", BestSignConfig.ACCOUNT);
			data.put("fid", fid);
			data.put("elements", elementsArray);
			String postUrlByRsa = BestSignConfig.getPostUrlByRsa(data, BestSignConfig.addDataToPdf);
			String dataString = JSONObject.toJSONString(data);
			String doPostJson = HttpClientUtil.doPostJson(postUrlByRsa, dataString);
			JSONObject parseObject = JSON.parseObject(doPostJson);
			if (parseObject != null) {
				if (parseObject.get("errno") != null && "0".equals(parseObject.get("errno").toString())) {
					String newfid = parseObject.getJSONObject("data").get("fid").toString();
					/** 创建合同 **/
					if (StringUtils.isNotBlank(newfid)) {
						String expireTime = BestSignConfig.getExpireTime(180);
						Map<String, Object> data2 = new HashMap<String, Object>();
						data2.put("account", BestSignConfig.ACCOUNT);
						data2.put("fid", newfid);
						data2.put("expireTime", expireTime);
						data2.put("title", title);
						data2.put("description", description);

						String postUrlByRsa2 = BestSignConfig.getPostUrlByRsa(data2, BestSignConfig.createContract);
						String dataString2 = JSONObject.toJSONString(data2);
						String doPostJson2 = HttpClientUtil.doPostJson(postUrlByRsa2, dataString2);

						JSONObject parseObject2 = JSON.parseObject(doPostJson2);
						if (parseObject2 != null) {
							if (parseObject2.get("errno") != null && "0".equals(parseObject2.get("errno").toString())) {
								contractId = parseObject2.getJSONObject("data").get("contractId").toString();
							} else {
								throw new Exception("创建合同错误");
							}
						}
					}
				} else {
					throw new Exception("添加PDF元素错误");
				}
			}
		}
		return contractId;
	}



	/** 签署并锁定合同方法封装 **/
	public Boolean signAndLockAndScan(JSONArray signaturePositionsArray, String contractId) throws Exception {
		Boolean flag=false;
		if (StringUtils.isNotBlank(contractId)) {
			Map<String, Object> data3 = new HashMap<String, Object>();
			data3.put("contractId", contractId);
			data3.put("signer", BestSignConfig.ACCOUNT);
			data3.put("signaturePositions", signaturePositionsArray);
			data3.put("signatureImageData", BestSignConfig.imageDataSign);

			String postUrlByRsa3 = BestSignConfig.getPostUrlByRsa(data3, BestSignConfig.signContract);
			String dataString3 = JSONObject.toJSONString(data3);
			String doPostJson3 = HttpClientUtil.doPostJson(postUrlByRsa3, dataString3);

			JSONObject parseObject3 = JSON.parseObject(doPostJson3);
			if (parseObject3 != null) {
				if (parseObject3.get("errno") != null && "0".equals(parseObject3.get("errno").toString())) {
					// 签署成功
					/** 锁定并结束合同接口 **/
					Map<String, Object> data4 = new HashMap<String, Object>();

					data4.put("contractId", contractId);
					String postUrlByRsa4 = BestSignConfig.getPostUrlByRsa(data4, BestSignConfig.lockEndContract);
					String dataString4 = JSONObject.toJSONString(data4);
					String doPostJson4 = HttpClientUtil.doPostJson(postUrlByRsa4, dataString4);
					JSONObject parseObject4 = JSON.parseObject(doPostJson4);
					if (parseObject4 != null) {
						if (parseObject4.get("errno") != null && "0".equals(parseObject4.get("errno").toString())) {
							// 锁定完成
							flag=true;
						}
					}
				}
			}
		} else {
			throw new Exception("合同编号为空");
		}
		return flag;
	}

	/** 获取预览URL **/
	public String getScanUrl(String contractId) throws Exception {
		String url=null;
		Map<String, Object> data5 = new HashMap<String, Object>();
		String urlexpireTime = BestSignConfig.getExpireTime(6);
		data5.put("account", BestSignConfig.ACCOUNT);
		data5.put("contractId", contractId);
		data5.put("dpi", EnumDpi.DPI_240.getCode());// 使用超清
		data5.put("expireTime", urlexpireTime);// 预览合同链接的有效期
		String postUrlByRsa5 = BestSignConfig.getPostUrlByRsa(data5, BestSignConfig.getContractUrl);
		String dataString5 = JSONObject.toJSONString(data5);
		String doPostJson5 = HttpClientUtil.doPostJson(postUrlByRsa5, dataString5);

		JSONObject parseObject5 = JSON.parseObject(doPostJson5);
		if (parseObject5 != null) {
			if (parseObject5.get("errno") != null && "0".equals(parseObject5.get("errno").toString())) {
				url = parseObject5.getJSONObject("data").get("url").toString();
			}
		}
		return url;

	}

    /**
     * 借款页面预览合同
     * @throws Exception 
     */
	@Override
	public JSONObject viewBestSign(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String borrowPerion = JsonUtil.getJStringAndCheck(params, "borrowPerion", null, true, detail); //借款天数
		String borrowAmt = JsonUtil.getJStringAndCheck(params, "borrowAmt", null, true, detail); //借款金额
		String contractNo = JsonUtil.getJStringAndCheck(params, "contractNo", null, false, detail); //合同号
		String purposeCode = JsonUtil.getJStringAndCheck(params, "purposeCode", null, true, detail); //借款用途
		String protocolType = JsonUtil.getJStringAndCheck(params, "protocolType", null, true, detail); //借款用途 0:借款合同，1：代扣协议
		String projectUrl = JsonUtil.getJStringAndCheck(params, "projectUrl", null, false, detail); //项目路径
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			AppLevel appLevel = appLevelMapper.selectByLevel(StringUtil.nvl(appLoanAppl.getLevel()));
			if (null != appLevel) {
				String signContractNo = "";
				if("0".equals(protocolType)){ //借款合同
					String mchRate = appLevel.getMchRate();
					// 每天服务费
					String fwf = StringUtil.formatNumberToDecimals(new BigDecimal(borrowAmt).multiply(new BigDecimal(mchRate)).toString(), 2);
					String cutRate = StringUtil.nvl(Double.parseDouble(appLevel.getFwfRate()) * 100).substring(0, 2);
					// 应还款金额
					double interest = new BigDecimal(borrowAmt).multiply(new BigDecimal(mchRate)).multiply(new BigDecimal(borrowPerion)).doubleValue();
					String shouldPayAmt = StringUtil.formatNumberToDecimals(new BigDecimal(borrowAmt).add(new BigDecimal(interest)).doubleValue() + "", 2);
					// 获取逾期率
					JSONObject overDuleRate = ServiceProrocolServiceImpl.getOverDuleRate(ifmBaseDictMapper);
					String overdule3DayRate = StringUtil.nvl(overDuleRate.get("overdule3DayRate")); // 三天内逾期利率
					String overduleOver3DayRate = StringUtil.nvl(overDuleRate.get("overduleOver3DayRate")); // 超过三天逾期利率
					// 借款用途
					String purposeName = ServiceProrocolServiceImpl.getPurposeName(purposeCode, ifmBaseDictMapper);
					// PDF添加元素
					JSONArray elementsArray = getBorrowPdfElements(loanCtm.getCustomName(), loanCtm.getIdentityCard(),
							appLoanAppl.getItemCode(), loanCtm.getBankCard(), borrowAmt, shouldPayAmt, borrowPerion,
							purposeName, cutRate, fwf, contractNo, overdule3DayRate, overduleOver3DayRate, "");
					// 借款协议
					String borrowContractPath = getprotocalByType(projectUrl, 0);
					// 莫愁花分期合同编号
					signContractNo = uploadAndCreatefenQiContract("随心花分期服务协议", "随心花分期服务协议", "3", "随心花分期服务协议合同", borrowContractPath, elementsArray);
				}else if("1".equals(protocolType)){ //代扣协议
					// PDF添加元素
					JSONArray elementsArray = getWithholdPdfElements(loanCtm.getCustomName(), loanCtm.getIdentityCard(),
							appLoanAppl.getItemCode(), "");
					// 代扣协议
					String withholdContractPath = getprotocalByType(projectUrl, 1);
					signContractNo = uploadAndCreatefenQiContract("代扣授权书", "代扣授权书", "2", "悦才代扣授权书合同", withholdContractPath, elementsArray);
				}
				if(!"".equals(signContractNo) && !"".equals(StringUtil.nvl(getScanUrl(signContractNo)))){
					detail.put("viewContractUrl", getScanUrl(signContractNo));
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "合同查看失败，请重试！");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您当前需要授信，请完善资料授信！");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 为借款PDF添加元素
	 * @param userName 用户名
	 * @param identityNo 身份证
	 * @param phone 手机号
	 * @param bankCardNo 银行卡号
	 * @param borrowAmt 借款金额
	 * @param shouldPayAmt 应还款金额
	 * @param borrowPerion 借款天数
	 * @param purposeCode 借款用途
	 * @param cutRate 砍头息
	 * @param fwf 每日利息
	 * @param contractNo 合同号
	 * @param overdule3DayRate 3天内逾期利率
	 * @param overduleOver3DayRate 3天外逾期利率
	 * @param borrowSeals 借款公章base64串
	 * @return
	 */
	public static JSONArray getBorrowPdfElements(String userName, String identityNo, String phone, String bankCardNo,
			String borrowAmt, String shouldPayAmt, String borrowPerion, String purposeName, String cutRate, String fwf,
			String contractNo, String overdule3DayRate, String overduleOver3DayRate, String borrowSeals) {
		JSONArray elementsArray = new JSONArray();
		// PDF添加元素
		ContractData ctd = new ContractData();
		ctd.setCustomName(userName);
		ctd.setPhone(phone);
		ctd.setIdetityCard(identityNo);
		ctd.setBankCardNo(bankCardNo);
		if (!"".equals(StringUtil.nvl(borrowAmt))) {
			ctd.setBorrowAmt(NumberToCnUtils.number2CNMontrayUnit(new BigDecimal(borrowAmt)));// 转换大写金额
		}
		if (!"".equals(StringUtil.nvl(shouldPayAmt))) {
			ctd.setShouldPayAmt(NumberToCnUtils.number2CNMontrayUnit(new BigDecimal(shouldPayAmt)));// 转换大写金额
		}
		ctd.setBorrowPerion(borrowPerion);
		ctd.setPurposeName(purposeName);
		ctd.setServeRate(cutRate);
		ctd.setMchRate(fwf);
		if (!"".equals(StringUtil.nvl(contractNo))) {
			ctd.setContractNo("MCHHT" + contractNo);
		}
		ctd.setLoanDate(DateUtil.format(new Date(), "yyyy-MM-dd"));
		ctd.setRepayDate(DateUtil.format(DateUtil.addDays(new Date(), Integer.parseInt(borrowPerion)), "yyyy-MM-dd"));
		ctd.setOverdule3DayRate(overdule3DayRate);
		ctd.setOverduleOver3DayRate(overduleOver3DayRate);
		ctd.setBorrowOfficialSealsText(borrowSeals);

		String elementsjson = BestSignConfig.getBorrowPdfElements(ctd);
		elementsArray = JSONArray.parseArray(elementsjson);
		return elementsArray;
	}
	
	/**
	 * 为代扣PDF添加元素
	 * @param userName 用户名
	 * @param identityNo 身份证
	 * @param phone 手机号
	 * @param withholdSeals 代扣公章base64串
	 * @return
	 */
	public static JSONArray getWithholdPdfElements(String userName, String identityNo, String phone,String withholdSeals) {
		JSONArray elementsArray = new JSONArray();
		// PDF添加元素
		ContractData ctd = new ContractData();
		ctd.setCustomName(userName);
		ctd.setPhone(phone);
		ctd.setIdetityCard(identityNo);
		ctd.setWithholdOfficialSealsText(withholdSeals);

		String elementsjson = BestSignConfig.getWithholdPdfElements(ctd);
		elementsArray = JSONArray.parseArray(elementsjson);
		return elementsArray;
	}
	
	/**
	 * 根据类型获取借款合同，贷款协议，公章
	 * @param type 0：借款协议，1：代扣协议，2：公章
	 * @return
	 */
	public static String getprotocalByType(String projectUrl, int type) {
		String result = "";
		if(0 == type){
			// 借款协议路径
			String borrowContractPath = projectUrl + "/contract/莫愁花分期服务协议.pdf";
			result = borrowContractPath;
		}else if(1 == type){
			// 代扣协议路径
			String withholdContractPath = projectUrl + "/contract/代扣授权书.pdf";
			result = withholdContractPath;
		}else if(2 == type){
			// 公章
			String picBase64 = BestSignConfig.getSealsBase(projectUrl);
			result = picBase64;
		}
		return result;
	}

}
