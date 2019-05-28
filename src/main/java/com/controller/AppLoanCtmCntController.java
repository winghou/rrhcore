package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.AppLoanCtmCntService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by fangchen on 2017/11/10.
 */
@Controller
public class AppLoanCtmCntController {

    private static final Logger logger = Logger.getLogger(AppLoanCtmCntController.class);

    @Autowired
    private AppLoanCtmCntService  appLoanCtmCntService;

    //展示所有贷款平台信息
    @RequestMapping("/platformQry")
    public @ResponseBody
    JSONObject platformQry (@RequestBody JSONObject jo) {
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.appLoanCtmCnt(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户和贷款平台的主键编号查询平台登录时的用户名和密码，主页Logo，进行用户登录
    @RequestMapping("/platformLogin")
    public @ResponseBody
    JSONObject platformLogin (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.platformLogin(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //贷款平台的修改或者新增
    @RequestMapping("/platformLoanCtmCntSave")
    public @ResponseBody
    JSONObject platformLoanCtmCntSave (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.platformLoanCtmCntSave(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //查询指定用户申请的所有贷款平台信息
    @RequestMapping("/userPlatforms")
    public @ResponseBody
    JSONObject userPlatforms (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.userPlatforms(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户id和绑定的相应邮箱密码，与爬虫对接后判断邮箱是否真实存在，密码是否正确
    @RequestMapping("/emailLogin")
    public @ResponseBody
    JSONObject emailLogin (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.ctmcntLogin(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户id获取其绑定的所有邮箱
    @RequestMapping("/userEmails")
    public @ResponseBody
    JSONObject userEmails (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.userEmails(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //查询相应用户指定邮箱序列号的邮箱和密码
    @RequestMapping("/emailDetailByCode")
    public @ResponseBody
    JSONObject emailDetailByCode (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.emailDetailByCode(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //修改邮箱密码
    @RequestMapping("/emailPwdChange")
    public @ResponseBody
    JSONObject emailPwdChange (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.emailmPwdChange(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户绑定所有信用卡查询
    @RequestMapping("/creditCardList")
    public @ResponseBody
    JSONObject creditCardList (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.creditCardList(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户userId和信用卡编号creditCardId获取信用卡详情
    @RequestMapping("/creditCardDetail")
    public @ResponseBody
    JSONObject creditCardDetail (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.creditCardDetail(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户信用卡的添加和修改
    @RequestMapping("/creditCardSaveOrUpdate")
    public @ResponseBody
    JSONObject creditCardSaveOrUpdate (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.creditCardSaveOrUpdate(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }


    //用户绑定所有储蓄卡查询
    @RequestMapping("/depositCardList")
    public @ResponseBody
    JSONObject depositCardList(@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.depositCardList(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户userId和储蓄卡编号creditCardId获取储蓄卡详情
    @RequestMapping("/depositCardDetail")
    public @ResponseBody
    JSONObject depositCardDetail (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.depositCardDetail(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户储蓄卡的添加和修改
    @RequestMapping("/depositCardSaveOrUpdate")
    public @ResponseBody
    JSONObject depositCardSaveOrUpdate (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.depositCardSaveOrUpdate(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户绑定所有社保卡查询
    @RequestMapping("/socialSecurityList")
    public @ResponseBody
    JSONObject socialSecurityList (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.socialSecurityList(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户userId和社保卡号SecurityId获取绑定社保详情
    @RequestMapping("/socialSecurityDetail")
    public @ResponseBody
    JSONObject socialSecurityDetail (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.socialSecurityDetail(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户社保信息的添加和修改
    @RequestMapping("/socialSecuritySaveOrUpdate")
    public @ResponseBody
    JSONObject socialSecuritySaveOrUpdate (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.socialSecuritySaveOrUpdate(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户绑定所有公积金查询
    @RequestMapping("/housingFundList")
    public @ResponseBody
    JSONObject housingFundList (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.housingFundList(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //根据用户userId和社保卡号SecurityId获取绑定公积金详情
    @RequestMapping("/housingFundDetail")
    public @ResponseBody
    JSONObject housingFundDetail (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.housingFundDetail(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户公积金账号信息的添加和修改
    @RequestMapping("/housingFundSaveOrUpdate")
    public @ResponseBody
    JSONObject housingFundSaveOrUpdate (@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.housingFundSaveOrUpdate(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }

    //用户可提额的总金额和各种贷款平台渠道的绑定进度展示
    @RequestMapping("/userProgressRate")
    public @ResponseBody
    JSONObject userProgressRate(@RequestBody JSONObject jo){
        try {
            RequestTemplate rt = new RequestTemplate(jo);
            JSONObject detail = appLoanCtmCntService.userProgressRate(rt.getParams());
            return new ResponseTemplate(jo, detail).getReturn();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return new ResponseTemplate(jo).getReturn();
        }
    }
}
