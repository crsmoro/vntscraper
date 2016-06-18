package com.shuffle.vnt.web.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reflections.Reflections;

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
		List<Map<String, Object>> jsonArray = new ArrayList<>();
		Reflections reflections = new Reflections("com.shuffle.vnt.service.parser");
		for (Class<? extends ServiceParser> serviceParserClass : reflections.getSubTypesOf(ServiceParser.class)) {
			if (serviceParserClass.isInterface()) {
				Map<String, Object> jsonObject = new HashMap<>();
				jsonObject.put("name", serviceParserClass.getSimpleName());
				jsonObject.put("value", serviceParserClass.getName());
				jsonArray.add(jsonObject);
			}
		}
		response.setMimeType("application/json");
		response.setData(VntUtil.getInputStream(VntUtil.toJson(jsonArray)));
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
