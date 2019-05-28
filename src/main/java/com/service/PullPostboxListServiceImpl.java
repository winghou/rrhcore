package com.service;

import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppPostboxMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtmCnt;
import com.model.AppPostbox;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.PullPostboxListService;
import com.util.APIHttpClient;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
@Service
@Transactional(rollbackFor=Exception.class)
public class PullPostboxListServiceImpl implements PullPostboxListService {
	@Autowired
	private AppPostboxMapper appPostboxMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
//	@Autowired
//	private AppLoanCreditCardBillMapper appLoanCreditCardBillMapper;
	/**
	 * 获取邮箱列表
	 */
	@Override
	public JSONObject pullPostboxList(JSONObject params) {
		JSONObject detail = new JSONObject();
		//查询当前邮箱列表
		JSONArray postboxList =new JSONArray();
		//创建JSONObject 用于存放后续的邮箱对象
		JSONObject postbox=null;
		//查询数据库，找到目前支持邮箱集合
		List<AppPostbox> appPostboxList=appPostboxMapper.selectPostboxList();
		//循环取出邮箱编入postbox
		for (AppPostbox appPostbox : appPostboxList) {
			postbox=new JSONObject();
			postbox.put("postboxulr", appPostbox.getPostboxurl());
//			postbox.put("postboxname", appPostbox.getPostboxname());
			postbox.put("postboxtype", appPostbox.getPostboxtype());
			postbox.put("postboxImg", null!=appPostbox.getPostboxImg()?appPostbox.getPostboxImg():"");
			postbox.put("loginSuccess", appPostbox.getLoginSuccess());
			postbox.put("postboxalias", appPostbox.getPostboxalias());//邮箱别名
			postboxList.add(postbox);
		}
		detail.put("postboxList", postboxList);
		return detail;
	}

	
	
	
	
