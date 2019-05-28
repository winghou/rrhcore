
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
import com.service.intf.DrainagePromotionService;
@Controller
public class DrainagePromotionController {
private static final Logger logger = Logger.getLogger(AdviceOrBackController.class);
	
	@Autowired
	DrainagePromotionService drainagePromotionService;
	
	/* @author yang.wu
	 * 类名称： getDrainagePromotionUrl
	 * 创建时间：2017年8月10日 上午11:57:47
	 * @param jo
	 * @return JSONObject
	 * 类描述：用户引流
	 */
	@RequestMapping("/ getDrainagePromotionUrl")
	public @ResponseBody JSONObject  getDrainagePromotionUrl(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = drainagePromotionService.getDrainagePromotionUrl(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}

