package com.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.AppBestSignService;

/**  
*   
* 项目名称：上上签签约服务
* 创建人：zhaheng  
* 创建时间：2017年12月26日 下午4:55:00  
* @version       
*/ 
@Controller
public class AppBestSignController { 
	private static final Logger logger = Logger.getLogger(AppBestSignController.class);
	@Autowired
	private AppBestSignService appBestSignService;
	/***上传并创建合同***/
	@RequestMapping("/uploadAndCreateContract")
	public @ResponseBody JSONObject queryCoupon (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.uploadAndCreateContract(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**生成用户签名/印章图片**/
	@RequestMapping("/CreateSign")
	public @ResponseBody JSONObject CreateSign (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.CreateSign();
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	/**上传用户签名/印章图片 这个是盖章**/
	@RequestMapping("/uploadSign")
	public @ResponseBody JSONObject uploadSign (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.uploadSign();
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**下载用户签名/印章图片**/
	@RequestMapping("/downLoadSign")
	public @ResponseBody JSONObject downLoadSign (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String url = appBestSignService.downLoadSign();
			JSONObject detail = new JSONObject();
			detail.put("downloadurl", url);
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**上传合同文件**/
	@RequestMapping("/uploadContract")
	public @ResponseBody JSONObject uploadContract (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String fid = appBestSignService.uploadContract(rt.getParams());
			JSONObject detail = new JSONObject();
			detail.put("fid", fid);
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	/**为PDF文件添加元素**/
	@RequestMapping("/addDataToPdf")
	public @ResponseBody JSONObject addDataToPdf (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.addDataToPdf(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	/**创建合同**/
	@RequestMapping("/createContract")
	public @ResponseBody JSONObject createContract (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.createContract(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	/**签署并锁定合同**/
	@RequestMapping("/signAndLockContract")
	public @ResponseBody JSONObject signContract (@RequestBody JSONObject jo, HttpServletRequest request) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String projectUrl = request.getSession().getServletContext().getRealPath("/");
			JSONObject object = rt.getParams();
			object.put("projectUrl", projectUrl);
			JSONObject detail = appBestSignService.signAndLockContract(object);
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**获取预览页URL**/
	@RequestMapping("/getContractUrl")
	public @ResponseBody JSONObject getContractUrl (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.getContractUrl(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**上传合同模板接口——为PDF添加元素接口——创建合同接口——签署合接口——锁定并结束合同接口——预览合同接口
	 * 一次性完成的接口，方便调试
	 * **/
	@RequestMapping("/fenQiContract")
	public @ResponseBody JSONObject fenQiContract (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.fenQiContract(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**上传并创建合同接口——签署合接口——锁定并结束合同接口——预览合同接口
	 * 一次性完成的接口，方便调试
	 * **/
	@RequestMapping("/daiKouContract")
	public @ResponseBody JSONObject daiKouContract (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appBestSignService.daiKouContract(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 借款页面预览合同
	 * @param jo
	 * @return
	 */
	@RequestMapping("/viewBestSign")
	public @ResponseBody JSONObject viewBestSign (@RequestBody JSONObject jo, HttpServletRequest request) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String projectUrl = request.getSession().getServletContext().getRealPath("/");
			JSONObject object = rt.getParams();
			object.put("projectUrl", projectUrl);
			JSONObject detail = appBestSignService.viewBestSign(object);
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
}