	/**
	 * 获取邮箱信息交给爬虫校验(只有验证通过，前端才会调用这个接口)
	 */
	@Override
	public JSONObject postboxListValidate(JSONObject params) {
		JSONObject detail = new JSONObject();
		  String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);  //传入用户ID
		  String cookie = JsonUtil.getJStringAndCheck(params, "cookie", null, false, detail);  //传入cookie
		  String url    = JsonUtil.getJStringAndCheck(params, "url", null, false, detail);  //传入验证的URL
		  String postboxType=JsonUtil.getJStringAndCheck(params, "postboxType", null, false, detail);  //传入邮箱类型
		  String html=JsonUtil.getJStringAndCheck(params, "html", null, false, detail);
		  String agent =JsonUtil.getJStringAndCheck(params, "agent", null, false, detail);  
		  //解析cookie
		  JSONObject cookiec=null;
		  try {
			cookiec=JSONObject.parseObject(cookie);
		  } catch (Exception e) {
			String iii = "";
			String ii = "";
			String[] cookis = cookie.split(";");
			if(cookis!=null&&cookis.length>1){
				int i = 0;
				for (String string : cookis) {
					if("=".equals(string.substring(string.length()-1, string.length()))&&string.length()>20){
						string = string.substring(0, string.length()-2).replace("=", "\":\"")+"=";
					}else{
						string = string.replace("=", "\":\"");
					}
					if(i<cookis.length-1){
						iii += string+"\",\"";
					}else if(i==cookis.length-1){
						iii += string;
					}
					i++;
				}
			}
			ii = "{\""+iii.replace(" ", "")+"\"}";
			
			cookiec=JSONObject.parseObject(ii);
		}
		  String username=null;
		   //告诉前台传入参数有问题
		  if(null==userId||null==cookie||null==url||null==postboxType){
			  detail.put(Consts.RESULT, ErrorCode.FAILED);
	          detail.put(Consts.RESULT_NOTE, "授权失败，请重新授权");
	          return detail;

		  }
		  //通过邮箱类型获取username
		  switch(Integer.parseInt(postboxType)){
		  case 0:
			  //QQ邮箱username
			  username=JsonUtil.getJStringAndCheck(cookiec, "username", null, false, detail);
			  break;
		  case 1:
			  //新浪邮箱
			  username = StringUtil.mailRegular(html,"\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
			  if("".equals(username)){
			      String[] tracknicks = html.split("data-value=");
			      if(tracknicks!=null&&tracknicks.length>1){
				       String tract = tracknicks[1];
				       String[] tras = tract.split("><");
				       username = StringUtil.nvl(tras[0].replace("\"", ""));
			      }
		     }else{
		    	 username="";
		     }
			  break;
		  case 2:
		  case 3:
			  //网易邮箱
			  if(null!=JsonUtil.getJStringAndCheck(cookiec, "MAIL_MISC", null, false, detail)&&!"".equals(JsonUtil.getJStringAndCheck(cookiec, "MAIL_MISC", null, false, detail))){
				  username=JsonUtil.getJStringAndCheck(cookiec, "MAIL_MISC", null, false, detail);
			  }else if(null!=JsonUtil.getJStringAndCheck(cookiec, " MAIL_MISC", null, false, detail)&&!"".equals(JsonUtil.getJStringAndCheck(cookiec, " MAIL_MISC", null, false, detail))){
				  username=JsonUtil.getJStringAndCheck(cookiec, " MAIL_MISC", null, false, detail);
			  }
			  break;
		  default:
			  username="";
			break;
		  }
		  //对从字符串中取出的username做UTF8格式编码
		  try{
			  username = StringUtil.nvl(StringUtil.decodeUnicode(URLDecoder.decode(username,"UTF-8")));
		  }catch (Exception e) {
		  }
		  
		  //生成UUID，作为该用户本次请求的唯一标识
		  String uuid =UUID.randomUUID().toString();
		  //查询用户
		  AppUser appUser = appUserMapper.selectByMchVersion(userId);
		  if(null!=appUser){
			  //根据邮箱类型查询邮箱名称
			 AppPostbox appPostbox=appPostboxMapper.selectappPostboxType(postboxType);
			 //TODO 查询数据库中的URL(推送爬虫邮箱验证接口URL)
			 IfmBaseDict ifmBaseDict=ifmBaseDictMapper.selectbyPhython("MCH_TE_PC_URL");
			//TODO 查询数据库中的URL(推送爬虫邮箱验证回调接口URL)
			 IfmBaseDict phythonCallBack=ifmBaseDictMapper.selectbyPhython("MCH_POSTBOX_CALLBACK_PC");
			 JSONObject job=new JSONObject();
			 if(null!=username&&!"".equals(username)){
				 job.put("username", username);
			  }else{
				 job.put("username", "");
			  }
			 job.put("userId", userId);
			 job.put("cookies", cookiec);
			 job.put("url", url);
			 job.put("mail_type", appPostbox.getPostboxname());
			 job.put("uuid", uuid);
			 job.put("html", html);
			 job.put("agent", agent);
			 job.put("callbackurl", phythonCallBack.getItemValue());
			 //TODO 
//			 job.put("callbackurl", "http://192.168.20.186:8080/api/phythonValidateCallBack");
			 userId = appUser.getUserid() + "";
			  //查询用户基本信息表
	          AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			  //初始化数据库表
	          AppLoanCtmCnt loanCtmCntInsert = new AppLoanCtmCnt();
	          //邮箱名称
              loanCtmCntInsert.setCntCommt(username+appPostbox.getPostboxname());
              loanCtmCntInsert.setCntType("5");//邮箱格式的运营商验证
              loanCtmCntInsert.setCntDesc("1");//降序排序
              loanCtmCntInsert.setApprId(appLoanAppl.getId());//用户注册后用户资料，贷款申请表的主键id
              loanCtmCntInsert.setPostboxUUID(uuid);
              loanCtmCntInsert.setPostboxType(appPostbox.getPostboxtype());
              appLoanCtmCntMapper.insertSelective(loanCtmCntInsert);
              //TODO 需要爬虫给出接口名(上线后需要改变接口IP地址)
             /* Pullphy pullphy =new Pullphy(ifmBaseDict.getItemValue(), job);
              Thread thread =new Thread(pullphy);
              thread.start();*/
//			 String result=APIHttpClient.doPost(ifmBaseDict.getItemValue(), job,60000);
//           System.out.println(result);
			  detail.put(Consts.RESULT, ErrorCode.SUCCESS);
		  }else{
			  detail.put(Consts.RESULT, ErrorCode.FAILED);
	          detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		  }
		return detail;
	}

	
}


/**
 * 单独处理提交爬虫信息
 * 目的：防止请求信息回调超时，而导致的页面请求超时
 * @author songchenwei
 *
 */
class Pullphy implements Runnable{
	private String url;
	private JSONObject job;
	
	
	public Pullphy(String url, JSONObject job) {
		super();
		this.url = url;
		this.job = job;
	}


	public Pullphy() {
		super();
		// TODO Auto-generated constructor stub
	}

 
	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public JSONObject getJob() {
		return job;
	}


	public void setJob(JSONObject job) {
		this.job = job;
	}


	@Override
	public void run() {
		String result =APIHttpClient.doPost(url, job,60000);
		System.out.println(result);
	}
	
}