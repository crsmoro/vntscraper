package com.shuffle.vnt.web.servlets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Cookie;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.model.TrackerUserUser;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTrackerUsers implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		List<TrackerUserUser> trackerUserUsers = new ArrayList<>();
		Tracker tracker = null;
		if (StringUtils.isNotBlank(session.getParms().get("tracker"))) {
			tracker = Tracker.getInstance(session.getParms().get("tracker"));
		}
		PersistenceManager<TrackerUserUser> persistenceManager = PersistenceManager.getDao(TrackerUserUser.class).eq("user", webServer.getUser()).eq("shared", true).or(2);
		if (tracker != null) {
			trackerUserUsers.addAll(persistenceManager.eq("trackerUser", webServer.getUser().getTrackerUser(tracker)).and(2).findAll());
		} else {
			trackerUserUsers.addAll(persistenceManager.findAll());
		}

		response.setMimeType("application/json");
		List<Map<String, Object>> jsonArray = new ArrayList<>();
		for (TrackerUserUser trackerUserUser : trackerUserUsers) {
			Map<String, Object> jsonObject = new HashMap<>();
			jsonObject.put("id", trackerUserUser.getId());
			jsonObject.put("tracker", trackerUserUser.getTrackerUser().getTracker().getName());
			jsonObject.put("trackerClass", trackerUserUser.getTrackerUser().getTracker().getClass().getName());
			jsonObject.put("username", trackerUserUser.getTrackerUser().getUsername());
			Date au = null;
			for (Cookie cookie : trackerUserUser.getTrackerUser().getCookies()) {
				if (au == null || au.before(new Date(cookie.getExpiration()))) {
					au = new Date(cookie.getExpiration());
				}
			}
			jsonObject.put("authenticatedUntil", au != null ? dateFormat.format(au) : null);
			jsonObject.put("shared", trackerUserUser.isShared());
			jsonObject.put("owner", trackerUserUser.getUser().getUsername());
			jsonObject.put("owned", webServer.getUser().equals(trackerUserUser.getUser()));
			jsonArray.add(jsonObject);
		}
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
