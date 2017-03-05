package com.shuffle.vnt.web.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.core.VntContext;
import com.shuffle.vnt.core.exception.VntException;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTrackers implements HttpServlet {

	private WebServer webServer;
	
	private final static transient Log log = LogFactory.getLog(LoadTrackers.class);

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		List<Map<String, Object>> jsonArray = new ArrayList<>();
		VntContext.getTrackers().forEach(tracker -> {
			Map<String, Object> jsonObject = new HashMap<>();
			jsonObject.put("name", tracker.getName());
			jsonObject.put("clazz", tracker.getClass().getName());
			jsonObject.put("hasCaptcha", tracker.hasCaptcha());
			boolean avaliable = false;
			for (TrackerUser trackerUser : webServer.getUser().getTrackerUsers()) {
				if (trackerUser.getTracker().getName().equals(tracker.getName())) {
					try {
						if (!trackerUser.getTracker().hasCaptcha() || (trackerUser.getTracker().hasCaptcha() && TrackerManagerFactory.getInstance(tracker).authenticate()))
						{
							avaliable = true;
							break;						
						}
					}
					catch (VntException e) {
						log.warn("Error verifying login", e);
					}
				}
			}
			jsonObject.put("avaliable", avaliable);
			jsonArray.add(jsonObject);
		});
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
