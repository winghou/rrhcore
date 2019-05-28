package com.service.intf;

import com.alibaba.fastjson.JSONObject;
import com.model.AppLoanAppl;
import com.model.AppWhiteKnightInfo;

public interface MorphoLoanInfoService {

	JSONObject loanInfo(String idfa,AppLoanAppl loanAppl,AppWhiteKnightInfo appWhiteKnightInfo);

}
