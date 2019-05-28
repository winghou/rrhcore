package com.dao;

import java.util.List;

import com.model.AppInsurance;

public interface AppInsuranceMapper {

	public List<AppInsurance> selectAppInsurance();
	
	public List<AppInsurance> selectAppInsuranceByType(String type);
}
