package com.dao;

import java.util.List;

import com.model.AppPrizeFakeList;

public interface AppPrizeFakeListMapper {

	List<AppPrizeFakeList> selectListsRandly(int num);
	
}
