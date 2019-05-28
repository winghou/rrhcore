package com.creditx.xbehavior;

public class RiskRequest {

    private String app_key = null;
    private String user_id = null;
    private String device_id = null;
    private BasicInfo basic = null;
    private Application application = null;

    public RiskRequest() {}

    public String getAppKey() {
        return app_key;
    }
    public void setAppKey(final String value) {
        app_key = value;
    }

    public String getUserId() {
        return user_id;
    }
    public void setUserId(final String value) {
        user_id = value;
    }

    public String getDeviceId() {
        return device_id;
    }
    public void setDeviceId(final String value) {
        device_id = value;
    }

    public BasicInfo getBasicInfo() {
        return basic;
    }
    public void setBasicInfo(final BasicInfo value) {
        basic = value;
    }

    public Application getApplication() {
        return application;
    }
    public void setApplication(final Application value) {
        application = value;
    }

}
