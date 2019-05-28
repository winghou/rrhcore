package com.service;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanAttchMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppUserMapper;
import com.dao.AppYouDunParamMapper;
import com.dao.AppYouDunResultMapper;
import com.dao.AppYouDunVerifyResultMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanAttch;
import com.model.AppLoanCtm;
import com.model.AppUser;
import com.model.AppYouDunParam;
import com.model.AppYouDunResult;
import com.model.AppYouDunVerifyResult;
import com.service.intf.YouDunService;
import com.util.EnumProductCode;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

import yc.ycsdk.oss.OSSFileUtil;

@Service
public class YouDunServiceImpl implements YouDunService {
	
	@Autowired
	private AppYouDunResultMapper appYouDunResultMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLoanAttchMapper appLoanAttchMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppYouDunParamMapper appYouDunParamMapper;
	@Autowired
	private AppYouDunVerifyResultMapper appYouDunVerifyResultMapper;
	
	/**
	 * 保存OCR信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject saveYouDunOCRInfo(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String product_code = JsonUtil.getJStringAndCheck(params, "product_code", null, false, detail);
		String partner_order_id = JsonUtil.getJStringAndCheck(params, "partner_order_id", null, false, detail);
		String idcard_front_photo = JsonUtil.getJStringAndCheck(params, "idcard_front_photo", null, false, detail);
		String idcard_portrait_photo = JsonUtil.getJStringAndCheck(params, "idcard_portrait_photo", null, false, detail);
		String idcard_back_photo = JsonUtil.getJStringAndCheck(params, "idcard_back_photo", null, false, detail);
		String id_number = JsonUtil.getJStringAndCheck(params, "id_number", null, false, detail);
		String id_name = JsonUtil.getJStringAndCheck(params, "id_name", null, false, detail);
		String address = JsonUtil.getJStringAndCheck(params, "address", null, false, detail);
		String living_photo = JsonUtil.getJStringAndCheck(params, "living_photo", null, false, detail);
		String risk_tag = JsonUtil.getJStringAndCheck(params, "risk_tag", null, false, detail);
		String similarity = JsonUtil.getJStringAndCheck(params, "similarity", null, false, detail);
		String thresholds = JsonUtil.getJStringAndCheck(params, "thresholds", null, false, detail);
		if(!"".equals(idcard_front_photo)){
			idcard_front_photo = URLDecoder.decode(idcard_front_photo, "utf-8");
		}
		if(!"".equals(idcard_back_photo)){
			idcard_back_photo = URLDecoder.decode(idcard_back_photo, "utf-8");
		}
		if(!"".equals(idcard_portrait_photo)){
			idcard_portrait_photo = URLDecoder.decode(idcard_portrait_photo, "utf-8");
		}
		AppYouDunResult youDunResult = appYouDunResultMapper.selectByOrderId(partner_order_id);
		if (null != youDunResult) {
			AppLoanCtm ctm = appLoanCtmMapper.selectByapprId(youDunResult.getApprId());
			if (null != ctm && ("".equals(StringUtil.nvl(youDunResult.getIdcardFrontPhoto()))
					|| "".equals(StringUtil.nvl(youDunResult.getIdcardBackPhoto()))
					|| "".equals(StringUtil.nvl(youDunResult.getSimilarity()))
					|| "".equals(StringUtil.nvl(youDunResult.getRiskTag())))) {
				Integer apprId = youDunResult.getApprId();
				Map<String, Object> map = null;
				AppLoanAttch attchx = null;
				OSSFileUtil util  = new OSSFileUtil("http://oss-cn-shanghai-internal.aliyuncs.com", "LTAIkntwEqMgZ2q2", "WyDBHmS0Ni4JXnyAHRnkKlHzsQXs3Y", "yuecai"); //正式
//				OSSFileUtil util  = new OSSFileUtil("http://oss-cn-shanghai.aliyuncs.com", "LTAIkntwEqMgZ2q2", "WyDBHmS0Ni4JXnyAHRnkKlHzsQXs3Y", "yuecai11"); //测试
				EnumProductCode enumProductCode = EnumProductCode.newInstance(product_code);
				switch (enumProductCode) {// 按产品接收结果数据
				case OCR_FRONT:
					if("".equals(StringUtil.nvl(youDunResult.getIdcardFrontPhoto()))){
						map = new HashMap<String, Object>();
						map.put("fileName", "0");
						map.put("apprId", apprId);
						List<AppLoanAttch> attchs0 = appLoanAttchMapper.selectByFileNameAndApprId(map);
						//删除身份证正面照
						if (null != attchs0 && attchs0.size() > 0) {
							for(AppLoanAttch attch0 : attchs0){
								appLoanAttchMapper.deleteByPrimaryKey(attch0.getId());
							}
						}
						String frontFhoto = idcard_front_photo.replace(" ", "+");
						idcard_portrait_photo = idcard_portrait_photo.replace(" ", "+");
						// 上传正面照
						Map<String, String> map2 = util.uploadBase642OSS(frontFhoto, "jpg", "mch");
						// 上传正面头像照
						Map<String, String> map4 = util.uploadBase642OSS(idcard_portrait_photo, "jpg", "mch");
						String frontFhotoUri = "";
						String portraitPhotoUri = "";
						String resultNode2 = StringUtil.nvl(map2.get("resultNode"));
						String resultNode4 = StringUtil.nvl(map4.get("resultNode"));
						if("success".equals(resultNode2) && "success".equals(resultNode4)){
							// 返回正面照URI
							String fes2 = StringUtil.nvl(map2.get("fes"));
							JSONObject object2 = JSON.parseObject(fes2);
							frontFhotoUri = "/" + StringUtil.nvl(object2.get("file_uri"));
							// 返回头像照URI
							String fes4 = StringUtil.nvl(map4.get("fes"));
							JSONObject object4 = JSON.parseObject(fes4);
							portraitPhotoUri = "/" + StringUtil.nvl(object4.get("file_uri"));
						}else{
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							return detail;
						}
						//保存正面图片地址
						if(!"".equals(frontFhotoUri) && !"".equals(portraitPhotoUri)){
							attchx = new AppLoanAttch();
							attchx.setApprId(apprId);
							attchx.setFileName("0");
							attchx.setFileUri(frontFhotoUri);
							attchx.setSmallPicUrl(frontFhotoUri);
							attchx.setPortraitPhotoUrl(portraitPhotoUri);
							appLoanAttchMapper.insertSelective(attchx);
							youDunResult.setIdcardFrontPhoto(frontFhotoUri);
							youDunResult.setIdNumber(id_number);
							youDunResult.setIdName(id_name);
							youDunResult.setAddress(address);
						}
					}
					break;
				case OCR_BACK:
					if("".equals(StringUtil.nvl(youDunResult.getIdcardBackPhoto()))){
						map = new HashMap<String, Object>();
						map.put("fileName", "1");
						map.put("apprId", apprId);
						List<AppLoanAttch> attchs1 = appLoanAttchMapper.selectByFileNameAndApprId(map);
						//删除身份证反面照
						if (null != attchs1 && attchs1.size() > 0) {
							for (AppLoanAttch attch1 : attchs1) {
								appLoanAttchMapper.deleteByPrimaryKey(attch1.getId());
							}
						}
						String backFhoto = idcard_back_photo.replace(" ", "+");
						// 上传身份证反面照
						Map<String, String> map2 = util.uploadBase642OSS(backFhoto, "jpg", "mch");
						String backFhotoUri = "";
						String resultNode2 = StringUtil.nvl(map2.get("resultNode"));
						if("success".equals(resultNode2)){
							// 获取返回反面照URI
							String fes2 = StringUtil.nvl(map2.get("fes"));
							JSONObject object2 = JSON.parseObject(fes2);
							backFhotoUri = "/" + StringUtil.nvl(object2.get("file_uri"));
						}
						//保存反面图片地址
						if(!"".equals(backFhotoUri)){
							attchx = new AppLoanAttch();
							attchx.setApprId(apprId);
							attchx.setFileName("1");
							attchx.setFileUri(backFhotoUri);
							attchx.setSmallPicUrl(backFhotoUri);
							appLoanAttchMapper.insertSelective(attchx);
							youDunResult.setIdcardBackPhoto(backFhotoUri);
						}
					}
					break;
				case VERIFY_RETURN_PHOTO:
                    //实名验证
                    break;
                case LIVING_DETECT:
                    //活体检测
                	if("".equals(StringUtil.nvl(youDunResult.getRiskTag()))){
                		youDunResult.setRiskTag(((JSONObject)JSON.parse(risk_tag)).toString());
                	}
                    break;
                case FACE_COMPARE:
                    //人脸比对
                	if("".equals(StringUtil.nvl(youDunResult.getSimilarity()))){
                		youDunResult.setSimilarity(similarity);
                		youDunResult.setThresholds(thresholds);
                		List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("YOUDUN_VARIFY_SCORE");
                		String score = StringUtil.nvl(maps.get(0).get("ITEM_VALUE"));
                		// 校验通过，保存用户资料
                		if(-1 != new BigDecimal(similarity).compareTo(new BigDecimal(score))){
                			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(apprId);
                			appLoanCtm.setCustomName(id_name);
                			appLoanCtm.setIdentityCard(id_number);
                			appLoanCtm.setIdentity_status("1");
                			appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm);
                		}
                	}
                    break;
                case VIDEO_AUTH:
                    //视频存证
                    break;
				}
				appYouDunResultMapper.updateByPrimaryKeySelective(youDunResult);
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
			}else{
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put(Consts.RESULT_NOTE, "已进行校验");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "未进行OCR的记录");
		}
		return detail;
	}
	
	/**
	 * 上传图片到服务器
	 * @param base64 照片
	 * @param picUrl 照片保存地址
	 * @param condensedPicUrl 照片压缩照保存地址
	 * @throws Exception 
	 */
	private static void uploadPictrue(byte[] base64, String picUrl, String condensedPicUrl) throws Exception {
		if (null != base64 && base64.length > 0 && !"".equals(picUrl) && !"".equals(condensedPicUrl)) {
			// 照片上传
			InputStream fin = new ByteArrayInputStream(base64);
			FileOutputStream fout = new FileOutputStream(picUrl);
			byte[] b = new byte[1024];
			int length = 0;
			while ((length = fin.read(b)) > 0) {
				fout.write(b, 0, length);
			}
			fin.close();
			fout.close();
			// 图片缩略
			int nw = 500;
			AffineTransform transform = new AffineTransform();
			BufferedImage bis = ImageIO.read(new File(picUrl));
			int w = bis.getWidth();
			int h = bis.getHeight();
			int nh = (nw * h) / w;
			double sx = (double) nw / w;
			double sy = (double) nh / h;
			transform.setToScale(sx, sy);
			AffineTransformOp ato = new AffineTransformOp(transform, null);
			BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_3BYTE_BGR);
			ato.filter(bis, bid);
			ImageIO.write(bid, "jpg", new File(condensedPicUrl));
		}
	}
	
	/**
	 * 保存身份证校验、人像校验结果
	 */
	@Override
	public JSONObject saveYouDunIDCheckInfo(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String partner_order_id = JsonUtil.getJStringAndCheck(params, "partner_order_id", null, false, detail);
		String verify_status = JsonUtil.getJStringAndCheck(params, "verify_status", null, false, detail);
		String similarity = JsonUtil.getJStringAndCheck(params, "similarity", null, false, detail);
		String auth_result = JsonUtil.getJStringAndCheck(params, "auth_result", null, false, detail);
		String fail_reason = JsonUtil.getJStringAndCheck(params, "fail_reason", null, false, detail);
		
		AppYouDunResult youDunResult = appYouDunResultMapper.selectByOrderId(partner_order_id);
		if (null != youDunResult) {
			youDunResult.setVerifyStatus(StringUtil.nvl(verify_status));
			youDunResult.setSimilarity(StringUtil.nvl(similarity));
			youDunResult.setAuthResult(StringUtil.nvl(auth_result));
			if("1".equals(verify_status) || "T".equals(auth_result)){
				youDunResult.setStatus("0"); //校验成功
 			}else{
 				youDunResult.setStatus("1"); //校验失败
 			}
			appYouDunResultMapper.updateByPrimaryKeySelective(youDunResult);
			detail.put(Consts.RESULT, ErrorCode.SUCCESS);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "未进行OCR的记录");
		}
		return detail;
	}

	/* @author yang.wu
	 * 类名称： GetYouDunSeriveImpl
	 * 创建时间：2017年5月11日 下午5:35:07
	 * @see com.service.intf.GetYouDunService#getYouDunSign(com.alibaba.fastjson.JSONObject)
	 * 类描述：有盾签名
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject getYouDunSign(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail); //用户id
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		if(appLoanAppl != null){
			String num = "3";
			List<Map<String, Object>> lists = ifmBaseDictMapper.selectBaseDict("YOUDUN_VARIFY_NUM");
			if (null != lists && lists.size() > 0) {
				num = StringUtil.nvl(lists.get(0).get("ITEM_VALUE"));
			}
			List<AppYouDunVerifyResult> youDunVerifyResults = appYouDunVerifyResultMapper.selectByApprIdThisDay(appLoanAppl.getId());
			if (null != youDunVerifyResults && youDunVerifyResults.size() >= (Integer.parseInt(num))) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "当日验证次数已达上限，请明天再试吧！");
				return detail;
			}
			AppYouDunParam appYouDunParam = appYouDunParamMapper.selectByPrimaryKey(1);
			String pubKey = appYouDunParam.getPublicKey();    //公钥
			String packageCode = appYouDunParam.getPackageCode();  //套餐编号
			String securityKey = appYouDunParam.getPrivateKey();   //私钥
			String notificationUrl = appYouDunParam.getNoyifyUrl();  //有盾回调地址
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			String signTime =dateFormat.format(new Date());  //签名时间 
			String partnerOrderId =UUID.randomUUID().toString().replace("-", "");  //商户订单号
			LinkedHashMap<String, Object> link = new LinkedHashMap<String, Object>();
			link.put("pub_key", pubKey);
			link.put("partner_order_id", partnerOrderId);
			link.put("sign_time", signTime);
			link.put("security_key", securityKey);
			String sign = StringUtil.md5Sign1(link, true);
			detail.put("sign", sign);
			detail.put("packageCode", packageCode);
			detail.put("pub_key", pubKey);
			detail.put("partner_order_id", partnerOrderId);
			detail.put("notificationUrl", notificationUrl);
			detail.put("sign_time", signTime);
			List<AppYouDunResult> appYouDunResult = appYouDunResultMapper.selectByApprId(appLoanAppl.getId());
			if(appYouDunResult != null && appYouDunResult.size() > 0){
				for(AppYouDunResult appYouDun : appYouDunResult){
					appYouDunResultMapper.deleteByPrimaryKey(appYouDun.getId());
				}
			}
			AppYouDunResult appYouDunResult02 = new AppYouDunResult();
			appYouDunResult02.setApprId(appLoanAppl.getId());
			appYouDunResult02.setPartnerOrderId(partnerOrderId);
			appYouDunResult02.setCreateDate(new Date());
			appYouDunResultMapper.insertSelective(appYouDunResult02);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
        	detail.put(Consts.RESULT_NOTE, "用户不存在");
		}
		return detail;
	}
	
	/**
	 * 获取有盾活体识别分数
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject getYouDunVerifyScore(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail); //用户id
		String verifyScore = JsonUtil.getJStringAndCheck(params, "verifyScore", null, true, detail); //验证分数
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null != mch){
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			if(Integer.parseInt(loanCtm.getSchedule_status()) > 4){
				return detail;
			}
			AppYouDunVerifyResult youDunVerifyResult = new AppYouDunVerifyResult();
			youDunVerifyResult.setApprId(appLoanAppl.getId());
			youDunVerifyResult.setCreateTime(new Date());
			youDunVerifyResult.setScore(verifyScore);
			appYouDunVerifyResultMapper.insertSelective(youDunVerifyResult);
			List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("YOUDUN_VARIFY_SCORE");
			String score  = "0.75";
			if(null != maps && maps.size() > 0){
				score = StringUtil.nvl(maps.get(0).get("ITEM_VALUE"));
			}
			if(-1 != new BigDecimal(verifyScore).compareTo(new BigDecimal(score))){ //大于等于
//				AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
				loanCtm.setSchedule_status("4");
				//对手机号与身份证号拼接字段加密
				loanCtm.setPhoneAndIdMd5(StringUtil.MD5(StringUtil.nvl(appLoanAppl.getItemCode()) + StringUtil.nvl(loanCtm.getIdentityCard())));
				appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
	        	detail.put(Consts.RESULT_NOTE, "校验未通过，请重试"); //活体校验未通过
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
        	detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 有盾扫描身份证正面检查身份证是否已使用
	 */
	@Override
	public JSONObject checkIDUseOrNot(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail); //用户id
		String identityNo = JsonUtil.getJStringAndCheck(params, "identityNo", null, true, detail); //获取身份证号码
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null != mch){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("identityCard", identityNo);
			map.put("apprId", loanAppl.getId());
			List<AppLoanCtm> loanCtms = appLoanCtmMapper.selectOthersByIdentityNo(map);
			if(null != loanCtms && loanCtms.size() > 0){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
	        	detail.put(Consts.RESULT_NOTE, "该身份证已被注册，请重试");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
        	detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
}
