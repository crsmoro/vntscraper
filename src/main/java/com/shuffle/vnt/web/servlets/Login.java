package com.shuffle.vnt.web.servlets;

import java.time.LocalDateTime;
import java.util.Collections;

import org.mindrot.jbcrypt.BCrypt;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.bean.Token;
import com.shuffle.vnt.web.model.User;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Login implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {

	}

	@Override
	public void doPost(IHTTPSession session, Response response) {
		String username = session.getParameters().getOrDefault("username", Collections.emptyList()).stream().findFirst().orElse(null);
		String password = session.getParameters().getOrDefault("password", Collections.emptyList()).stream().findFirst().orElse(null);
		User user = PersistenceManager.getDao(User.class).eq("username", username).findOne();
		if (user != null && BCrypt.checkpw(password, user.getPassword())) {
			Token token = new Token(user, LocalDateTime.now().plusDays(30));
			session.getCookies().set("token", VntSecurity.encrypt(VntUtil.toJson(token), VntSecurity.getTokenKey()), 30);
			ReturnObject returnObject = new ReturnObject(true, token);
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(false, null))));
		}
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
