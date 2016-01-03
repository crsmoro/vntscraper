package com.shuffle.vnt.web.servlets;

import org.reflections.Reflections;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadServiceParsers implements HttpServlet {

	@Override
    public void setWebServer(WebServer webServer) {
	
    }

	@Override
	public void doGet(IHTTPSession session, Response response) {
		JsonArray jsonArray = new JsonArray();
		Reflections reflections = new Reflections("com.shuffle.vnt.services.parser");
		for (Class<? extends ServiceParser> serviceParserClass : reflections.getSubTypesOf(ServiceParser.class)) {
			if (serviceParserClass.isInterface()) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name", serviceParserClass.getSimpleName());
				jsonObject.addProperty("value", serviceParserClass.getName());
				jsonArray.add(jsonObject);
			}
		}
		response.setMimeType("application/json");
		response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(jsonArray)));
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
