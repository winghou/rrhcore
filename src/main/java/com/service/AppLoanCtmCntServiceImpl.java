package com.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.*;
import com.frame.Consts;
import com.model.*;
import com.service.intf.AppLoanCtmCntService;
import com.util.APIHttpClient;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by fangchen on 2017/11/10.
 */
@Service
public class AppLoanCtmCntServiceImpl implements AppLoanCtmCntService {

    @Autowired
    private AppLoanPlatformMapper appLoanPlatformMapper;
    @Autowired
    private AppLoanCtmCntMapper appLoanCtmCntMapper;
    @Autowired
    private IfmBaseDictMapper ifmBaseDictMapper;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private AppLoanApplMapper appLoanApplMapper;
    @Autowired
    private AppLoanCtmMapper appLoanCtmMapper;
    @Autowired
    private APPCreditMapper appCreditMapper;
    @Autowired
    private AppIncreaseAmtInfoMapper appIncreaseAmtInfoMapper;


    //展示所有贷款平台信息
    @Override
    public JSONObject appLoanCtmCnt(JSONObject params){

        JSONObject detail = new JSONObject();
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        List<AppLoanPlatform> platformList = appLoanPlatformMapper.selectPlatformList();
        if(null!=platformList  && platformList.size() > 0){
            JSONArray jsonArray = new JSONArray();
            JSONObject object = null;
            //逐条获取每一个贷款平台信息
            for(AppLoanPlatform platform : platformList){
                object = new JSONObject();
                object.put("id",platform.getId());
                object.put("platformName", platform.getPlatformName());
                object.put("logoimageUrl", platform.getLogoimageUrl());
                jsonArray.add(object);
            }
            detail.put("platformList", jsonArray);
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "无可用的贷款平台");
        }
        return detail;
    }

    //根据用户和贷款平台的主键编号查询平台登录时的用户名和密码，主页Logo，进行用户数据传输
    @Override
    public JSONObject platformLogin(JSONObject params){
        JSONObject detail = new JSONObject();
        String cntLx = JsonUtil.getJStringAndCheck(params, "cntLx", null, true, detail);  //传入平台ID
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            userId = appUser.getUserid() + "";
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
            AppLoanCtmCnt oriLoan = new AppLoanCtmCnt();
            oriLoan.setCntLx(cntLx);
            oriLoan.setApprId(appLoanAppl.getId());
            AppLoanCtmCnt appLoanCtmCntUser = appLoanCtmCntMapper.platformLogin(oriLoan);
            AppLoanPlatform appLoanPlatform = appLoanPlatformMapper.selectByPrimaryKey(Integer.parseInt(cntLx));
            if (null!=appLoanCtmCntUser ) {
                detail.put("cntCommt", appLoanCtmCntUser.getCntCommt());
                detail.put("cntPass", appLoanCtmCntUser.getCntPass());
                detail.put("platformName", appLoanPlatform.getPlatformName());
                detail.put("logoimageUrl", appLoanPlatform.getLogoimageUrl());
            } else {
                detail.put("cntCommt", "");
                detail.put("cntPass", "");
                detail.put("platformName", appLoanPlatform.getPlatformName());
                detail.put("logoimageUrl", appLoanPlatform.getLogoimageUrl());
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return  detail;
    }

    //用户保存平台新的账号密码,修改原有密码账号
    @Override
    public JSONObject platformLoanCtmCntSave(JSONObject params) {
        JSONObject detail = new JSONObject();
        String cntLx = JsonUtil.getJStringAndCheck(params, "cntLx", null, true, detail);  //传入平台ID
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String cntCommt = JsonUtil.getJStringAndCheck(params, "cntCommt", null, true, detail); //用户登录平台的名称
        String cntPass = JsonUtil.getJStringAndCheck(params, "cntPass", null, true, detail);  //用户登录平台密码
        //校验账号长度
        if(cntCommt.length()>50){
        	 detail.put(Consts.RESULT, ErrorCode.FAILED);
             detail.put(Consts.RESULT_NOTE, "您的账号过长，请重新输入");
        }
        if(cntPass.length()>50){
       	 	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的密码过长，请重新输入");
       }
        
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            userId = appUser.getUserid() + "";
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
            AppLoanCtmCnt oriLoan = new AppLoanCtmCnt();
            oriLoan.setCntLx(cntLx);
            oriLoan.setApprId(appLoanAppl.getId());
            AppLoanCtmCnt appLoanCtmCntUser = appLoanCtmCntMapper.platformLogin(oriLoan);
            if (null!=cntCommt   && !"".equals(cntCommt)&&  null!=cntPass && !"".equals(cntPass) ) {
//            if (cntCommt != null && cntCommt != "" && cntPass != null && cntPass != "") {
                if (null!=appLoanCtmCntUser  ) {
                    appLoanCtmCntUser.setCntCommt(cntCommt);
                    appLoanCtmCntUser.setCntPass(cntPass);
                    appLoanCtmCntMapper.updateByPrimaryKey(appLoanCtmCntUser);
                    detail.put("updateLoanCtmCnt", 1);
                } else {
                    List<AppLoanCtmCnt> userPlatformList = appLoanCtmCntMapper.userPlatforms(appLoanAppl.getId());
//                    if(userPlatformList.size() < 3) {
                        AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                        newAppLoanCtmCnt.setCntLx(cntLx);
                        newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                        newAppLoanCtmCnt.setCntCommt(cntCommt);
                        newAppLoanCtmCnt.setCntPass(cntPass);
                        newAppLoanCtmCnt.setCntDesc("1");
                        newAppLoanCtmCnt.setCntType("10");
                        appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                        detail.put("saveLoanCtmCnt", 1);
//                    }else{
//                        detail.put("saveLoanCtmCnt", 2);
//                        detail.put(Consts.RESULT, ErrorCode.FAILED);
//                        detail.put(Consts.RESULT_NOTE, "同一个用户不能绑定3个以上的贷款平台");
//                    }
                }
            }else {
                detail.put("saveLoanCtmCnt", 0);
                detail.put(Consts.RESULT, ErrorCode.FAILED);
                detail.put(Consts.RESULT_NOTE, "平台账号和密码不能为空");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //查询指定用户申请的所有贷款平台信息
    @Override
    public JSONObject userPlatforms(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            userId = appUser.getUserid() + "";
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
            List<AppLoanCtmCnt> appLoanCtmCntList = appLoanCtmCntMapper.userPlatforms(appLoanAppl.getId());
            if (null!=appLoanCtmCntList  && appLoanCtmCntList.size() > 0) {
                JSONArray jsonArray = new JSONArray();
                JSONObject object = null;
                for (AppLoanCtmCnt loanCtmCnt : appLoanCtmCntList) {
                    object = new JSONObject();
                    object.put("cntCommt", loanCtmCnt.getCntCommt());
                    object.put("cntPass", loanCtmCnt.getCntPass());
                    object.put("platformId", Integer.parseInt(loanCtmCnt.getCntLx()));
                    AppLoanPlatform appLoanPlatform = appLoanPlatformMapper.selectByPrimaryKey(Integer.parseInt(loanCtmCnt.getCntLx()));
                    object.put("platformName", appLoanPlatform.getPlatformName());
                    object.put("logoimageUrl", appLoanPlatform.getLogoimageUrl());
                    jsonArray.add(object);
                }
                detail.put("platformSize", appLoanCtmCntList.size());
                detail.put("userPlatformList", jsonArray);
            } else {
                detail.put("platformSize", 0);
                detail.put("userPlatformList", null);
                detail.put(Consts.RESULT, "0");
                detail.put(Consts.RESULT_NOTE, "该用户暂未在贷款平台上申请贷款");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return  detail;
    }

    //根据用户id和绑定的相应邮箱密码，与爬虫对接后判断邮箱是否真实存在，密码是否正确
    @Override
    public JSONObject ctmcntLogin(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String cntCommt = JsonUtil.getJStringAndCheck(params, "cntCommt", null, true, detail);  //登录邮箱地址
        String cntPass = JsonUtil.getJStringAndCheck(params, "cntPass", null, true, detail);  //登录邮箱密码
        String mailType = null;
        String captchaType = null;
        if(cntCommt.length()>50){
       	 detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的账号过长，请重新输入");
       }
       if(cntPass.length()>50){
      	 	detail.put(Consts.RESULT, ErrorCode.FAILED);
           detail.put(Consts.RESULT_NOTE, "您的密码过长，请重新输入");
      }
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            userId = appUser.getUserid() + "";
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
            String postRequest = null;
            String localMethod = "http://192.168.1.27:8080/rrhcore/emailLogin";
            List<Map<String, Object>> loanCnts = ifmBaseDictMapper.selectBaseDict("MCH EMAIL ENDS");
            ArrayList<String> strList = new ArrayList<String>();
            for(Map<String, Object> map :loanCnts){
                strList.add(StringUtil.toString(map.get("ITEM_VALUE")));
            }
            for(int i=0; i < strList.size(); i++){
                if(cntCommt.endsWith(strList.get(i))){
                    if(strList.get(i).indexOf("@qq.com") != -1){
                        mailType = "qq_mail";
                        captchaType ="qq";
                    }else if(strList.get(i).indexOf("@126.com") != -1){
                        mailType = "126_mail";
                        captchaType ="126";
                    }else if(strList.get(i).indexOf("@163.com") != -1){
                        mailType = "163_mail";
                        captchaType ="163";
                    }else if(strList.get(i).indexOf("@sina.com") != -1){
                        mailType = "sina_mail";
                        captchaType ="sina";
                    }
                }
            }
            String emailParam = cntCommt.substring(0,cntCommt.lastIndexOf('@'));
//            if(mailType != null && mailType != ""){
            if( null!=mailType&&!"".equals(mailType)){
                if(cntCommt.endsWith("@sina.com")){
                    postRequest = "http://192.168.1.96:8888/mail_login?username="+cntCommt+"&password="+cntPass+"&mail_type="+mailType+"&url="+localMethod;
                }else{
                    postRequest = "http://192.168.1.96:8888/mail_login?username="+emailParam+"&password="+cntPass+"&mail_type="+mailType+"&url="+localMethod;
                }
                String result = APIHttpClient.doPost(postRequest,300000);
                if("".equals(result)||null==result){
                    detail.put("code",5);
                    detail.put(Consts.RESULT, ErrorCode.FAILED);
                    detail.put(Consts.RESULT_NOTE, "网络不稳，请求超时");
                }else{
                    JSONObject jsonResult = (JSONObject)JSON.parse(result);
                    if(null==jsonResult){
                        detail.put("code",5);
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "网络不稳，请求超时");
                    }else{
                        String msg = StringUtil.nvl(jsonResult.get("msg"));
                        String code = StringUtil.nvl(jsonResult.get("code"));
                        if("1".equals(code)){
                            //判断用户传入的邮箱是否在运营商验证表app_loan_ctmcnt中存在，如果不存在则将这条邮箱登录记录存放在数据库该表中
                            List<AppLoanCtmCnt> loanCtmCntList = appLoanCtmCntMapper.chargeEmail(cntCommt);
                            if(loanCtmCntList.size() == 0){
                                AppLoanCtmCnt loanCtmCntInsert = new AppLoanCtmCnt();
                                loanCtmCntInsert.setCntCommt(cntCommt);//邮箱名称
                                loanCtmCntInsert.setCntPass(cntPass);//邮箱登录密码
                                loanCtmCntInsert.setCntType("5");//邮箱格式的运营商验证
                                loanCtmCntInsert.setCntDesc("1");//降序排序
                                loanCtmCntInsert.setApprId(appLoanAppl.getId());//用户注册后用户资料，贷款申请表的主键id
                                appLoanCtmCntMapper.insertSelective(loanCtmCntInsert);
                                detail.put(Consts.RESULT_NOTE,msg);
                                detail.put("code",code);
                            }else{
                                detail.put(Consts.RESULT_NOTE,"该邮箱已经被绑定");
                                detail.put("code",0);
                            }
                        }else if("3".equals(code)){
                            detail.put(Consts.RESULT_NOTE,msg);
                            detail.put("code",code);

                        }else{
                            detail.put(Consts.RESULT_NOTE,msg);
                            detail.put("code",code);
                        }
                    }
                }
            }else{
                detail.put(Consts.RESULT_NOTE,"邮箱格式不符合要求");
                detail.put("code",4);
            }
        }
        return  detail;
    }

    //查询指定用户绑定的所有邮箱信息
    @Override
    public JSONObject userEmails(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            userId = appUser.getUserid() + "";
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
            List<AppLoanCtmCnt> appLoanCtmCntList = appLoanCtmCntMapper.ctmcntEmail(appLoanAppl.getId());
            if(appLoanCtmCntList != null && appLoanCtmCntList.size() > 0){
                JSONArray jsonArray = new JSONArray();
                JSONObject object = null;
                for(AppLoanCtmCnt loanCtmCnt : appLoanCtmCntList){
                    object = new JSONObject();
                    object.put("cntCommt",loanCtmCnt.getCntCommt());
                    object.put("cntPass",loanCtmCnt.getCntPass());
                    object.put("emailId",loanCtmCnt.getId());
                    jsonArray.add(object);
                }
                detail.put("userEmailList",jsonArray);
            }else {
                detail.put("userEmailList", null);
                detail.put(Consts.RESULT, "0");
                detail.put(Consts.RESULT_NOTE, "该用户暂未绑定邮箱");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return  detail;
    }

    //查询相应用户指定邮箱序列号的邮箱和密码
    @Override
    public JSONObject emailDetailByCode(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String emailId = JsonUtil.getJStringAndCheck(params, "emailId", null, true, detail);  //传入用户绑定邮箱的序号
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            List<AppLoanCtmCnt> appLoanEmailAll = appLoanCtmCntMapper.AppLoanEmailAll();
//            if(appLoanEmailAll == null || appLoanEmailAll.size() ==0){
            if(null==appLoanEmailAll   || appLoanEmailAll.size() ==0){
                detail.put("cntCommt","");
                detail.put("cntPass","");
            }else {
//                if (emailId != "" && emailId != null) {
            	  if (null!=emailId&&"".equals(emailId)) {
                    AppLoanCtmCnt appLoanCtmCntEmail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(emailId));
//                    if(appLoanCtmCntEmail != null) {
                    if(null!=appLoanCtmCntEmail ) {
                        detail.put("cntCommt", appLoanCtmCntEmail.getCntCommt());
                        detail.put("cntPass", appLoanCtmCntEmail.getCntPass());
                    }else{
                        detail.put(Consts.RESULT, "0");
                        detail.put(Consts.RESULT_NOTE, "该邮箱不存在");
                    }
                } else {
                    detail.put(Consts.RESULT, "0");
                    detail.put(Consts.RESULT_NOTE, "邮箱的唯一性标识符不能为空");
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //修改邮箱登录密码
    @Override
    public JSONObject emailmPwdChange(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String emailId = JsonUtil.getJStringAndCheck(params, "emailId", null, true, detail);  //传入用户绑定邮箱的主键id
        String newPwd = JsonUtil.getJStringAndCheck(params, "newPwd", null, true, detail);  //传入更改的新密码
        if(newPwd.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的密码过长，请重新输入");
        }
        if(emailId == null || emailId != ""){
            emailId ="0";
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        String postRequest = null;
        if(null != appUser) {
            userId = appUser.getUserid() + "";
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
            String mailType = null;
            String captchaType = null;
            AppLoanCtmCnt appLoanEmail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(emailId));
            if(appLoanEmail != null){
                String cntCommt = appLoanEmail.getCntCommt();
                if(cntCommt.indexOf("@qq.com") != -1){
                    mailType = "qq_mail";
                    captchaType ="qq";
                }else if(cntCommt.indexOf("@126.com") != -1){
                    mailType = "126_mail";
                    captchaType ="126";
                }else if(cntCommt.indexOf("@163.com") != -1){
                    mailType = "163_mail";
                    captchaType ="163";
                }else if(cntCommt.indexOf("@sina.com") != -1){
                    mailType = "sina_mail";
                    captchaType ="sina";
                }
                String localMethod = "http://192.168.1.27:8080/rrhcore/emailLogin";
                String emailParam = cntCommt.substring(0,cntCommt.lastIndexOf('@'));
//              String postRequest = "http://credit.yuecaijf.com/login?username="+emailParam+"&password="+newPwd+"&mail_type="+mailType+"&url="+localMethod;
//              String result = APIHttpClient.doPost(postRequest,30000);
                if(cntCommt.endsWith("@sina.com")){
                    postRequest = "http://192.168.1.96:8888/mail_login?username="+cntCommt+"&password="+newPwd+"&mail_type="+mailType+"&url="+localMethod;
                }else{
                    postRequest = "http://192.168.1.96:8888/mail_login?username="+emailParam+"&password="+newPwd+"&mail_type="+mailType+"&url="+localMethod;
                }
                String result = APIHttpClient.doPost(postRequest,300000);
                if("".equals(result)||null==result){
                    detail.put("code",5);
                    detail.put(Consts.RESULT, ErrorCode.FAILED);
                    detail.put(Consts.RESULT_NOTE, "网络不稳，请求超时");
                }else {
                    JSONObject jsonResult = (JSONObject)JSON.parse(result);
                    if(null==jsonResult){
                        detail.put("code",5);
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "网络不稳，请求超时");
                    }else{
                        String msg = StringUtil.nvl(jsonResult.get("msg"));
                        String code = StringUtil.nvl(jsonResult.get("code"));
                        if("1".equals(code)){
                            appLoanEmail.setCntPass(newPwd);
                            appLoanEmail.setApprId(appLoanAppl.getId());
                            appLoanCtmCntMapper.updateByPrimaryKey(appLoanEmail);
                            detail.put(Consts.RESULT_NOTE,"密码修改成功");
                            detail.put("code",code);
                        }else if("3".equals(code)){
                            detail.put(Consts.RESULT_NOTE,msg);
                            detail.put("code",code);
                        }else{
                            detail.put(Consts.RESULT, ErrorCode.FAILED);
                            detail.put(Consts.RESULT_NOTE,msg);
                            detail.put("code",code);
                        }
                    }
                }
            }else{
                detail.put("code",4);
                detail.put(Consts.RESULT, ErrorCode.FAILED);
                detail.put(Consts.RESULT_NOTE, "该邮箱不存在");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户绑定所有信用卡查询
    @Override
    public JSONObject creditCardList(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //查询前台传入用户userId下在数据库中绑定的信用卡集合
            List<AppLoanCtmCnt> creditCardList = appLoanCtmCntMapper.creditCardList(appLoanAppl.getId());
            List<AppLoanCtmCnt> creditCardGroupIdList = appLoanCtmCntMapper.creditCardGroup(appLoanAppl.getId());
//          if(creditCardList.size()>0 && creditCardList!=null) {
            if(null!=creditCardList&&creditCardList.size()>0  ) {
                JSONArray jsonArray = new JSONArray();
                JSONObject object = null;
                for (AppLoanCtmCnt creditCard : creditCardGroupIdList) {
                    object = new JSONObject();
                    AppLoanCtmCnt appLoanCtmCntCreditCard = appLoanCtmCntMapper.selectByPrimaryKey(creditCard.getId());
                    object.put("cmtCommt",appLoanCtmCntCreditCard.getCntCommt());
                    object.put("creditCardId",creditCard.getId());
                    jsonArray.add(object);
                }
                detail.put("customName",customName);
                detail.put("creditCardSize",creditCardGroupIdList.size());
                detail.put("creditCardList",jsonArray);
            }else{
                JSONArray jsonArrayNull = new JSONArray();
                detail.put("customName",customName);
                detail.put("creditCardSize",0);
                detail.put("creditCardList",jsonArrayNull);
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //根据用户userId和信用卡编号creditCardId获取信用卡详情
    @Override
    public JSONObject creditCardDetail(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String creditCardId = JsonUtil.getJStringAndCheck(params, "creditCardId", null, false, detail);  //传入用户绑定信用卡的主键
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            List<AppLoanCtmCnt> appLoanCtmCntAll = appLoanCtmCntMapper.creditCardAll();
//            if(appLoanCtmCntAll == null || appLoanCtmCntAll.size() == 0){
            if(null==appLoanCtmCntAll  || appLoanCtmCntAll.size() == 0){
                detail.put("customName", customName);
                detail.put("cntCommt", "");
                detail.put("cntPass", "");
                detail.put("bankName", "");
                detail.put("mobilePhone", "");
            }else {
//              if (creditCardId != null && creditCardId != "") {
                if (null!=creditCardId  && !"".equals(creditCardId)  ) {
                    AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(creditCardId));
//                    if (appLoanCtmCntDetail != null) {
                    if (null!=appLoanCtmCntDetail  ) {
                        //获取数据字典中所有银行卡信息
                        String bankName = null;
                        List<Map<String, Object>> loanCnts = ifmBaseDictMapper.selectBaseDict("BANK_NAME");
                        for (Map<String, Object> map : loanCnts) {
                            if (appLoanCtmCntDetail.getCntLx().equals(map.get("ITEM_KEY"))) {
                                bankName = StringUtil.nvl(map.get("ITEM_VALUE"));
                            }
                        }
//                        if (bankName == null || bankName == "") {
                        if (null==bankName   || "".equals(bankName)) {
                            bankName = "";
                        }
                        detail.put("customName", customName);
                        detail.put("cntCommt", appLoanCtmCntDetail.getCntCommt());
                        detail.put("cntPass", appLoanCtmCntDetail.getCntPass());
                        detail.put("bankName", bankName);
                        detail.put("mobilePhone", appLoanCtmCntDetail.getMobilePhone());
                    } else {
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "该信用卡不存在");
                    }
                } else {
                    detail.put(Consts.RESULT, "0");
                    detail.put(Consts.RESULT_NOTE, "信用卡当前使用用户:");
                    detail.put("customName", customName);
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户信用卡的添加和修改
    @Override
    public JSONObject creditCardSaveOrUpdate(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String bankCardNo = JsonUtil.getJStringAndCheck(params, "bankCardNo", null, true, detail); //信用卡账号
        String bankCardPwd = JsonUtil.getJStringAndCheck(params, "bankCardPwd", null, true, detail);  //信用卡网银登录密码
        String bankName = JsonUtil.getJStringAndCheck(params, "bankName", null, true, detail); //银行卡类型
        String mobilePhone = JsonUtil.getJStringAndCheck(params, "mobilePhone", null, true, detail);  //开卡预留手机号
        String creditCardId = JsonUtil.getJStringAndCheck(params, "creditCardId", null, true, detail);  //信用卡的下标号码
        //数据库中信用卡CntCommt字段长度50 ，需判断
        if(bankCardNo.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "信用卡长度过长，请重新输入");
            return detail;
        }
        if(bankCardPwd.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "信用卡密码长度过长，请重新输入");
            return detail;
        }
        

//        if(creditCardId == null || creditCardId == ""){
        if(null==creditCardId  || "".equals(creditCardId)){
            creditCardId = "0";
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //获取数据字典中所有银行卡信息
            String bankCardItemKey = "";
            List<Map<String, Object>> loanCnts = ifmBaseDictMapper.selectBaseDict("BANK_NAME");
            for(Map<String, Object> map :loanCnts){
                if(bankName.equals(map.get("ITEM_VALUE"))){
                    bankCardItemKey = StringUtil.nvl(map.get("ITEM_KEY"));
                }
            }
//            if(bankCardItemKey == ""){
            if("".equals(bankCardItemKey)){
                detail.put("updateOrSaveError", "修改保存操作失败");
                detail.put(Consts.RESULT, ErrorCode.FAILED);
                detail.put(Consts.RESULT_NOTE, "系统不支持该类型银行卡");
            }else {
                //查询相应用户user_id绑定过的信用卡的个数是否超过传入的下标编号，如果没有超过则修改原有的相应信息，如果超过则添加绑定账号
                //信用卡修改时将原有的信用卡信息在数据库中覆盖，添加时将新信用卡信息添加在数据库表app_loan_ctmcnt中
                List<AppLoanCtmCnt> creditCardListOrigin = appLoanCtmCntMapper.creditCardInsertList(appLoanAppl.getId());
//                if (bankCardNo != null && bankCardNo != "" && bankCardPwd != null && bankCardPwd != "") {
                if (null!=bankCardNo &&!"".equals(bankCardNo) && null!=bankCardPwd && !"".equals(bankCardPwd)) {                
                    if(!creditCardId.equals("0")){
                        AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(creditCardId));
                        if (appLoanCtmCntDetail != null) {
                            String str = appLoanCtmCntDetail.getGroupId();
                            AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                            newAppLoanCtmCnt.setCntCommt(bankCardNo);
                            newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                            newAppLoanCtmCnt.setCntPass(bankCardPwd);
                            newAppLoanCtmCnt.setMobilePhone(mobilePhone);
                            newAppLoanCtmCnt.setCntType("6");
                            newAppLoanCtmCnt.setCntDesc("1");
                            newAppLoanCtmCnt.setCntLx(bankCardItemKey);
                            newAppLoanCtmCnt.setStatus(2);
                            newAppLoanCtmCnt.setGroupId(str);
                            appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                            detail.put("updateSuccess", "修改成功");
                        }else{
                            detail.put("updateError", "修改失败");
                            detail.put(Consts.RESULT, ErrorCode.FAILED);
                            detail.put(Consts.RESULT_NOTE, "需要修改的信用卡不存在");
                        }
                    } else {
                        //查询相应用户userid下的所有信用卡的添加信息 如果不小于3则停止添加
                        if (creditCardListOrigin.size() < 3) {
                            AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                            String uuid = UUID.randomUUID().toString();
                            newAppLoanCtmCnt.setCntCommt(bankCardNo);
                            newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                            newAppLoanCtmCnt.setCntPass(bankCardPwd);
                            newAppLoanCtmCnt.setMobilePhone(mobilePhone);
                            newAppLoanCtmCnt.setCntType("6");
                            newAppLoanCtmCnt.setCntDesc("1");
                            newAppLoanCtmCnt.setCntLx(bankCardItemKey);
                            newAppLoanCtmCnt.setStatus(1);
                            newAppLoanCtmCnt.setGroupId(uuid);
                            appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                            detail.put(Consts.RESULT, "0");
                            detail.put("saveSuccess", "保存成功");
                        } else {
                            detail.put("saveError", "保存失败");
                            detail.put(Consts.RESULT, ErrorCode.FAILED);
                            detail.put(Consts.RESULT_NOTE, "同一个用户不能绑定3张以上的信用卡");
                        }
                    }
                } else {
                    detail.put("updateOrSaveError", "修改保存操作失败");
                    detail.put(Consts.RESULT, ErrorCode.FAILED);
                    detail.put(Consts.RESULT_NOTE, "信用卡账号和密码不能为空");
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户绑定所有储蓄卡查询
    @Override
    public JSONObject depositCardList(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //查询前台传入用户userId下在数据库中绑定的储蓄卡集合
            List<AppLoanCtmCnt> depositCardList = appLoanCtmCntMapper.depositCardList(appLoanAppl.getId());
            List<AppLoanCtmCnt> depositCardGroupIdList = appLoanCtmCntMapper.depositCardGroup(appLoanAppl.getId());
//            if(depositCardList.size()>0 && depositCardList!=null) {
            if(null!=depositCardList&&depositCardList.size()>0) {
                JSONArray jsonArray = new JSONArray();
                JSONObject object = null;
                for (AppLoanCtmCnt depositCard : depositCardGroupIdList) {
                    object = new JSONObject();
                    AppLoanCtmCnt appLoanCtmCntDepositCard = appLoanCtmCntMapper.selectByPrimaryKey(depositCard.getId());
                    object.put("cmtCommt",appLoanCtmCntDepositCard.getCntCommt());
                    object.put("depositCardId",depositCard.getId());
                    jsonArray.add(object);
                }
                detail.put("customName",customName);
                detail.put("depositCardSize",depositCardGroupIdList.size());
                detail.put("depositCardList",jsonArray);
            }else{
                JSONArray jsonArrayNull = new JSONArray();
                detail.put("customName",customName);
                detail.put("depositCardSize",0);
                detail.put("depositCardList",jsonArrayNull);
                detail.put(Consts.RESULT, "0");
                detail.put(Consts.RESULT_NOTE, "当前用户暂未绑定储蓄卡");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //根据用户userId和储蓄卡编号depositCardId获取储蓄卡详情
    @Override
    public JSONObject depositCardDetail(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String depositCardId = JsonUtil.getJStringAndCheck(params, "depositCardId", null, false, detail);  //传入用户绑定储蓄卡的序号
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            List<AppLoanCtmCnt> appLoanCtmCntAll = appLoanCtmCntMapper.depositCardAll();
            if(appLoanCtmCntAll.size() == 0 || appLoanCtmCntAll == null){
                detail.put("customName", customName);
                detail.put("cntCommt", "");
                detail.put("cntPass", "");
                detail.put("bankName", "");
                detail.put("mobilePhone", "");
            }else {
//                if (depositCardId != null && depositCardId != "") {
            if (null!= depositCardId  && !"".equals(depositCardId)) {
                    AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(depositCardId));
                    if (appLoanCtmCntDetail != null) {
                        //获取数据字典中所有银行卡信息
                        String bankName = null;
                        List<Map<String, Object>> loanCnts = ifmBaseDictMapper.selectBaseDict("BANK_NAME");
                        for (Map<String, Object> map : loanCnts) {
                            if (appLoanCtmCntDetail.getCntLx().equals(map.get("ITEM_KEY"))) {
                                bankName = StringUtil.nvl(map.get("ITEM_VALUE"));
                            }
                        }
//                        if (bankName == null || bankName == "") {
                        if ( null ==bankName || "".equals(bankName)) {
                            bankName = "";
                        }
                        detail.put("customName", customName);
                        detail.put("cntCommt", appLoanCtmCntDetail.getCntCommt());
                        detail.put("cntPass", appLoanCtmCntDetail.getCntPass());
                        detail.put("bankName", bankName);
                        detail.put("mobilePhone", appLoanCtmCntDetail.getMobilePhone());
                    } else {
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "该储蓄卡不存在");
                    }
                } else {
                    detail.put(Consts.RESULT, "0");
                    detail.put(Consts.RESULT_NOTE, "储蓄卡当前使用用户:");
                    detail.put("customName", customName);
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户储蓄卡的添加和修改
    @Override
    public JSONObject depositCardSaveOrUpdate(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String bankCardNo = JsonUtil.getJStringAndCheck(params, "bankCardNo", null, true, detail); //储蓄卡账号
        String bankCardPwd = JsonUtil.getJStringAndCheck(params, "bankCardPwd", null, true, detail);  //储蓄卡网银登录密码
        String bankName = JsonUtil.getJStringAndCheck(params, "bankName", null, true, detail); //银行卡类型
        String mobilePhone = JsonUtil.getJStringAndCheck(params, "mobilePhone", null, true, detail);  //开卡预留手机号
        String depositCardId = JsonUtil.getJStringAndCheck(params, "depositCardId", null, false, detail);  //储蓄卡的下标号码
        if(bankCardNo.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "信用卡长度过长，请重新输入");
            return detail;
        }
        if(bankCardPwd.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "信用卡密码长度过长，请重新输入");
            return detail;
        }
        
        if(depositCardId == null || depositCardId == ""){
            depositCardId = "0";
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //获取数据字典中所有银行卡信息
            String bankCardItemKey = "";
            List<Map<String, Object>> loanCnts = ifmBaseDictMapper.selectBaseDict("BANK_NAME");
            for(Map<String, Object> map :loanCnts){
                if(bankName.equals(map.get("ITEM_VALUE"))){
                    bankCardItemKey = StringUtil.nvl(map.get("ITEM_KEY"));
                }
            }
            if(bankCardItemKey == ""){
                detail.put("updateOrSaveError", "修改保存操作失败");
                detail.put(Consts.RESULT, ErrorCode.FAILED);
                detail.put(Consts.RESULT_NOTE, "系统不支持该类型银行卡");
            }else {
                //查询相应用户user_id绑定过的储蓄卡的个数是否超过传入的下标编号，如果没有超过则修改原有的相应信息，如果超过则添加绑定账号
                //储蓄卡修改时将原有的储蓄卡信息在数据库中覆盖，添加时将新储蓄卡信息添加在数据库表app_loan_ctmcnt中
                List<AppLoanCtmCnt> depositCardListOrigin = appLoanCtmCntMapper.depositCardInsertList(appLoanAppl.getId());
//                if (bankCardNo != null && bankCardNo != "" && bankCardPwd != null && bankCardPwd != "") {
                if (null!=bankCardNo&&!"".equals(bankCardNo)&&null!=bankCardPwd&&!"".equals(bankCardPwd)) {
//                    if (!depositCardId.equals("0")) {
                	  if (!"0".equals(depositCardId)) {
                        AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(depositCardId));
//                        if (appLoanCtmCntDetail != null) {
                        if (null!=appLoanCtmCntDetail ) {
                            String str = appLoanCtmCntDetail.getGroupId();
                            AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                            newAppLoanCtmCnt.setCntCommt(bankCardNo);
                            newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                            newAppLoanCtmCnt.setCntPass(bankCardPwd);
                            newAppLoanCtmCnt.setMobilePhone(mobilePhone);
                            newAppLoanCtmCnt.setCntType("11");
                            newAppLoanCtmCnt.setCntDesc("1");
                            newAppLoanCtmCnt.setCntLx(bankCardItemKey);
                            newAppLoanCtmCnt.setStatus(2);
                            newAppLoanCtmCnt.setGroupId(str);
                            appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                            detail.put("updateSuccess", "修改成功");
                        } else {
                            detail.put("updateError", "修改失败");
                            detail.put(Consts.RESULT, ErrorCode.FAILED);
                            detail.put(Consts.RESULT_NOTE, "需要修改的储蓄卡不存在");
                        }
                    }else {
                        //查询相应用户userid下的所有储蓄卡信息 如果不小于3则停止添加
                        if (depositCardListOrigin.size() < 3) {
                            String uuid = UUID.randomUUID().toString();
                            AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                            newAppLoanCtmCnt.setCntCommt(bankCardNo);
                            newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                            newAppLoanCtmCnt.setCntPass(bankCardPwd);
                            newAppLoanCtmCnt.setMobilePhone(mobilePhone);
                            newAppLoanCtmCnt.setCntType("11");
                            newAppLoanCtmCnt.setCntDesc("1");
                            newAppLoanCtmCnt.setCntLx(bankCardItemKey);
                            newAppLoanCtmCnt.setStatus(1);
                            newAppLoanCtmCnt.setGroupId(uuid);
                            appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                            detail.put(Consts.RESULT, "0");
                            detail.put("saveSuccess", "保存成功");
                        } else {
                            detail.put("saveError", "保存失败");
                            detail.put(Consts.RESULT, ErrorCode.FAILED);
                            detail.put(Consts.RESULT_NOTE, "同一个用户不能绑定3张以上的储蓄卡");
                        }
                    }
                } else {
                    detail.put("updateOrSaveError", "修改保存操作失败");
                    detail.put(Consts.RESULT, ErrorCode.FAILED);
                    detail.put(Consts.RESULT_NOTE, "储蓄卡账号和密码不能为空");
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户绑定所有社保卡查询
    @Override
    public JSONObject socialSecurityList(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //查询前台传入用户userId下在数据库中绑定的社保信息集合
            List<AppLoanCtmCnt> socialSecurityList = appLoanCtmCntMapper.socialSecurityList(appLoanAppl.getId());
            List<AppLoanCtmCnt> socialSecurityGroupIdList = appLoanCtmCntMapper.socialSecurityGroup(appLoanAppl.getId());
//            if(socialSecurityList.size()>0 && socialSecurityList!=null) {
            if(null!=socialSecurityList&&socialSecurityList.size()>0 ) {
                JSONArray jsonArray = new JSONArray();
                JSONObject object = null;
                for (AppLoanCtmCnt socialSecurity : socialSecurityGroupIdList) {
                    object = new JSONObject();
                    AppLoanCtmCnt appLoanCtmCntSocialSecurity = appLoanCtmCntMapper.selectByPrimaryKey(socialSecurity.getId());
                    object.put("cmtCommt",appLoanCtmCntSocialSecurity.getCntCommt());
                    object.put("SecurityId",socialSecurity.getId());
                    jsonArray.add(object);
                }
                detail.put("customName",customName);
                detail.put("socialSecuritySize",socialSecurityGroupIdList.size());
                detail.put("socialSecurityList",jsonArray);
            }else{
                JSONArray jsonArrayNull = new JSONArray();
                detail.put("customName",customName);
                detail.put("socialSecuritySize",0);
                detail.put("socialSecurityList",jsonArrayNull);
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //根据用户userId和社保卡号SecurityId获取绑定社保详情
    @Override
    public JSONObject socialSecurityDetail(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String securityId = JsonUtil.getJStringAndCheck(params, "SecurityId", null, false, detail);  //传入用户绑定社保卡的序号
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            List<AppLoanCtmCnt> appLoanCtmCntAll = appLoanCtmCntMapper.socialSecurityAll();
            if( null== appLoanCtmCntAll || appLoanCtmCntAll.size() == 0){
                detail.put("customName", customName);
                detail.put("loginSecurityNo", "");
                detail.put("loginPassword", "");
                detail.put("province", "");
                detail.put("city", "");
            }else {
//                if (securityId != null && securityId != "") {
            	if (null!=securityId&&!"".equals(securityId)) {
                    AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(securityId));
//                    if (appLoanCtmCntDetail != null) {
                    if (null!=appLoanCtmCntDetail ) {
                        detail.put("customName", customName);
                        detail.put("loginSecurityNo", appLoanCtmCntDetail.getCntCommt());
                        detail.put("loginPassword", appLoanCtmCntDetail.getCntPass());
                        detail.put("province", appLoanCtmCntDetail.getProvince());
                        detail.put("city", appLoanCtmCntDetail.getCity());
                    }
//                    else {
//                        detail.put(Consts.RESULT, ErrorCode.FAILED);
//                        detail.put(Consts.RESULT_NOTE, "该社保账号不存在");
//                    }
                } else {
                    detail.put(Consts.RESULT, "0");
                    detail.put(Consts.RESULT_NOTE, "当前绑定社保用户:");
                    detail.put("customName", customName);
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户社保信息的添加和修改
    @Override
    public JSONObject socialSecuritySaveOrUpdate(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String loginSecurityNo = JsonUtil.getJStringAndCheck(params, "loginSecurityNo", null, true, detail); //社保卡账号
        String loginPassword = JsonUtil.getJStringAndCheck(params, "loginPassword", null, true, detail);  //社保卡登录密码
        String province = JsonUtil.getJStringAndCheck(params, "province", null, true, detail); //社保所在省
        String city = JsonUtil.getJStringAndCheck(params, "city", null, true, detail);  //社保所在市
        String securityId = JsonUtil.getJStringAndCheck(params, "SecurityId", null, false, detail);  //社保列表传入的下标代码
//        if(securityId == null || securityId == ""){
        if(null==securityId || "".equals(securityId)){
            securityId = "0";
        }
        if(loginSecurityNo.length()>15){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的社保账号过长，长度应在8~15位之间");
            return detail;
        }
        if(loginPassword.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的社保密码过长，请重新输入");
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //查询相应用户user_id绑定过的社保信息的个数是否超过传入的下标编号，如果没有超过则修改原有的相应信息，如果超过则添加绑定账号
            //社保信息修改时将原有的社保信息在数据库中覆盖，添加时将新社保信息添加在数据库表app_loan_ctmcnt中

            List<AppLoanCtmCnt> socialSecurityListOrigin = appLoanCtmCntMapper.socialSecurityInsertList(appLoanAppl.getId());
            if (loginSecurityNo != null && loginSecurityNo != "" && loginPassword != null && loginPassword != "") {
//                if (!securityId.equals("0")) {
            	if (!"0".equals(securityId)) {
                    AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(securityId));
//                    if (appLoanCtmCntDetail != null) {
                    if (null!=appLoanCtmCntDetail) {
                        String str = appLoanCtmCntDetail.getGroupId();
                        AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                        newAppLoanCtmCnt.setCntCommt(loginSecurityNo);
                        newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                        newAppLoanCtmCnt.setCntPass(loginPassword);
                        newAppLoanCtmCnt.setCntType("12");
                        newAppLoanCtmCnt.setCntDesc("1");
                        newAppLoanCtmCnt.setProvince(province);
                        newAppLoanCtmCnt.setCity(city);
                        newAppLoanCtmCnt.setStatus(2);
                        newAppLoanCtmCnt.setGroupId(str);
                        appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                        detail.put("updateSuccess", "修改成功");
                    }else {
                        detail.put("updateError", "修改失败");
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "需要修改的社保账号不存在");
                    }
                } else {
                    //查询相应用户userid下的所有社保信息 如果不小于3则停止添加
                    if (socialSecurityListOrigin.size() < 3) {
                        AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                        String uuid = UUID.randomUUID().toString();
                        newAppLoanCtmCnt.setCntCommt(loginSecurityNo);
                        newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                        newAppLoanCtmCnt.setCntPass(loginPassword);
                        newAppLoanCtmCnt.setCntType("12");
                        newAppLoanCtmCnt.setCntDesc("1");
                        newAppLoanCtmCnt.setProvince(province);
                        newAppLoanCtmCnt.setCity(city);
                        newAppLoanCtmCnt.setStatus(1);
                        newAppLoanCtmCnt.setGroupId(uuid);
                        appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                        detail.put(Consts.RESULT, "0");
                        detail.put("saveSuccess", "保存成功");
                    } else {
                        detail.put("saveError", "保存失败");
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "同一个用户不能绑定3个以上的社保账号");
                    }
                }
            } else {
                detail.put("updateOrSaveError", "修改保存操作失败");
                detail.put(Consts.RESULT, ErrorCode.FAILED);
                detail.put(Consts.RESULT_NOTE, "社保账号和密码不能为空");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户绑定所有的公积金账号查询
    @Override
    public JSONObject housingFundList(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //查询前台传入用户userId下在数据库中绑定的公积金账号集合
            List<AppLoanCtmCnt> housingFundList = appLoanCtmCntMapper.housingFundList(appLoanAppl.getId());//根据appLoanAppl.getId()查询该用户是否有公积金账号
            List<AppLoanCtmCnt> housingFundGroupIdList = appLoanCtmCntMapper.housingFundGroup(appLoanAppl.getId());//根据group去区分最新的公积金条目
//            if(housingFundList!=null&& housingFundList.size()>0 ) {
            if(null!=housingFundList&& housingFundList.size()>0 ) {
                JSONArray jsonArray = new JSONArray();
                JSONObject object = null;
                for (AppLoanCtmCnt housingFund : housingFundGroupIdList) {
                    object = new JSONObject();
                    AppLoanCtmCnt appLoanCtmCntHousingFund = appLoanCtmCntMapper.selectByPrimaryKey(housingFund.getId());
                    object.put("cmtCommt",appLoanCtmCntHousingFund.getCntCommt());
                    object.put("fundId",housingFund.getId());
                    jsonArray.add(object);
                }
                detail.put("customName",customName);
                detail.put("housingFundSize",housingFundGroupIdList.size());
                detail.put("housingFundList",jsonArray);
            }else{
                JSONArray jsonArrayNull = new JSONArray();
                detail.put("customName",customName);
                detail.put("housingFundSize",0);
                detail.put("housingFundList",jsonArrayNull);
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //根据用户userId和公积金账号FundId获取绑定公积金详情
    @Override
    public JSONObject housingFundDetail(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String fundId = JsonUtil.getJStringAndCheck(params, "fundId", null, false, detail);  //传入用户绑定公积金账号
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            List<AppLoanCtmCnt> appLoanCtmCntAll = appLoanCtmCntMapper.housingFundAll();
//            if(appLoanCtmCntAll.size() == 0 || appLoanCtmCntAll == null){
            if(appLoanCtmCntAll.size() == 0 || null== appLoanCtmCntAll ){
                detail.put("customName", customName);
                detail.put("loginFundNo", "");
                detail.put("loginPassword", "");
                detail.put("province", "");
                detail.put("city", "");
            }else {
                if ( null!=fundId  && !"".equals(fundId) ) {
                    AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(fundId));
                    if (appLoanCtmCntDetail != null) {
                        detail.put("customName", customName);
                        detail.put("loginFundNo", appLoanCtmCntDetail.getCntCommt());
                        detail.put("loginPassword", appLoanCtmCntDetail.getCntPass());
                        detail.put("province", appLoanCtmCntDetail.getProvince());
                        detail.put("city", appLoanCtmCntDetail.getCity());
                    } else {
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "该公积金账号不存在");
                    }
                } else {
                    detail.put(Consts.RESULT, "0");
                    detail.put(Consts.RESULT_NOTE, "当前绑定公积金用户:");
                    detail.put("customName", customName);
                }
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户公积金账号信息的添加和修改
    @Override
    public JSONObject housingFundSaveOrUpdate(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        String loginFundNo = JsonUtil.getJStringAndCheck(params, "loginFundNo", null, true, detail); //公积金账号
        String loginPassword = JsonUtil.getJStringAndCheck(params, "loginPassword", null, true, detail);  //公积金登录密码
        String province = JsonUtil.getJStringAndCheck(params, "province", null, true, detail); //公积金所在省
        String city = JsonUtil.getJStringAndCheck(params, "city", null, true, detail);  //公积金所在市
        String fundId = JsonUtil.getJStringAndCheck(params, "fundId", null, false, detail);  //公积金列表传入的下标代码
        if(loginFundNo.length()>15){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的公积金账号过长，请重新输入");
            return detail;
        }
        if(loginPassword.length()>50){
        	detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "您的密码过长，请重新输入");
            return detail;
        }
        
        if(fundId == null || fundId == ""){
            fundId = "0";
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户身份信息
            AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
            //获取前台传入用户userId对应的数据库姓名
            String customName = appLoanCtm.getCustomName();
            //查询相应用户user_id绑定过的公积金账号的个数是否超过传入的下标编号，如果没有超过则修改原有的相应信息，如果超过则添加绑定账号
            //公积金修改时将原有的公积金信息在数据库中覆盖，添加时将新公积金信息添加在数据库表app_loan_ctmcnt中
            List<AppLoanCtmCnt> housingFundListOrigin = appLoanCtmCntMapper.housingFundInsertList(appLoanAppl.getId());
//            if (loginFundNo != null && loginFundNo != "" && loginPassword != null && loginPassword != "") {
            if ( null!=loginFundNo  && !"".equals(loginFundNo)  && null!=loginPassword  && !"".equals(loginPassword)) {
//                if(!fundId.equals("0")){
            	if(!"0".equals(fundId)){
                    AppLoanCtmCnt appLoanCtmCntDetail = appLoanCtmCntMapper.selectByPrimaryKey(Integer.parseInt(fundId));
//                    if(appLoanCtmCntDetail != null){
                    if(null!=appLoanCtmCntDetail  ){
                        String str = appLoanCtmCntDetail.getGroupId();
                        AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                        newAppLoanCtmCnt.setCntCommt(loginFundNo);
                        newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                        newAppLoanCtmCnt.setCntPass(loginPassword);
                        newAppLoanCtmCnt.setCntType("7");
                        newAppLoanCtmCnt.setCntDesc("1");
                        newAppLoanCtmCnt.setProvince(province);
                        newAppLoanCtmCnt.setCity(city);
                        newAppLoanCtmCnt.setStatus(2);
                        newAppLoanCtmCnt.setGroupId(str);
                        appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                        detail.put("updateSuccess", "修改成功");
                    }else {
                        detail.put("updateError", "修改失败");
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "需要修改的公积金账号不存在");
                    }
                } else {
                    //查询相应用户userid下的所有公积金信息 如果不小于3则停止添加
                    if (housingFundListOrigin.size() < 3) {
                        AppLoanCtmCnt newAppLoanCtmCnt = new AppLoanCtmCnt();
                        String uuid = UUID.randomUUID().toString();
                        newAppLoanCtmCnt.setCntCommt(loginFundNo);
                        newAppLoanCtmCnt.setApprId(appLoanAppl.getId());
                        newAppLoanCtmCnt.setCntPass(loginPassword);
                        newAppLoanCtmCnt.setCntType("7");
                        newAppLoanCtmCnt.setCntDesc("1");
                        newAppLoanCtmCnt.setProvince(province);
                        newAppLoanCtmCnt.setCity(city);
                        newAppLoanCtmCnt.setStatus(1);
                        newAppLoanCtmCnt.setGroupId(uuid);
                        appLoanCtmCntMapper.insertSelective(newAppLoanCtmCnt);
                        detail.put(Consts.RESULT, "0");
                        detail.put("saveSuccess", "保存成功");
                    } else {
                        detail.put("saveError", "保存失败");
                        detail.put(Consts.RESULT, ErrorCode.FAILED);
                        detail.put(Consts.RESULT_NOTE, "同一个用户不能绑定3个以上的公积金账号");
                    }
                }
            } else {
                detail.put("updateOrSaveError", "修改保存操作失败");
                detail.put(Consts.RESULT, ErrorCode.FAILED);
                detail.put(Consts.RESULT_NOTE, "公积金账号和密码不能为空");
            }
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }

    //用户可提额的总金额和各种贷款平台渠道的绑定进度展示
    @Override
    public JSONObject userProgressRate(JSONObject params){
        JSONObject detail = new JSONObject();
        String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //传入用户ID
        if (detail.containsKey(Consts.RESULT)) {
            return detail;
        }
        AppUser appUser = appUserMapper.selectByMchVersion(userId);
        //将所传递的各贷款平台占用率用百分比表示,百分比保留两位小数
        DecimalFormat df = new DecimalFormat("0.00");
        if(null != appUser) {
            String userName = appUser.getUserName();
            //根据用户用户名获取用户资料，贷款申请信息
            AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userName);
            //获取用户的总提额度对象（查看用户是否授信）
            APPCredit appCredit = appCreditMapper.selectByApprId(appLoanAppl.getId());
            //获取各贷款申请渠道用户绑定卡的数量，计算百分比后作为接口的返回值传送给前台
            int platformNum = appLoanCtmCntMapper.countPlatformsByApprId(appLoanAppl.getId());
            String platformPercentage = null, emailPercentage = null, qqPercentage = null, weChatPercentage = null;
            switch (platformNum){
                case 0:
                    platformPercentage = "0.00";
                    break;
                case 1:
                    platformPercentage = "0.33";
                    break;
                case 2:
                    platformPercentage = "0.66";
                    break;
                default:
                    platformPercentage = "0.90";
                    break;
            }
//            int emailNum = appLoanCtmCntMapper.countEmailByApprId(appLoanAppl.getId());
            List<AppLoanCtmCnt> emailNum1=appLoanCtmCntMapper.ctmcntEmail(appLoanAppl.getId());
            HashSet<Integer> emailset=new HashSet<Integer>();
            for(int i=0; i<emailNum1.size();i++){
            	emailset.add(emailNum1.get(i).getPostboxType());
            }
            int emailNum=emailset.size();
            switch (emailNum){
                case 0:
                    emailPercentage = "0.0";
                    break;
                case 1:
                    emailPercentage = "0.33";
                    break;
                case 2:
                    emailPercentage = "0.66";
                    break;
                default:
                    emailPercentage = "0.90";
                    break;
            }
            int creditCardNum = appLoanCtmCntMapper.countCreditCardByApprId(appLoanAppl.getId());
            String creditCardPercentage = null;
            switch (creditCardNum){
            case 0:
            	creditCardPercentage = "0.0";
                break;
            case 1:
            	creditCardPercentage = "0.33";
                break;
            case 2:
            	creditCardPercentage = "0.66";
                break;
            default:
            	creditCardPercentage = "1.00";
                break;
        }
            int depositCardNum = appLoanCtmCntMapper.countDepositCardByApprId(appLoanAppl.getId());
            String depositCardPercentage =null;
            switch (depositCardNum){
            case 0:
            	depositCardPercentage = "0.0";
                break;
            case 1:
            	depositCardPercentage = "0.33";
                break;
            case 2:
            	depositCardPercentage = "0.66";
                break;
            default:
            	depositCardPercentage = "1.00";
                break;
        }
            int socialSecurityNum = appLoanCtmCntMapper.countSocialSecurityByApprId(appLoanAppl.getId());
            String socialSecurityPercentage =null;
            switch (socialSecurityNum){
            case 0:
            	socialSecurityPercentage = "0.0";
                break;
            case 1:
            	socialSecurityPercentage = "0.33";
                break;
            case 2:
            	socialSecurityPercentage = "0.66";
                break;
            default:
            	socialSecurityPercentage = "1.00";
                break;
        }
            int housingFundNum = appLoanCtmCntMapper.countHousingFundByApprId(appLoanAppl.getId());
            String housingFundPercentage = null;
            switch (housingFundNum){
            case 0:
            	housingFundPercentage = "0.0";
                break;
            case 1:
            	housingFundPercentage = "0.33";
                break;
            case 2:
            	housingFundPercentage = "0.66";
                break;
            default:
            	housingFundPercentage = "1.00";
                break;
        }
            List<AppIncreaseAmtInfo> qqAuthorizeList = appIncreaseAmtInfoMapper.qqAuthorizeByApprId(appLoanAppl.getId());
            List<AppIncreaseAmtInfo> wechatAuthorizeList = appIncreaseAmtInfoMapper.weChatAuthorizeByApprId(appLoanAppl.getId());
            if(qqAuthorizeList.size() > 0){
                qqPercentage = "1.00";
            }else{
                qqPercentage = "0.00";
            }
            if(wechatAuthorizeList.size() > 0){
                weChatPercentage = "1.00";
            }else{
                weChatPercentage = "0.00";
            }
            //TODO 授信判断 1.如果用户授信则取出授信金额格式化  2.如果用户未授信则，只返回可用额度为0.00 
            if(null!=appCredit){
            	//TODO df.format(appCredit.getCreditAmt() 格式化取出数据为小数点后2位
            	detail.put("totalCredit",String.valueOf(df.format(appCredit.getCreditAmt())));
            }else{
            	detail.put("totalCredit",String.valueOf(df.format(0)));
            }
            JSONObject platformObject = new JSONObject();
            IfmBaseDict ifmBaseDict=ifmBaseDictMapper.selectbyMention("MCH_TE_ENTRANCELINK");
            ArrayList<JSONObject> showMentionList = new ArrayList<JSONObject>();
            platformObject.put("name","贷款平台");
            platformObject.put("percentage",platformPercentage);
            platformObject.put("entranceLink",ifmBaseDict.getItemValue()+"/platformBinding.html");
            platformObject.put("authorizeList",ifmBaseDict.getItemValue()+"/platLogin.html");
            platformObject.put("authorizeShowList",ifmBaseDict.getItemValue()+"/platShow.html");
            showMentionList.add(platformObject);
            JSONObject emailObject = new JSONObject();
            emailObject.put("name","邮箱");
            emailObject.put("percentage",emailPercentage);
            emailObject.put("entranceLink","");
            emailObject.put("authorizeList","");
            emailObject.put("authorizeShowList","");
            showMentionList.add(emailObject);
            JSONObject depositCardObject = new JSONObject();
            depositCardObject.put("name","储蓄卡");
            depositCardObject.put("percentage",depositCardPercentage);
            depositCardObject.put("entranceLink","");
            depositCardObject.put("authorizeList",ifmBaseDict.getItemValue()+"/savingsDepositCard.html");
            depositCardObject.put("authorizeShowList",ifmBaseDict.getItemValue()+"/savingsCardDisplay.html");
            showMentionList.add(depositCardObject);
            JSONObject criditCardObject = new JSONObject();
            criditCardObject.put("name","信用卡");
            criditCardObject.put("percentage",creditCardPercentage);
            criditCardObject.put("entranceLink","");
            criditCardObject.put("authorizeList",ifmBaseDict.getItemValue()+"/credit.html");
            criditCardObject.put("authorizeShowList",ifmBaseDict.getItemValue()+"/creditShow.html");
            showMentionList.add(criditCardObject);
            JSONObject housingFundObject = new JSONObject();
            housingFundObject.put("name","公积金");
            housingFundObject.put("percentage",housingFundPercentage);
            housingFundObject.put("entranceLink","");
            housingFundObject.put("authorizeList",ifmBaseDict.getItemValue()+"/accumulationFund.html");
            housingFundObject.put("authorizeShowList",ifmBaseDict.getItemValue()+"/accumulationDisplay.html");
            showMentionList.add(housingFundObject);
            JSONObject socialSecurityObject = new JSONObject();
            socialSecurityObject.put("name","社保");
            socialSecurityObject.put("percentage",socialSecurityPercentage);
            socialSecurityObject.put("entranceLink","");
            socialSecurityObject.put("authorizeList",ifmBaseDict.getItemValue()+"/socialSecurity.html");
            socialSecurityObject.put("authorizeShowList",ifmBaseDict.getItemValue()+"/showSocialSecurity.html");
            showMentionList.add(socialSecurityObject);
            JSONObject qqObject = new JSONObject();
            qqObject.put("name","QQ");
            qqObject.put("percentage",qqPercentage);
            qqObject.put("entranceLink","");
            qqObject.put("authorizeList","");
            qqObject.put("authorizeShowList","");
            showMentionList.add(qqObject);
            JSONObject weChatObject = new JSONObject();
            weChatObject.put("name","微信");
            weChatObject.put("percentage",weChatPercentage);
            weChatObject.put("entranceLink","");
            weChatObject.put("authorizeList","");
            weChatObject.put("authorizeShowList","");
            showMentionList.add(weChatObject);
            detail.put("showList",showMentionList);
        }else{
            detail.put(Consts.RESULT, ErrorCode.FAILED);
            detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
        }
        return detail;
    }
}
