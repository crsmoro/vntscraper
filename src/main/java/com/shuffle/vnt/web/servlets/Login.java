package com.shuffle.vnt.web.servlets;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.criterion.Restrictions;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.model.User;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Login implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {

	}

	@Override
	public void doPost(IHTTPSession session, Response response) {
		String username = session.getParms().get("username");
		String password = session.getParms().get("password");
		User user = PersistenceManager.findOne(User.class, Restrictions.and(Restrictions.eq("username", username), Restrictions.eq("password", password)));
		if (user != null) {
			String sessionhash = "";
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				sessionhash = new BigInteger(1, messageDigest.digest((username + password + String.valueOf(System.currentTimeMillis())).getBytes())).toString(16);
				user.setSession(sessionhash);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			PersistenceManager.save(user);
			session.getCookies().set("session", sessionhash, 30);
			response.setStatus(Status.REDIRECT);
			response.addHeader("Location", "/");
		} else {
			response.setStatus(Status.REDIRECT);
			response.addHeader("Location", "login.html");
		}
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
