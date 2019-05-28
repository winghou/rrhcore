package com.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.domain.IvsDetail;
import com.antgroup.zmxy.openplatform.api.domain.ZmWatchListDetail;
import com.antgroup.zmxy.openplatform.api.domain.ZmWatchListExtendInfo;
import com.antgroup.zmxy.openplatform.api.request.*;
import com.antgroup.zmxy.openplatform.api.response.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppUserMapper;
import com.dao.AppZhimaScoreMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppUser;
import com.model.AppZhimaScore;
import com.service.intf.DecisionEngineService;
import com.service.intf.ZhiMaCreditService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

import javax.crypto.BadPaddingException;

@Service
public class ZhiMaCreditServiceImpl implements ZhiMaCreditService {

	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppZhimaScoreMapper ifmZhimaScoreMapper;
	@Autowired
	private DecisionEngineService decisionEngineService;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject authorizeQry(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(mch.getUserName());
		if(null != appLoanAppl.getAccountStatus() && 3 == appLoanAppl.getAccountStatus()){ //账户关闭
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
			return detail;
		} else {
			AppLoanCtm ApploanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			if (Integer.parseInt(ApploanCtm.getSchedule_status()) < 6) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "请先进行联系人认证");
				return detail;
			}
	//		Map<String, Object> ifmzhimaScore = ifmZhimaScoreMapper.selectByUserId(Integer.parseInt(userId));
			/*Map<String, Object> m = new HashMap<String, Object>();
			m.put("userId", userId);
			m.put("status", "1");
			List<AppZhimaScore> ifmzhimaScore = ifmZhimaScoreMapper.selectByUserIdAndStatus(m);*/
			AppZhimaScore ifmzhimaScore= ifmZhimaScoreMapper.selectZhimaScoreByUserId(Integer.parseInt(userId));
			if (null == ifmzhimaScore ||"1".equals(ifmzhimaScore.getStatus())) {

			    Map<String, Object> map = ifmBaseDictMapper.selectZhiMaCfg("1");
				// 芝麻开放平台地址
				String gatewayUrl = StringUtil.toString(map.get("gateway_url"));
				// 商户应用 Id
				String appId = StringUtil.toString(map.get("app_id"));
				// 商户 RSA 私钥
				String privateKey = StringUtil.toString(map.get("private_key"));
				// 芝麻 RSA 公钥
				String zhimaPublicKey = StringUtil.toString(map.get("zhima_public_key"));

				String name = JsonUtil.getJStringAndCheck(params, "name", null, false, detail);
				String certNo = JsonUtil.getJStringAndCheck(params, "certNo", null, false, detail);
				if (detail.containsKey(Consts.RESULT)) {
					return detail;
				}

//				ZhimaCustomerCertificationInitializeRequest request = new ZhimaCustomerCertificationInitializeRequest();
//				request.setChannel("apppc");
//				request.setPlatform("zmop");
//				long timeNow = System.currentTimeMillis();
//				String longTime = String.valueOf(timeNow);
//				if(longTime.length() <= 17){
//					timeNow = timeNow * (int)Math.pow(10,17-longTime.length());
//				}
//				String timer = String.valueOf(timeNow);
//				Random rm = new Random();
//				DecimalFormat df=new DecimalFormat("00000000000000");
//				int randomStr = (int)((1+rm.nextDouble())*Math.pow(10,13));
//				String radomNum = df.format(randomStr).substring(1,14);
////				request.setTransactionId(timer+radomNum);// 必要
//				String uuid = UUID.randomUUID().toString().replaceAll("-","");
//				request.setTransactionId(uuid);
//				request.setProductCode("w1010100000000002978");// 必要参数
//				request.setBizCode("FACE");// 必要参数
//				request.setIdentityParam("{\"identity_type\":\"CERT_INFO\",\"cert_type\":\"IDENTITY_CARD\",\"cert_name\":\"" + name + "\",\"cert_no\":\"" + certNo + "\"}");// 必要参数
//				request.setMerchantConfig("{\"need_user_authorization\":\"false\"}");//
//				request.setExtBizParam("{}");// 必要参数
//				DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
//				String bizNo = null;
//				try {
//					ZhimaCustomerCertificationInitializeResponse response = (ZhimaCustomerCertificationInitializeResponse) client.execute(request);
//					String url = client.generatePageRedirectInvokeUrl(request);
//					List<NameValuePair> pars = URLEncodedUtils.parse(url, Charset.forName("UTF-8"));
//					for (NameValuePair nr : pars) {
//						if ("sign".equals(nr.getName())) {
//							detail.put("sign", URLEncoder.encode(nr.getValue(), "utf-8"));
//						}
//						if ("params".equals(nr.getName())) {
//							detail.put("params", URLEncoder.encode(nr.getValue(), "utf-8"));
//						}
//					}
//					if (null != response && response.isSuccess()) {
//						bizNo =response.getBizNo();
//					}
//				} catch (ZhimaApiException e) {
//					e.printStackTrace();
//				}
//				detail.put("biz_no",bizNo);

				ZhimaAuthInfoAuthorizeRequest req = new ZhimaAuthInfoAuthorizeRequest();
				req.setChannel("apppc");
				req.setPlatform("zmop");
				req.setIdentityType("2");// 必要参数
				req.setIdentityParam("{\"name\":\""+ name +"\",\"certType\":\"IDENTITY_CARD\",\"certNo\":\"" + certNo + "\"}");// 必要参数
				req.setBizParams("{\"auth_code\":\"M_H5\",\"channelType\":\"app\",\"state\":\"商户自定义\"}");//
				DefaultZhimaClient zhimaClient = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
				String url = zhimaClient.generatePageRedirectInvokeUrl(req);
				String param = null, sign = null,charset = null;
				String[] urlParams = url.split("&");
//				for(int i = 0; i < urlParams.length; i++){
//					if(urlParams[i].startsWith("params=")){
//						param = urlParams[i].substring(7);
//						param = param.replaceAll(" ","+");
//					}else if(urlParams[i].startsWith("sign=")){
//						sign = urlParams[i].substring(5);
//						sign = sign.replaceAll(" ","+");
//					}else if(urlParams[i].indexOf("charset=") != -1){
//						charset = urlParams[i].substring(urlParams[i].indexOf("charset=")+8);
//					}
//				}
				List<NameValuePair> pars = URLEncodedUtils.parse(url, Charset.forName("UTF-8"));
				for (NameValuePair nr : pars) {
				      if ("sign".equals(nr.getName())) {
				      sign=URLEncoder.encode(nr.getValue(), "utf-8");
				      }
				      if ("params".equals(nr.getName())) {
				       param=URLEncoder.encode(nr.getValue(), "utf-8");
				      }
				     }

				
				if(param.indexOf("%") != -1) {
					param = URLDecoder.decode(param, "utf-8");
				}
				if(sign.indexOf("%") != -1) {
					sign = URLDecoder.decode(sign, "utf-8");
				}
				DefaultZhimaClient decodePwdClient = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
				detail.put("return_url","http://api.xiaoyuedai.com/zhima");
				detail.put("url",url);
				detail.put("param",param);
				detail.put("sign",sign);
				try{
					String result = decodePwdClient.decryptAndVerifySign(param,sign);
					detail.put("decodeResult",result);
				}catch(Exception e){
					System.out.println(e);
					detail.put(Consts.RESULT, "0");
					detail.put("decodeResult","解密验签失败");
				}
			} else {
				if(!"8".equals(appLoanAppl.getStatus()) && Integer.parseInt(ApploanCtm.getSchedule_status()) <= 7){
					ApploanCtm.setSchedule_status("7");
					appLoanCtmMapper.updateByPrimaryKeySelective(ApploanCtm);
				}
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "芝麻信用已经认证");
			}
		}
		return detail;
			
		
				
				
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject zhimaCallBack(JSONObject param) throws Exception {
		Map<String, Object> map = ifmBaseDictMapper.selectZhiMaCfg("1");
		// 芝麻开放平台地址
		String gatewayUrl = StringUtil.toString(map.get("gateway_url"));
		// 商户应用 Id
		String appId = StringUtil.toString(map.get("app_id"));
		// 商户 RSA 私钥
		String privateKey = StringUtil.toString(map.get("private_key"));
		// 芝麻 RSA 公钥
		String zhimaPublicKey = StringUtil.toString(map.get("zhima_public_key"));

		JSONObject detail = new JSONObject();
		String params = JsonUtil.getJStringAndCheck(param, "params", null, false, detail);
		params = params.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
		try {
			params = URLDecoder.decode(params, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		String sign = JsonUtil.getJStringAndCheck(param, "sign", null, false, detail);
		sign = sign.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
		try {
			sign = URLDecoder.decode(sign, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String userId = JsonUtil.getJStringAndCheck(param, "userId", null, true, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录,请先登录");
			return detail;
		}
		userId = mch.getUserid() + "";
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if(null == appUser){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "该用户未注册");
			return detail;
		}
		AppLoanAppl appl = appLoanApplMapper.selectByitemCode(appUser.getUserName());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
		DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
		try {
	        String strNew1 = params.replace(" ", "+");
	        String strNew2 = sign.replace(" ", "+");
	        
			String jm = client.decryptAndVerifySign(strNew1, strNew2);
			String[] s = jm.split("&");
			String open_id = "";
			for (String s1 : s) {
				String[] str = s1.split("=");
				if (str[0].equals("open_id")) {
					detail.put("open_id", str[1]);
					open_id = str[1];
					break;
				}
			}
			if (!"".equals(open_id)) {
				AppZhimaScore ifmZhimaScore = ifmZhimaScoreMapper.selectZhimaScoreByUserId(mch.getUserid());
				if(null == ifmZhimaScore){
					ifmZhimaScore = new AppZhimaScore();
					ifmZhimaScoreMapper.insertSelective(ifmZhimaScore);
				}
				ifmZhimaScore.setUserId(mch.getUserid());
				ifmZhimaScore.setOpenId(open_id);
				AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
				if(null == loanAppl){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "该用户未注册");
					return detail;
				}
				String uuid = UUID.randomUUID().toString();
				Map<String, String> score = new HashMap<String, String>();
				// 分别获得芝麻信用的分数
				// 去获得用户的信息
				ZhimaCreditScoreGetRequest req = new ZhimaCreditScoreGetRequest();
				req.setChannel("apppc");
				req.setPlatform("zmop");
				req.setTransactionId(uuid);// 必要参数
				req.setProductCode("w1010100100000000001");// 必要参数
				req.setOpenId(open_id);// 必要参数
				DefaultZhimaClient clientScore = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
				try {
					ZhimaCreditScoreGetResponse response = clientScore.execute(req);
					if (null != response && response.isSuccess()) {
						// 芝麻信用分
						ifmZhimaScore.setZhimaCreditScore(StringUtil.toString(response.getZmScore()));
						loanAppl.setZhimaCreditScore(StringUtil.toString(response.getZmScore()));
						score.put("score", response.getZmScore());
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, response.getErrorMessage());
						return detail;
					}
				} catch (ZhimaApiException e) {
					e.printStackTrace();
				}

                uuid = UUID.randomUUID().toString();
                String name = appLoanCtm.getCustomName();
                String certNo = appLoanCtm.getIdentityCard();
                String mobile =  appUser.getUserName();
                String email = "";
                String bankCard = appLoanCtm.getBankCard();
                String address = appLoanCtm.getAddress();

				ZhimaCreditIvsDetailGetRequest reqRequest = new ZhimaCreditIvsDetailGetRequest();
				//ZhimaCreditAntifraudScoreGetRequest reqRequest = new ZhimaCreditAntifraudScoreGetRequest();

				reqRequest.setChannel("apppc");
                reqRequest.setPlatform("zmop");
                reqRequest.setProductCode("w1010100000000000103");// 必要参数
                reqRequest.setTransactionId(uuid);// 必要参数
				reqRequest.setCertType("IDENTITY_CARD");//
				reqRequest.setCertNo(certNo);// 必要参数
				reqRequest.setName(name);// 必要参数
				reqRequest.setMobile(mobile);
                reqRequest.setEmail(email);
				reqRequest.setBankCard(bankCard);//
                reqRequest.setAddress(address);
				/*reqRequest.setIp("191.168.1.27");//
				reqRequest.setMac("1C-1B-0D-FE-E9-9B");//
				reqRequest.setWifimac("00-00-00-00-00-E0");//
				reqRequest.setImei("868331011992179");//*/

				DefaultZhimaClient ivsclient = new DefaultZhimaClient(gatewayUrl, appId, privateKey, zhimaPublicKey);
				try {
					ZhimaCreditIvsDetailGetResponse response =(ZhimaCreditIvsDetailGetResponse)ivsclient.execute(reqRequest);
//					ZhimaCreditAntifraudScoreGetResponse response =(ZhimaCreditAntifraudScoreGetResponse)ivsclient.execute(reqRequest);
					if (null != response && response.isSuccess()) {
						// 反欺诈信息
						ifmZhimaScore.setZhimaIvsScore(StringUtil.toString(response.getIvsScore()));
						loanAppl.setZhimaIvsScore(StringUtil.toString(response.getIvsScore()));
						List<IvsDetail> list = response.getIvsDetail();
						JSONArray json = new JSONArray();
						for (IvsDetail ivsDetail : list) {
							JSONObject jo = new JSONObject();
							jo.put("code", ivsDetail.getCode());
							jo.put("description", ivsDetail.getDescription());
							json.add(jo);
						}
						ifmZhimaScore.setIvsDetail(StringUtil.toString(json));
						score.put("ivsscore", response.getIvsScore() + "");
					}
				} catch (ZhimaApiException e) {
					e.printStackTrace();
				}

				uuid = UUID.randomUUID().toString();
				ZhimaCreditWatchlistiiGetRequest creditWatchlistGetRequest = new ZhimaCreditWatchlistiiGetRequest();
				creditWatchlistGetRequest.setPlatform("zmop");
				creditWatchlistGetRequest.setChannel("apppc");
				creditWatchlistGetRequest.setProductCode("w1010100100000000022");
				creditWatchlistGetRequest.setTransactionId(uuid);
				creditWatchlistGetRequest.setOpenId(open_id);
				DefaultZhimaClient watchclient = new DefaultZhimaClient(gatewayUrl, appId, "utf-8", privateKey,
						zhimaPublicKey);
				ZhimaCreditWatchlistiiGetResponse response;
				try {
					// 关注名单
					response = watchclient.execute(creditWatchlistGetRequest);
					if (null != response && response.isSuccess()) {
						if (response.getIsMatched()) {
							ifmZhimaScore.setWatchlistiiIsMatched("true");
							loanAppl.setWatchlistiiIsMatched("true");
							List<ZmWatchListDetail> list = response.getDetails();
							JSONArray json = new JSONArray();
							for (ZmWatchListDetail zmWatchListDetail : list) {
								JSONObject jo = new JSONObject();
								jo.put("biz_code", zmWatchListDetail.getBizCode());
								jo.put("level",zmWatchListDetail.getLevel());
								jo.put("type", zmWatchListDetail.getType());
								jo.put("code", zmWatchListDetail.getCode());
								jo.put("refresh_time", StringUtil.toString(zmWatchListDetail.getRefreshTime()));
								jo.put("settlement", StringUtil.toString(zmWatchListDetail.getSettlement()));
								jo.put("status", StringUtil.toString(zmWatchListDetail.getStatus()));
								jo.put("statement", StringUtil.toString(zmWatchListDetail.getStatement()));
								List<ZmWatchListExtendInfo> info = zmWatchListDetail.getExtendInfo();
								JSONArray dataList = new JSONArray();
								for (ZmWatchListExtendInfo zmWatchListExtendInfo : info) {
									JSONObject fo = new JSONObject();
									fo.put("key", zmWatchListExtendInfo.getKey());
									fo.put("value", zmWatchListExtendInfo.getValue());
									fo.put("description", zmWatchListExtendInfo.getDescription());
									jo.put("extend_info", zmWatchListExtendInfo);
									dataList.add(fo);
								}
								jo.put("list", dataList);
								json.add(jo);
							}
							ifmZhimaScore.setWatchlistiiDetail(StringUtil.toString(json));
							ifmZhimaScore.setWatchlistiiIsMatched("true");
							loanAppl.setWatchlistiiIsMatched("true");
						} else {
							ifmZhimaScore.setWatchlistiiIsMatched("false");
							loanAppl.setWatchlistiiIsMatched("false");
						}
					}
				} catch (ZhimaApiException e) {
					e.printStackTrace();
				}
				ifmZhimaScore.setStatus("0");
				ifmZhimaScore.setAuticDate(new Date());
				ifmZhimaScoreMapper.updateByPrimaryKeySelective(ifmZhimaScore);
				//风控退回的资料不需要改认证状态
				if(!"1".equals(StringUtil.nvl(appl.getZhimaStatus()))){
					if(Integer.parseInt(appLoanCtm.getSchedule_status()) <= 7){
						appLoanCtm.setSchedule_status("7");
					}
				}else{
					loanAppl.setZhimaStatus("0");
				}
				appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
				appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm);
				if("8".equals(StringUtil.nvl(loanAppl.getStatus()))){
					JSONObject object = new JSONObject();
					object.put("userId", loanAppl.getUserId());
					Map<String, Object> m = new HashMap<String, Object>();
					m.put("cntType", "2");
					m.put("apprId", appl.getId());
					AppLoanCtmCnt loanCtmCnt = appLoanCtmCntMapper.queryByType(m);
					object.put("token", loanCtmCnt.getCntLx());
					object.put("report_id", loanCtmCnt.getGroupId());
					JSONObject object2 = decisionEngineService.checkReportExsit(object);
					if("1".equals(StringUtil.nvl(object2.get("result")))){
						throw new Exception();
					}
				}
				detail.put(Consts.RESULT_NOTE, "芝麻信用分认证成功");
				detail.put("creditScore",ifmZhimaScore.getZhimaCreditScore());
				detail.put("ivsScore",ifmZhimaScore.getZhimaIvsScore());
				detail.put("watchlist",ifmZhimaScore.getWatchlistiiIsMatched());
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "芝麻认证失败");
			}
		} catch (ZhimaApiException e) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "芝麻认证失败");
			e.printStackTrace();
			throw new Exception();
		}
		return detail;
	}

}
