package com.shuffle.vnt.web.servlets;

import org.reflections.Reflections;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuffle.vnt.core.parser.TrackerConfig;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTrackers implements HttpServlet {

	@Override
    public void setWebServer(WebServer webServer) {
	
    }

	@Override
	public void doGet(IHTTPSession session, Response response) {
		JsonArray jsonArray = new JsonArray();
		Reflections reflections = new Reflections("com.shuffle.vnt.tracker");
		for (Class<? extends TrackerConfig> trackerConfigClass : reflections.getSubTypesOf(TrackerConfig.class))
		{
			try {
				TrackerConfig trackerConfig = trackerConfigClass.newInstance();
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name", trackerConfig.getName());
				jsonArray.add(jsonObject);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
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
