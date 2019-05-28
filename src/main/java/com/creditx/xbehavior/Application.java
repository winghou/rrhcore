package com.creditx.xbehavior;

public class Application implements java.io.Serializable {

	private static final long serialVersionUID = -8371113468317266540L;
	
	private String is_new_applicant = null;
    private String loan_apply_date = null;

    public Application() {}

    public String getIsNewApplicant() {
        return is_new_applicant;
    }
    public void setIsNewApplicant(final String value) {
    	is_new_applicant = value;
    }

    public String getApplyTime() {
        return loan_apply_date;
    }
    public void setApplyTime(final String value) {
        loan_apply_date = value;
    }

}
