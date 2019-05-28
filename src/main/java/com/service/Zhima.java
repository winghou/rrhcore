package com.service;

import java.net.URLDecoder;

import com.alibaba.fastjson.JSON;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaAuthInfoAuthorizeRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditIvsDetailGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditWatchlistiiGetRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditIvsDetailGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditWatchlistiiGetResponse;

public class Zhima {
	//芝麻开放平台地址
    private String gatewayUrl     = "https://zmopenapi.zmxy.com.cn/openapi.do";
    //商户应用 Id
    private String appId          = "1000967";
    //商户 RSA 私钥
    private String privateKey     = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAN5ISdmwgVpF8wwAHPGhh5laGG08t3BaR6Bdilvocpf3UVIRSnbs+vCfz/Cyfj8DHZSMMb6gqhwkDZLBwSoZiOLlvyEQloSo8cLG22LVZE3Dy11wH8v+UzRd4IDpU9Ic42Sw1dJ5oi0gL3GfTCBgEsTaF+ufUnGWXl7Ejhet1HMXAgMBAAECgYA3Nq+Zcj1/RfbMWU7XOm9rSTTeFYHfV52JGRMgJ0ons1gw0Xi0t8LReHD3ce7oCgcT+gr1NgVrPg1dEeHAal1UwFg2BPTEFIaVb3UAkHsHd3ad4aFCf63fiXHgcRCKLLDCPUmRw3Gx0iH5ubgGxS+O5LJ/buQoeaCK8ay08sVW6QJBAPxmf3hYSGkqbAaq20ONeClN6sSakZhCwiYWWwKswCKhb5dnqhUl4FmIlJ6KdCE+lbd3CRweKUIVNW93xenMAY0CQQDhc9TDhhoCcAuw4aoYjXl1WCwi0GEt7t5DyCsbbbryjZV0VA1cFK22YPrrDxFDtuLASftqgWVgzaBfhNSUvbQzAkEA8O9m/xpHBRzZwgOXCwUgj00xrjtegFo5uuHcPtFpF6XDQGRUl5twbgdS14STFqkHgfq/V3fzKoHUYHOVGXkEgQJAYS6m9ilmKMrXn0WHJtJFGtoJHHCqkzw2Pw2X11jzjv6M//oDUD/xqX4gaxaxoHjDQZQidgSVCKdajBIb+InVlQJBAOVU3SGyc/aZ3+1nM3fflOnm3kLmOp+zZgc2lBsdkYGEomjBVYtm8+LPp+rcH18PJe5Qu5FeHzOR5L2Wmb6VbkQ=";
    //芝麻 RSA 公钥
    private String zhimaPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD/aUx5GtO3719VE+NYEtL8Cd2zuerYvsxlEw57KneTZ41c5Lq/7ygeRmX7fRhL9OZRj7T1ezAW3OfH5I3vZ6ymawer3sVuBn96cTl987o80vRyIP6gThkt9UD4MIXmuWqJZ+U0bq33S3lkIy//yzkZwB2JRr6AXab32C7KeqPKCwIDAQAB";
 
    public String  testZhimaAuthInfoAuthorize() {
        ZhimaAuthInfoAuthorizeRequest req = new ZhimaAuthInfoAuthorizeRequest();
        
        req.setChannel("appsdk");
        req.setPlatform("zmop");
       req.setIdentityType("2");// 必要参数        
       req.setIdentityParam("{\"name\":\"陈传熙\",\"certType\":\"IDENTITY_CARD\",\"certNo\":\"342923199111251211\"}");// 必要参数        
       req.setBizParams("{\"auth_code\":\"M_APPSDK\",\"channelType\":\"apppc\",\"state\":\"商户自定义\"}");//        
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey,
            zhimaPublicKey);
        try {
        	String url = client.generatePageRedirectInvokeUrl(req);  
            System.out.println(url);
            return url;
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
        return "";
    }
 
    public static void main(String[] args) {
    	//Zhima result = new  Zhima();
        //result.testZhimaAuthInfoAuthorize();
    	//result.jm();
    	//result.testZhimaCreditScoreGet();
    	//result.testZhimaCreditIvsDetailGet();
    	//result.testCreditWatchlist();
    }
    
