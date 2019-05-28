package com.frame;

public class Consts {

	// 返回报文标签
	public final static String RESULT = "result";

	public final static String RESULT_NOTE = "resultNote";

	public final static String DETAIL = "detail";

	public final static String PAGE_AMT = "pages";

	public final static String RECORD_AMT = "totalRecordNum";

	/**
	 * 请求报文命令标签
	 */
	public final static String CMD_LABEL = "cmd";

	/**
	 * token标签
	 */
	public final static String TOKEN = "token";

	/**
	 * token标签
	 */
	public final static String USERID = "userId";

	/**
	 * 请求报文参数标签
	 */
	public final static String PARAMS_LABEL = "params";

	public final static String PAGE_NO = "pageNo";

	public final static String ONEPAGENUM = "onePageNum";

	public final static String VERSION = "version";

	/**
	 * 一个分页的数据项个数
	 */
	public final static int ONE_PAGE_NUM = 10;
	public final static int STATUS_ENABLE = 1;
	public final static int STATUS_DISABLE = 0;
	
	/**
	 *  不校验单点登录的接口
	 */
	public final static String notTokenValidation = "login|fastLogin|register|getValiCodeFromPhone|queryServiceUrl|queryAnnounce|homePage|forgetPassword|queryOneMessageByMesId|saveYouDunOCRInfo|saveYouDunIDCheckInfo|sumbitUserInfoToDecisionEngine|queryUserRewardAmt|queryServiceProrocol|prizeLotteryTable|queryPersonInfoConfiguration|loverActivity|getVoiceMsgFromPhone|getVoiceMsgClues|appRegister";
	
}
