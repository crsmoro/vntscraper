package com.shuffle.vnt.web.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.shuffle.vnt.web.model.User;

public class Token implements Serializable {

	private static final long serialVersionUID = -566455238305976725L;

	private User user;

	private LocalDateTime expires;

	public Token() {
		
	}

	public Token(User user, LocalDateTime expires) {
		super();
		this.user = user;
		this.expires = expires;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpires() {
		return expires;
	}

	public void setExpires(LocalDateTime expires) {
		this.expires = expires;
	}

	@Override
	public String toString() {
		return "Token [user=" + user + ", expires=" + expires + "]";
	}
}
