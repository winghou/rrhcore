package com.dao;

import java.util.List;
import java.util.Map;

import com.model.IfmBaseDict;

public interface IfmBaseDictMapper {
	public IfmBaseDict selectByPrimaryKey(Integer id);
	public List<Map<String,Object>> selectBaseDict(String param);
	public List<Map<String,Object>> selectBankName(String param);
	public List<IfmBaseDict> selectSchool(IfmBaseDict ifmBaseDict );
	public List<IfmBaseDict> selectPlace(String parentId );
	public IfmBaseDict selectBykey(IfmBaseDict ifmBaseDict);
	public IfmBaseDict selectfWFAndRate(Map<String,Object> par);
	public Map<String, Object> selectZhiMaCfg(String dataType);
	public List<IfmBaseDict> selectBankCode(IfmBaseDict ifmBaseDict );
	public List<Map<String, Object>> selectNotwithDrawId();
	public List<IfmBaseDict> selectTypeBankCode(IfmBaseDict ifmBaseDict );
    public List<IfmBaseDict> fetchDictsByType(String type);
	public List<IfmBaseDict> selectByBankNo(String bankNo);
	public List<IfmBaseDict> qryIfmBaseDict(String type);
	/**
	 * 查询随心花资料配置项
	 * @param type
	 * @return
	 */
	public List<IfmBaseDict> qryMchPersonInfoConfiguration(String param);
	public IfmBaseDict selectbyPhython(String dataType);
	//随心花提额页面URL路径
	public IfmBaseDict selectbyMention(String dataType);
	
	public List<IfmBaseDict> selectByItemKeyAndDataTypeList(Map<String,Object> map);
	
	void updateByDataType(IfmBaseDict ifmBaseDict);
}