    // params=E%2Fvd51TA2%2BZOZ%2BDO%2F4giGo717o4PSMya0Dnmtd1daoDZrnA%2BMpLug204ZpRvXo8j02RNXokFtTOWYDMGDIYf6H6Q7oI8FAsKYBoYwt3QJgMhNbNLj0QiH56ToRqf%2BmF6m0LNHkOgjN7orl7b%2BjIqeqtwwW7BOsnLWB%2FNvF30yloyTR0tym9oJvfrzB2FcUgDXXgk%2FglSMwwIgfww024UcA%2Ft3kfEnBsqKxOK%2F5WfI%2B3FhUhRTJ8yvyoGWoQ8bzUhtgLgoTWjZ%2F6NqizVwNetQ%2FVjBKeH0e1IS%2B%2Ft6VI9XDqQVxUuD3lsMkf71v3noErjdzI8pjHGphsUy264BoGOQA%3D%3D&sign=eOvExTRlZ9532oHn1hsLM5H4aNC21oOzP3ihpBAqYFsOPwFnWBBD2DDcD9qlUBTO0CfQmf8%2B5%2F8TBH7NW8%2FkRhKeEbClF8v4rmbtQsGt64Mun%2FlhvRcFSMEwgoSO084%2BZ6VXJ1Bt7lKRAUPgr5MTbGGftbbXZFdNktHRbHA7VEo%3D
    String param = "E%2Fvd51TA2%2BZOZ%2BDO%2F4giGo717o4PSMya0Dnmtd1daoDZrnA%2BMpLug204ZpRvXo8j02RNXokFtTOWYDMGDIYf6H6Q7oI8FAsKYBoYwt3QJgMhNbNLj0QiH56ToRqf%2BmF6m0LNHkOgjN7orl7b%2BjIqeqtwwW7BOsnLWB%2FNvF30yloyTR0tym9oJvfrzB2FcUgDXXgk%2FglSMwwIgfww024UcA%2Ft3kfEnBsqKxOK%2F5WfI%2B3FhUhRTJ8yvyoGWoQ8bzUhtgLgoTWjZ%2F6NqizVwNetQ%2FVjBKeH0e1IS%2B%2Ft6VI9XDqQVxUuD3lsMkf71v3noErjdzI8pjHGphsUy264BoGOQA%3D%3D";
    String sign = "eOvExTRlZ9532oHn1hsLM5H4aNC21oOzP3ihpBAqYFsOPwFnWBBD2DDcD9qlUBTO0CfQmf8%2B5%2F8TBH7NW8%2FkRhKeEbClF8v4rmbtQsGt64Mun%2FlhvRcFSMEwgoSO084%2BZ6VXJ1Bt7lKRAUPgr5MTbGGftbbXZFdNktHRbHA7VEo%3D";
    public void jm(){
    	try {
    		DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey,zhimaPublicKey);
    		String jm = client.decryptAndVerifySign(URLDecoder.decode(param,"utf-8"), URLDecoder.decode(sign,"utf-8"));
			System.out.println(jm);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void  testZhimaCreditScoreGet() {
    	String gatewayUrl     = "https://zmopenapi.zmxy.com.cn/openapi.do";
        ZhimaCreditScoreGetRequest req = new ZhimaCreditScoreGetRequest();
        req.setChannel("apppc");
        req.setPlatform("zmop");
               req.setTransactionId("201610181111111111111111115");// 必要参数        
               req.setProductCode("w1010100100000000001");// 必要参数        
               req.setOpenId("268816488675809985825662544");// 必要参数        
                DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey,
            zhimaPublicKey);
        try {
            ZhimaCreditScoreGetResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
            System.out.println(response.getZmScore());
            System.out.println(response.getBizNo());
            System.out.println(response.getBody());
            System.out.println(response.getParams());
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }
    
    public void  testZhimaCreditIvsDetailGet() {
        ZhimaCreditIvsDetailGetRequest req = new ZhimaCreditIvsDetailGetRequest();
        req.setChannel("apppc");
        req.setPlatform("zmop");
               req.setProductCode("w1010100000000000103");// 必要参数        
               req.setTransactionId("201610181111111111111111116");// 必要参数        
               req.setCertNo("362502198604023618");//        
               req.setCertType("100");//        
               req.setName("邓越程");//        
               req.setMobile("15951892570");//        
               /*req.setEmail("jnlxhy@alitest.com");//        
               req.setBankCard("20110602436748024138");//        
               req.setAddress("杭州市西湖区天目山路266号");//        
               req.setIp("101.247.161.1");//        
               req.setMac("44-45-53-54-00-00");//        
               req.setWifimac("00-00-00-00-00-00-00-E0");//        
               req.setImei("868331011992179");//        
               req.setImsi("460030091733165");//        
*/                DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, privateKey,
            zhimaPublicKey);
        try {
            ZhimaCreditIvsDetailGetResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
            System.out.println(response.getIvsScore());
            System.out.println(response.getBizNo());
            System.out.println(response.getBody());
            System.out.println(response.getParams());
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }
 
    
    /**
     * 测试行业关注名单接口.
     * @throws ZhimaApiException
     */
    public  void testCreditWatchlist() {
        ZhimaCreditWatchlistiiGetRequest creditWatchlistGetRequest = new ZhimaCreditWatchlistiiGetRequest();
        creditWatchlistGetRequest.setPlatform("zmop");
        creditWatchlistGetRequest.setChannel("apppc");
        creditWatchlistGetRequest.setTransactionId("20161127233347555555551235");
        creditWatchlistGetRequest.setProductCode("w1010100100000000022");
        creditWatchlistGetRequest.setOpenId("268816488675809985825662544");
        DefaultZhimaClient client = new DefaultZhimaClient(gatewayUrl, appId, "utf-8", privateKey,zhimaPublicKey);
        ZhimaCreditWatchlistiiGetResponse response;
        try {
            response = client.execute(creditWatchlistGetRequest);
            System.out.println(JSON.toJSON(response));
        } catch (ZhimaApiException e) {
            e.printStackTrace();
        }
    }
 
	
}
