package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppChannelPageStatistic;
import com.model.AppChannelPageStatisticWithBLOBs;

public interface AppChannelPageStatisticMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppChannelPageStatisticWithBLOBs record);

    int insertSelective(AppChannelPageStatisticWithBLOBs record);

    AppChannelPageStatisticWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppChannelPageStatisticWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(AppChannelPageStatisticWithBLOBs record);

    int updateByPrimaryKey(AppChannelPageStatistic record);

    List<Map<String, Object>> selectByChannelLogin(String channel);

	List<AppChannelPageStatisticWithBLOBs> selectByChannelType(Map<String, Object> map1);
}