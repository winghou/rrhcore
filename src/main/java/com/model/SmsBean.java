package com.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmsBean {
	private String id;
	private String code;
	private String to;
	private List<SmsParams> multi = new ArrayList<SmsParams>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public List<SmsParams> getMulti() {
		return multi;
	}
	public void setMulti(List<SmsParams> multi) {
		this.multi = multi;
	}

	public static class SmsParams{
		private String to ; 
		private Map<String,String> vars = new HashMap<String,String>();
		public String getTo() {
			return to;
		}
		public void setTo(String to) {
			this.to = to;
		}
		public Map<String, String> getVars() {
			return vars;
		}
		public void setVars(Map<String, String> vars) {
			this.vars = vars;
		}
		
	}
	
}
