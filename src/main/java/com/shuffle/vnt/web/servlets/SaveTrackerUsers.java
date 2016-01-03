package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.ReturnObject;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SaveTrackerUsers implements HttpServlet {

	@Override
    public void setWebServer(WebServer webServer) {
	
    }

	@Override
	public void doGet(IHTTPSession session, Response response) {

	}

	@Override
	public void doPost(IHTTPSession session, Response response) {

		response.setMimeType("application/json");
		ReturnObject returnObject;
		TrackerUser trackerUser = null;
		boolean isNew = Boolean.valueOf(session.getParms().get("new"));
		
		if (isNew) {
			trackerUser = new TrackerUser();
			PreferenceManager.getInstance().getPreferences().getTrackerUsers().add(trackerUser);
		}
		else {
			trackerUser = PreferenceManager.getInstance().getTrackerUser(session.getParms().get("pktracker"), session.getParms().get("pkusername"));
		}
		
		if (!isNew && trackerUser == null) {
			returnObject = new ReturnObject(false, "Tracker user not found to update", new IllegalArgumentException("Invalid Tracker User"));
			response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
			return;
		}
		
		trackerUser.setTracker(session.getParms().get("tracker"));
		trackerUser.setUsername(session.getParms().get("username"));
		if (session.getParms().get("password") != null && !"".equals(session.getParms().get("password"))) {
			trackerUser.setPassword(session.getParms().get("password"));
		}
		PreferenceManager.getInstance().savePreferences();

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {
		
	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		TrackerUser trackerUser = PreferenceManager.getInstance().getTrackerUser(session.getParms().get("pktracker"), session.getParms().get("pkusername"));
		PreferenceManager.getInstance().getPreferences().getTrackerUsers().remove(trackerUser);
		PreferenceManager.getInstance().savePreferences();
		
		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}

}
