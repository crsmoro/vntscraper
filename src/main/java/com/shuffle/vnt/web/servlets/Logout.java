package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Logout implements HttpServlet {
	
	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		webServer.getUser().setSession(null);
		PersistenceManager.save(webServer.getUser());
		session.getCookies().delete("session");
		response.setStatus(Status.REDIRECT);
		response.addHeader("Location", "/login.html");
	}

	@Override
	public void doPost(IHTTPSession session, Response response) {

	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
