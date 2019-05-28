package com.model;

public class NameAndPhone {

	private String name;
	private String phone;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof NameAndPhone) {
			NameAndPhone nameAndPhone = (NameAndPhone) obj;
			if (nameAndPhone.name.equals(this.name) && nameAndPhone.phone.equals(this.phone))
				return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ((name == null) ? 0 : name.hashCode()) * ((phone == null) ? 0 : phone.hashCode());
	}
}
