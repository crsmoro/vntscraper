package com.shuffle.vnt.web.servlets;

import java.util.HashMap;
import java.util.Map;

import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class CurrentUser implements HttpServlet {
	
	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		Map<String, Object> mapdata = new HashMap<>();
		mapdata.put("user", webServer.getUser().getUsername());
		mapdata.put("admin", webServer.getUser().isAdmin());
		ReturnObject returnObject = new ReturnObject(true, mapdata);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
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
