package com.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.model.APPPayPlan;

public interface APPPayPlanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(APPPayPlan record);

    int insertSelective(APPPayPlan record);

    APPPayPlan selectByPrimaryKey(Integer id);
    
    List<APPPayPlan> selectBywithDrawId(APPPayPlan params);
    
    List<APPPayPlan> selectAllByWithDrawId(Integer withDrawId);
    
    List<APPPayPlan> selectByApprId(String apprId);
    
	List<APPPayPlan> selectByApprIdAndStatus(@Param("apprId") Integer apprId, @Param("status") String status);
    
    Double selectSumAmtByApprid(APPPayPlan params);
    
    int selectCurperiodsBywithDrawId(Integer withDrawId);
    
    List<Map<String,Object>> selectShouldPayThisMonth(Map<String,Object> pamams);
    
    List<Map<String,Object>> selectShouldPayNextMonth(Map<String,Object> pamams);
    
    Double selectAllOverdue(Integer apprId);
    
    int updateByPrimaryKeySelective(APPPayPlan record);

    int updateByPrimaryKey(APPPayPlan record);    
    
    
    /**
     * 查询用户是否还有逾期
     * @param apprId
     * @return
     */
    List<Map<String, Object>> selectOverduleByApprIdAndStatus(Integer apprId);
    
    /**
     * 查询当前订单是否全部结清
     * @param id
     * @return
     */
    List<Map<String, Object>> selectOtherPayplanByWithId(@Param("withId") Integer withId, @Param("repayId") Integer repayId);
}