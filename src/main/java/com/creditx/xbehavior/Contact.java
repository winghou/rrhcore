package com.creditx.xbehavior;

public class Contact implements java.io.Serializable {

	private static final long serialVersionUID = 2164599645166474294L;
	
	private String contact_name = null;
    private String contact_mobile = null;

    public Contact(String name, String number) {
        contact_name = name;
        contact_mobile = number;
    }

    public String getName() {
        return contact_name;
    }
    public void setName(final String value) {
        contact_name = value;
    }

    public String getNumber() {
        return contact_mobile;
    }
    public void setNumber(final String value) {
        contact_mobile = value;
    }

}
