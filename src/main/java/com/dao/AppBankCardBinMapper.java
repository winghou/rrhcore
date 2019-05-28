package com.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.model.AppBankCardBin;

public interface AppBankCardBinMapper {

	List<AppBankCardBin> selectByBankCode(@Param("bankCode")String bankCode,@Param("bankCode2")String bankCode2);
}
