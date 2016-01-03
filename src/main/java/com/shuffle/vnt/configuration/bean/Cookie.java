package com.shuffle.vnt.configuration.bean;

import java.io.Serializable;

public class Cookie implements Serializable {
	
	private static final long serialVersionUID = 3995909762051248616L;

	private String name;

	private String value;

	private long expiration;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	@Override
	public String toString() {
		return "Cookie [name=" + name + ", value=" + value + ", expiration=" + expiration + "]";
	}
}