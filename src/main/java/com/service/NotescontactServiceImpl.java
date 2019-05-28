package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.IfmBaseDictMapper;
import com.service.intf.NotescontactService;
import com.util.StringUtil;

@Service
public class NotescontactServiceImpl implements NotescontactService {
	
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	/* @author yang.wu
	 * 类名称： NotescontactServiceImpl
	 * 创建时间：2017年5月22日 下午2:27:24
	 * @see com.service.intf.NotescontactService#notescontact(com.alibaba.fastjson.JSONObject)
	 * 类描述：联系客服
	 */
	@Override
	public JSONObject notescontact(JSONObject params) {
		JSONObject detail = new JSONObject();
		List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("NOTESCONTACT");
		/*StringTokenizer st = new StringTokenizer(com.util.StringUtil.nvl(list.get(0).get("ITEM_VALUE")),",");
		JSONArray phoneAarry = new JSONArray();
		while(st.hasMoreTokens()){
			JSONObject phone = new JSONObject();
			phone.put("phone", st.nextToken());
			phoneAarry.add(phone);
        }*/		
		JSONArray arr = new JSONArray();
		if(!"".equals(StringUtil.nvl(list.get(0).get("ITEM_VALUE")))){
			String[] phones = StringUtil.nvl(list.get(0).get("ITEM_VALUE")).split(",");
			if(phones.length>0){
				for (String string : phones) {
					arr.add(string);
				}
			}
		}
		detail.put("customerServicePhone", arr); //工作电话
		detail.put("workTime", list.get(1).get("ITEM_VALUE")); //工作时间
		detail.put("pictureAddress", list.get(2).get("ITEM_VALUE"));  //公众号图片地址
		detail.put("weChatName", list.get(3).get("ITEM_VALUE")); //微信公众号名称
		return detail;
	}

}
