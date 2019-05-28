package com.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoanCtmShipMapper;
import com.dao.AppLoginMapper;
import com.dao.AppOprLogMapper;
import com.dao.AppPhoneBookMapper;
import com.dao.AppPhoneBookTwoMapper;
import com.dao.AppUserMapper;
import com.dao.AppZhimaScoreMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppLoanCtmShip;
import com.model.AppLogin;
import com.model.AppOprLog;
import com.model.AppPhoneBook;
import com.model.AppPhoneBookTwo;
import com.model.AppUser;
import com.model.AppZhimaScore;
import com.model.NameAndPhone;
import com.service.intf.AppGoSumbitService;
import com.service.intf.ContactsConfirmService;
import com.service.intf.RegisterInfoIsCompleteService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class ContactsConfirmImpl implements ContactsConfirmService {

	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private RegisterInfoIsCompleteService registerInfoIsCompleteService;
	@Autowired
	private AppGoSumbitService AppGoSumbitService;
	@Autowired
	private AppLoanCtmShipMapper appLoanCtmShipMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private AppPhoneBookMapper appPhoneBookMapper;
	@Autowired
	private AppPhoneBookTwoMapper appPhoneBookTwoMapper;
	@Autowired
	private AppOprLogMapper appOprLogMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppZhimaScoreMapper ifmZhimaScoreMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject contactsConfirm(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		JSONArray dataList = params.getJSONArray("contacts");
		JSONArray passWordList = params.getJSONArray("passWordList");
		JSONArray phoneNameList = params.getJSONArray("phoneNameList");

		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLogin login = appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
		if (appLoanAppl != null) {
			List<AppLoanCtmShip> appLoanCtmShips = appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId());
			List<Map<String, Object>> pw = appLoanCtmCntMapper.selectCnPassByApprId(appLoanAppl.getId());
			if (appLoanCtmShips.size() == 0 && pw.size() == 0) {
				for (Object obj : dataList) {
					JSONObject s = (JSONObject) obj;
					AppLoanCtmShip appLoanCtmShip = new AppLoanCtmShip();
					appLoanCtmShip.setApprId(appLoanAppl.getId());
					appLoanCtmShip.setShipType(StringUtil.toString(s.get("contactType")));
					appLoanCtmShip.setShipCnt(StringUtil.toString(s.get("contactPhone")));
					appLoanCtmShip.setShipName(StringUtil.toString(s.get("contactName")));
					appLoanCtmShipMapper.insertSelective(appLoanCtmShip);
				}
				for (Object obj : passWordList) {
					JSONObject s = (JSONObject) obj;
					AppLoanCtmCnt appLoanCtmCnt = new AppLoanCtmCnt();
					appLoanCtmCnt.setApprId(appLoanAppl.getId());
					appLoanCtmCnt.setCntType(StringUtil.toString(s.get("cntTybe")));
					if (StringUtil.toString(s.get("cntTybe")).equals("0")) {
						appLoanCtmCnt.setCntLx("0");
					} else {
						appLoanCtmCnt.setCntLx("1");
					}
					appLoanCtmCnt.setCntPass(StringUtil.toString(s.get("cntPass")));
					appLoanCtmCntMapper.insertSelective(appLoanCtmCnt);
				}
				List<AppPhoneBook> books = new ArrayList<AppPhoneBook>();
				for (Object obj : phoneNameList) {
					JSONObject s = (JSONObject) obj;
					AppPhoneBook appPhoneBook = new AppPhoneBook();
					appPhoneBook.setApprId(appLoanAppl.getId());
					appPhoneBook.setLoanphone(StringUtil.toString(s.get("phone")));
					appPhoneBook.setPhoneName(StringUtil.toString(s.get("phoneName")));
					books.add(appPhoneBook);
				}
				appPhoneBookMapper.insertListAppPhoneBook(books);
				AppOprLog appOprLog = new AppOprLog();
				appOprLog.setModuleId("0");
				appOprLog.setOprContent("联系人与密码保存成功!");
				appOprLog.setUserid(userId);
				appOprLogMapper.insertSelective(appOprLog);
				detail.put(Consts.RESULT_NOTE, "联系人与密码保存成功");
				JSONObject j = registerInfoIsCompleteService.registerInfoIsComplete(params);
				if (j.getBooleanValue("isCompleter")) {
					AppGoSumbitService.AppgoSumbit(params);
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "联系人与密码已填写！");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "userId错误！");
		}

		return detail;
	}

	@Override
	public JSONObject queryContacts(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if (null != user) {
			AppLogin login = appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
			List<AppLoanCtmShip> appLoanCtmShips = appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId());
			List<Map<String, Object>> pws = appLoanCtmCntMapper.selectCnPassByApprId(appLoanAppl.getId());
			if (null != appLoanCtmShips && appLoanCtmShips.size() > 0 && null != pws && pws.size() > 0) {
				JSONArray phoneList = new JSONArray();
				JSONArray pwList = new JSONArray();
				for (AppLoanCtmShip appLoanCtmShip : appLoanCtmShips) {
					JSONObject jo = new JSONObject();
					jo.put("contactType", appLoanCtmShip.getShipType());
					jo.put("contactName", appLoanCtmShip.getShipName());
					jo.put("contactPhone", appLoanCtmShip.getShipCnt());
					phoneList.add(jo);
				}
				for (Map<String, Object> pw : pws) {
					JSONObject jo = new JSONObject();
					jo.put("cntType", pw.get("cnt_type"));
					jo.put("cntPass", pw.get("cnt_pass"));
					pwList.add(jo);
				}
				detail.put("phoneList", phoneList);
				detail.put("pwList", pwList);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "未填写联系人");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请重新登录");
		}
		return detail;
	}

	/**
	 * 保存联系人/修改联系人信息，并记录通讯录
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject saveContacts(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		JSONArray dataList = params.getJSONArray("contacts");
		JSONArray phoneNameList = params.getJSONArray("phoneNameList");
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		if (appLoanAppl != null) {
			if(null != appLoanAppl.getAccountStatus() && 3 == appLoanAppl.getAccountStatus()){ //账户关闭
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
				return detail;
			}else{
				if("1".equals(StringUtil.nvl(appLoanAppl.getStatus()))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "正在授信中，不可修改资料");
					return detail;
				}
				if("2".equals(StringUtil.nvl(appLoanAppl.getStatus()))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "授信已通过，不可修改资料");
					return detail;
				}
				List<AppLoanCtmShip> appLoanCtmShips = appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId());
				AppLoanCtm ctm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
				if (5 > Integer.parseInt(ctm.getSchedule_status())) {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "请先完善基本资料");
					return detail;
				}
				// 保存联系人资料
				if (null != dataList && dataList.size() > 0) {
					if (appLoanCtmShips.size() == 0 || appLoanCtmShips == null) { // 新增资料
						JSONObject s = null;
						AppLoanCtmShip appLoanCtmShip = null;
						String ppp = "";
						String adress = "";
						for (Object obj : dataList) {
							s = (JSONObject) obj;
							appLoanCtmShip = new AppLoanCtmShip();
							appLoanCtmShip.setApprId(appLoanAppl.getId());
							appLoanCtmShip.setShipType(StringUtil.toString(s.get("contactType")));
							ppp = StringUtil.toString(s.get("contactPhone")).replace("-", "").replace(" ", "").replace("%","");
							appLoanCtmShip.setShipCnt(ppp);
							appLoanCtmShip.setShipName(StringUtil.toString(s.get("contactName")));
							if ("0".equals(StringUtil.toString(s.get("contactType")))) {
								adress = StringUtil.toString(s.get("familyAdress")) + "," + StringUtil.toString(s.get("contactAdress"));
								appLoanCtmShip.setShipAddr(adress);
							}
							appLoanCtmShip.setRelationship(StringUtil.toString(s.get("relationship")));
							appLoanCtmShipMapper.insertSelective(appLoanCtmShip);
						}
						AppOprLog appOprLog = new AppOprLog();
						appOprLog.setModuleId("0");
						appOprLog.setOprContent("联系人保存成功");
						appOprLog.setUserid(userId);
						appOprLogMapper.insertSelective(appOprLog);
						detail.put(Consts.RESULT_NOTE, "联系人保存成功");
					} else { //修改资料
						JSONObject s = null;
						String ppp = "";
						String adress = "";
						for (Object obj : dataList) {
							s = (JSONObject) obj;
							ppp = StringUtil.toString(s.get("contactPhone")).replace("-", "").replace(" ", "").replace("%","");
							for (AppLoanCtmShip ships : appLoanCtmShips) {
								if (ships.getShipType().equals(StringUtil.toString(s.get("contactType")))) {
									ships.setShipCnt(ppp);
									ships.setShipName(StringUtil.toString(s.get("contactName")));
									if ("0".equals(StringUtil.toString(s.get("contactType")))) {
										adress = StringUtil.toString(s.get("familyAdress")) + "," + StringUtil.toString(s.get("contactAdress"));
										ships.setShipAddr(adress);
									}
									ships.setRelationship(StringUtil.toString(s.get("relationship")));
									appLoanCtmShipMapper.updateByPrimaryKeySelective(ships);
								}
							}
						}
						detail.put(Consts.RESULT_NOTE, "联系人修改成功");
					}
					//风控退回的资料不需要改认证状态
					if(!"1".equals(appLoanAppl.getContactInfoStatus())){
						if (6 >= Integer.parseInt(ctm.getSchedule_status())) {
							List<Map<String,Object>> ifmBaseDicts= ifmBaseDictMapper.selectBaseDict("IS_ZHIMA_CREDIT"); //获取请求接口url
						    if("1".equals(ifmBaseDicts.get(0).get("ITEM_VALUE"))){
						    	ctm.setSchedule_status("7");
								AppZhimaScore ifmZhimaScore = ifmZhimaScoreMapper.selectZhimaScoreByUserId(mch.getUserid());
								if(ifmZhimaScore == null ){
									AppZhimaScore appZhimaScore = new AppZhimaScore();
									appZhimaScore.setZhimaCreditScore("-1");
									appZhimaScore.setZhimaIvsScore("-1");
									appZhimaScore.setOpenId("-1");
									appZhimaScore.setStatus("0");
									appZhimaScore.setWatchlistiiIsMatched("false");
									appZhimaScore.setUserId(mch.getUserid());
									appLoanAppl.setZhimaCreditScore("-1");
									appLoanAppl.setZhimaIvsScore("-1");
									appLoanAppl.setWatchlistiiIsMatched("false");
									ifmZhimaScoreMapper.insertSelective(appZhimaScore);
									appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
								}
						    }else{
						    	ctm.setSchedule_status("6");
						    }
						}
					}else{
						appLoanAppl.setContactInfoStatus("0");
						appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
					}
					ctm.setLxr_status("1");
					appLoanCtmMapper.updateByPrimaryKeySelective(ctm);
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "请先填写联系人资料");
					return detail;
				}
				// 保存通讯录
				if (null != phoneNameList && phoneNameList.size() > 0) {
					JSONObject s = null;
					String phoneName = "";
					String phone = "";
					NameAndPhone nameAndPhone = null;
					Set<NameAndPhone> nameAndPhones = new HashSet<NameAndPhone>();
					List<AppPhoneBook> phoneBooks = appPhoneBookMapper.selectByApprId(appLoanAppl.getId());
					if(null != phoneBooks && phoneBooks.size() > 0){ //已存在的通讯录先拿出来
						for(AppPhoneBook phoneBook : phoneBooks){
							nameAndPhone = new NameAndPhone();
							nameAndPhone.setName(phoneBook.getPhoneName());
							nameAndPhone.setPhone(phoneBook.getLoanphone());
							nameAndPhones.add(nameAndPhone);
						}
						appPhoneBookMapper.deleteByApprId(appLoanAppl.getId());
					}
					for (Object obj : phoneNameList) {
						s = (JSONObject) obj;
						phoneName = StringUtil.toString(s.get("phoneName"));
						phone = StringUtil.toString(s.get("phone"));
						if (phone.length() > 500) {
							phone = phone.substring(0, 500);
						}
						nameAndPhone = new NameAndPhone();
						nameAndPhone.setName(phoneName.replace("%", ""));
						nameAndPhone.setPhone(phone.replace("%", ""));
						nameAndPhones.add(nameAndPhone);
					}
					List<AppPhoneBook> books = new ArrayList<AppPhoneBook>();
					AppPhoneBook appPhoneBook = null;
					if(null != nameAndPhones && nameAndPhones.size() > 0){
						for(NameAndPhone namePhone : nameAndPhones){
							appPhoneBook = new AppPhoneBook();
							appPhoneBook.setApprId(appLoanAppl.getId());
							appPhoneBook.setLoanphone(namePhone.getPhone());
							appPhoneBook.setPhoneName(namePhone.getName());
							books.add(appPhoneBook);
						}
						appPhoneBookMapper.insertListAppPhoneBook(books);
					}
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject savePhoneList(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		JSONArray phoneNameList = params.getJSONArray("phoneNameList");
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null == mch) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid() + "";
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		if (appLoanAppl != null) {
			List<AppPhoneBookTwo> appPhoneBookTwos = appPhoneBookTwoMapper.selectByapprId(appLoanAppl.getId());
			if (null == appPhoneBookTwos || appPhoneBookTwos.size() == 0) {
				List<AppPhoneBookTwo> books = new ArrayList<AppPhoneBookTwo>();
				if (phoneNameList != null && phoneNameList.size() > 0) {
					JSONObject s = null;
					String phoneName = "";
					String phone = "";
					NameAndPhone nameAndPhone = null;
					Set<NameAndPhone> nameAndPhones = new HashSet<NameAndPhone>();
					for (Object obj : phoneNameList) {
						s = (JSONObject) obj;
						phoneName = StringUtil.toString(s.get("phoneName"));
						phone = StringUtil.toString(s.get("phone"));
						if (phone.length() > 500) {
							phone = phone.substring(0, 500);
						}
						nameAndPhone = new NameAndPhone();
						nameAndPhone.setName(phoneName);
						nameAndPhone.setPhone(phone);
						nameAndPhones.add(nameAndPhone);
					}
					if (null != nameAndPhones && nameAndPhones.size() > 0) {
						String nName = "";
						String nPhone = "";
						AppPhoneBookTwo appPhoneBook = null;
						for (NameAndPhone nAndPhone : nameAndPhones) {
							nName = nAndPhone.getName();
							nPhone = nAndPhone.getPhone();
							appPhoneBook = new AppPhoneBookTwo();
							appPhoneBook.setApprId(appLoanAppl.getId());
							appPhoneBook.setLoanphone(nPhone);
							appPhoneBook.setPhoneName(nName);
							books.add(appPhoneBook);
						}
					}
				} else {
					AppPhoneBookTwo appPhoneBook = new AppPhoneBookTwo();
					appPhoneBook.setApprId(appLoanAppl.getId());
					appPhoneBook.setLoanphone("通讯录无联系人");
					appPhoneBook.setPhoneName("通讯录无联系人");
					books.add(appPhoneBook);
				}
				appPhoneBookTwoMapper.insertListAppPhoneBook(books);
				AppOprLog appOprLog = new AppOprLog();
				appOprLog.setModuleId("0");
				appOprLog.setOprContent("通讯录认证成功");
				appOprLog.setUserid(userId);
				appOprLogMapper.insertSelective(appOprLog);
				detail.put(Consts.RESULT_NOTE, "通讯录认证成功");
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "通讯录已认证");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "认证失败");
		}
		return detail;
	}

}
