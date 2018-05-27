package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Logout implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {
		
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		session.getCookies().delete("token");
		response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(true, null))));
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
