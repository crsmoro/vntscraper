package com.shuffle.vnt.core.security;

import com.shuffle.vnt.web.bean.Token;
import com.shuffle.vnt.web.model.User;

public abstract class SecurityContext {

	private static final ThreadLocal<Token> token = new ThreadLocal<>();

	private static final ThreadLocal<User> user = new ThreadLocal<>();

	public static void setUser(User user) {
		SecurityContext.user.set(user);
	}

	public static void setToken(Token token) {
		SecurityContext.token.set(token);
	}

	public static User getUser() {
		return SecurityContext.user.get();
	}

	public static Token getToken() {
		return SecurityContext.token.get();
	}
}
