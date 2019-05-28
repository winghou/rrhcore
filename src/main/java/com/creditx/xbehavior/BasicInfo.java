package com.creditx.xbehavior;

public class BasicInfo implements java.io.Serializable {

	private static final long serialVersionUID = -5541624731989462174L;
	
	private String user_name = null;
    private String identity_number = null;
    private String mobile_md5 = null;
    private String home_province = null;
    private String home_city = null;
    private String home_county = null;
    private String home_address = null;
    private String company_province = null;
    private String company_city = null;
    private String company_county = null;
    private String company_address = null;
    private String university_name = null;
    private String education_level = null;
    private String major = null;
    private String marriage_status = null;
    private Contact[] emergency_contacts = null;

    public BasicInfo() {}

    public String getName() {
        return user_name;
    }
    public void setName(final String value) {
        user_name = value;
    }

    public String getIdentity() {
        return identity_number;
    }
    public void setIdentity(final String value) {
        identity_number = value;
    }

    public String getMobileMd5() {
        return mobile_md5;
    }
    public void setMobileMd5(final String value) {
    	mobile_md5 = value;
    }

    public String getHomeProvince() {
        return home_province;
    }
    public void setHomeProvince(final String value) {
        home_province = value;
    }

    public String getHomeCity() {
        return home_city;
    }
    public void setHomeCity(final String value) {
        home_city = value;
    }

    public String getHomeCounty() {
        return home_county;
    }
    public void setHomeCounty(final String value) {
        home_county = value;
    }

    public String getHomeAddress() {
        return home_address;
    }
    public void setHomeAddress(final String value) {
        home_address = value;
    }

    public String getCompanyProvince() {
        return company_province;
    }
    public void setCompanyProvince(final String value) {
        company_province = value;
    }

    public String getCompanyCity() {
        return company_city;
    }
    public void setCompanyCity(final String value) {
        company_city = value;
    }

    public String getCompanyCounty() {
        return company_county;
    }
    public void setCompanyCounty(final String value) {
        company_county = value;
    }

    public String getCompanyAddress() {
        return company_address;
    }
    public void setCompanyAddress(final String value) {
        company_address = value;
    }

    public String getUniversity() {
        return university_name;
    }
    public void setUniversity(final String value) {
        university_name = value;
    }

    public String getEducationLevel() {
        return education_level;
    }
    public void setEducationLevel(final String value) {
        education_level = value;
    }

    public String getMajor() {
        return major;
    }
    public void setMajor(final String value) {
        major = value;
    }

    public String getMarriageStatus() {
        return marriage_status;
    }
    public void setMarriageStatus(final String value) {
        marriage_status = value;
    }

    public Contact[] getEmergencyContacts() {
        return emergency_contacts;
    }
    public void setEmergencyContacts(final Contact[] value) {
        emergency_contacts = value;
    }

}
