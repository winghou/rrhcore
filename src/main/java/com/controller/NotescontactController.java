package com.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.NotescontactService;

@Controller
public class NotescontactController {
	private static final Logger logger = Logger.getLogger(NotescontactController.class);
	
	@Autowired
	private NotescontactService notescontactService;
	
	/* @author yang.wu
	 * 类名称： notescontact
	 * 创建时间：2017年5月22日 下午2:27:41
	 * @param jo
	 * @return JSONObject
	 * 类描述：联系客服
	 */
	@RequestMapping("/notescontact")
	public @ResponseBody JSONObject notescontact (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = notescontactService.notescontact(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
