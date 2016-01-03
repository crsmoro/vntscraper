package com.shuffle.vnt.services.torrentmanager;

public interface WebClient {

	String addUrl();

	String labelField();

	String torrentField();

	enum AuthenticationType {
		BASIC, DIGEST, FORM, HTTPS
	}

	public AuthenticationType getAuthenticationType();

}
