package com.shuffle.vnt.web;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public interface HttpServlet {
	
	String mimeTypeJson = "application/json";
	
    	void setWebServer(WebServer webServer);
    
	void doGet(IHTTPSession session, Response response);

	void doPost(IHTTPSession session, Response response);
	
	void doPut(IHTTPSession session, Response response);
	
	void doDelete(IHTTPSession session, Response response);
}
