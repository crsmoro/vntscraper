package com.shuffle.vnt.web.servlets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.Cookie;
import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTrackerUsers implements HttpServlet {

	@Override
    public void setWebServer(WebServer webServer) {
	
    }

	@Override
	public void doGet(IHTTPSession session, Response response) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		List<TrackerUser> trackerUsers = new ArrayList<>();
		String tracker = session.getParms().get("tracker");
		if (tracker != null && !"".equals(tracker)) {
			trackerUsers.addAll(PreferenceManager.getInstance().getTrackerUsers(tracker));
		} else {
			trackerUsers.addAll(PreferenceManager.getInstance().getPreferences().getTrackerUsers());
		}

		response.setMimeType("application/json");
		JsonArray jsonArray = new JsonArray();
		for (TrackerUser trackerUser : trackerUsers) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("tracker", trackerUser.getTracker());
			jsonObject.addProperty("username", trackerUser.getUsername());
			Date au = null;
			for (Cookie cookie : trackerUser.getCookies()) {
				if (au == null || au.before(new Date(cookie.getExpiration()))) {
					au = new Date(cookie.getExpiration());
				}
			}
			jsonObject.addProperty("authenticatedUntil", au != null ? dateFormat.format(au) : null);
			jsonArray.add(jsonObject);
		}
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
