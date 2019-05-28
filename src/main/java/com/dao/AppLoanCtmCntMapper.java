package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppLoanCtmCnt;

public interface AppLoanCtmCntMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppLoanCtmCnt record);

    int insertSelective(AppLoanCtmCnt record);

    AppLoanCtmCnt selectByPrimaryKey(Integer id);
    
    AppLoanCtmCnt selectppwByapprId(Integer apprId);
    
    AppLoanCtmCnt selecttbAccountByapprId(Integer apprId);
    
    AppLoanCtmCnt selectjdAccountByapprId(Integer apprId);
    
    List<Map<String,Object>> selectCnPassByApprId(Integer apprId);
    
    List<Map<String,Object>> selectCntCommtByApprId(Integer apprId);
    
    List<AppLoanCtmCnt> queryAllByApprId(Integer apprId);

    AppLoanCtmCnt queryByType(Map<String,Object> params);

    int updateByPrimaryKeySelective(AppLoanCtmCnt record);

    int updateByPrimaryKey(AppLoanCtmCnt record);

    List<AppLoanCtmCnt> AppLoanEmailAll();

    List<AppLoanCtmCnt> ctmcntEmail(Integer apprId);

    List<AppLoanCtmCnt> chargeEmail(String cntCommt);

    AppLoanCtmCnt platformLogin(AppLoanCtmCnt record);

    List<AppLoanCtmCnt> userPlatforms(Integer apprId);

    List<AppLoanCtmCnt> creditCardList(Integer apprId);

    List<AppLoanCtmCnt> depositCardList(Integer apprId);

    List<AppLoanCtmCnt> socialSecurityList(Integer apprId);

    List<AppLoanCtmCnt> housingFundList(Integer apprId);

    List<AppLoanCtmCnt> creditCardInsertList(Integer apprId);

    List<AppLoanCtmCnt> depositCardInsertList(Integer apprId);

    List<AppLoanCtmCnt> socialSecurityInsertList(Integer apprId);

    List<AppLoanCtmCnt> housingFundInsertList(Integer apprId);

    List<AppLoanCtmCnt> creditCardAll();

    List<AppLoanCtmCnt> depositCardAll();

    List<AppLoanCtmCnt> socialSecurityAll();

    List<AppLoanCtmCnt> housingFundAll();

    List<AppLoanCtmCnt> creditCardGroup(Integer apprId);

    List<AppLoanCtmCnt> depositCardGroup(Integer apprId);

    List<AppLoanCtmCnt> socialSecurityGroup(Integer apprId);

    List<AppLoanCtmCnt> housingFundGroup();

    int countEmailByApprId(Integer apprId);

    int countPlatformsByApprId(Integer apprId);

    int countCreditCardByApprId(Integer apprId);

    int countDepositCardByApprId(Integer apprId);

    int countSocialSecurityByApprId(Integer apprId);

    int countHousingFundByApprId(Integer apprId);

	List<AppLoanCtmCnt> housingFundGroup(Integer apprId);
	//根据uuid查询用户（用户重复验证只查询当前UUID）
	AppLoanCtmCnt selectByUUID(String uuid);
}