package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.User;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SaveUser implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {

	}

	@Override
	public void doPost(IHTTPSession session, Response response) {
		if (!webServer.getUser().isAdmin()) {
			response.setStatus(Response.Status.FORBIDDEN);
			return;
		}

		response.setMimeType("application/json");
		ReturnObject returnObject;
		User user = null;
		boolean isNew = Boolean.valueOf(session.getParms().get("new"));

		if (isNew) {
			user = new User();
		} else {
			user = PersistenceManager.getDao(User.class).findOne(Long.valueOf(session.getParms().get("id")));
		}

		if (!isNew && user == null) {
			returnObject = new ReturnObject(false, "User not found to update", new IllegalArgumentException("Invalid User"));
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
			return;
		}

		user.setUsername(session.getParms().get("username"));
		if (StringUtils.isNotBlank(session.getParms().get("password"))) {
			user.setPassword(session.getParms().get("password"));
		}
		user.setAdmin(Boolean.valueOf(session.getParms().get("admin")));
		PersistenceManager.getDao(User.class).save(user);

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		if (!webServer.getUser().isAdmin()) {
			response.setStatus(Response.Status.FORBIDDEN);
			return;
		}

		User user = PersistenceManager.getDao(User.class).findOne(Long.valueOf(session.getParms().get("id")));
		PersistenceManager.getDao(User.class).remove(user);

		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

}
