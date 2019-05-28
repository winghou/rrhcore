package com.service.intf;

import java.util.Map;

public interface CheckTokenService {

	public String checkToken(Map<String, Object> map);

	String checkwxToken(Map<String, Object> map);
}
