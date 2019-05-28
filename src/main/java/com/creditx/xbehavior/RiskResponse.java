package com.creditx.xbehavior;

import java.util.Map;

public class RiskResponse {

    private String requestID = null;
    private int code = -1;
    private String errorMsg = null;
    private RiskRequest input = null;
    private Map<String, Object> output = null;

    public RiskResponse() {}

    public String getRequestId() {
        return requestID;
    }

    public String getErrorMessage() {
        return errorMsg;
    }

    public RiskRequest getRequest() {
        return input;
    }
    public void setUserId(final RiskRequest value) {
        input = value;
    }

    public int getStatusCode() {
        return code;
    }
    public void setStatusCode(final int value) {
        code = value;
    }

    public int getScore() {
        if (output != null) {
            Object scoreObj = output.get("riskScore");
            if (scoreObj instanceof Double)
                return ((Double)scoreObj).intValue();
        }
        return -1;
    }

	public Map<String, Object> getOutput() {
		return output;
	}

	public void setOutput(Map<String, Object> output) {
		this.output = output;
	}

}
