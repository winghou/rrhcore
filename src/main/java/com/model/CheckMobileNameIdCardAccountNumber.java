package com.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 银行卡验真接口demo
 * 
 * @author chenmeng
 *
 */
public class CheckMobileNameIdCardAccountNumber {
  @XmlRootElement
  public static class Req {
    private String name;
    private String idCard;
    private String mobile;
    private String accountNo;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getIdCard() {
      return idCard;
    }

    public void setIdCard(String idCard) {
      this.idCard = idCard;
    }

    public String getMobile() {
      return mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }

    public String getAccountNo() {
      return accountNo;
    }

    public void setAccountNo(String accountNo) {
      this.accountNo = accountNo;
    }

  }

  /*@Test
  public void test() throws IOException {

	    String url ="https://api.365dayservice.com:8044/credit-api/v1/accountInfoV2/checkMobileNameIdCardAccountNumberV2"; // 这里填写url            
	    String perPayKey = "wsg/9RiQe/Uv0pjsTYkmujvxQ4FWgIUeIlR72n3O5yY=";// 这里填写xa-key
	    RestTemplate restTemplate = new RestTemplate();
	    HttpHeaders header = new HttpHeaders();
	    header.set("xa-key", perPayKey);
	     String tex=FileHelper.readFile("E://errlog.json");
	   	 FileOutputStream fs = new FileOutputStream(new File("E:\\text.txt"));
		 PrintStream p = new PrintStream(fs);
		 JSONArray res =(JSONArray) JSON.parse(tex);
		 for(int i=0;i<res.size();i++){
			 JSONObject job = res.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
			 Req req = new Req();
			 req.setName(job.getString("name"));
		     req.setIdCard(job.getString("idCard"));
		     req.setMobile(job.getString("mobile"));
		     req.setAccountNo(job.getString("accountNo"));
		     HttpEntity<Req> h = new HttpEntity<>(req, header);
		     try {
		         ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, h, String.class);
		         System.out.println(resp.getBody());
		         p.println(resp.getBody());
		       } catch (HttpClientErrorException e) {
		         System.out.println(e.getResponseBodyAsString());
		         p.println(e.getResponseBodyAsString());
		         e.printStackTrace();
		       }
		 }
		 p.close();    
  }*/
}
