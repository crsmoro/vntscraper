package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.TrackerUserUser;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SaveTrackerUsers implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {

	}

	@Override
	public void doPost(IHTTPSession session, Response response) {

		response.setMimeType("application/json");
		ReturnObject returnObject;
		TrackerUser trackerUser = null;
		TrackerUserUser trackerUserUser = null;
		boolean isNew = Boolean.valueOf(session.getParms().get("new"));

		if (isNew) {
			trackerUser = new TrackerUser();
			trackerUserUser = new TrackerUserUser();
		} else {
			trackerUserUser = PersistenceManager.getDao(TrackerUserUser.class).findOne(Long.valueOf(session.getParms().get("id")));
			trackerUser = trackerUserUser.getTrackerUser();
		}

		if (!isNew && trackerUser == null) {
			returnObject = new ReturnObject(false, "Tracker user not found to update", new IllegalArgumentException("Invalid Tracker User"));
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
			return;
		}

		trackerUser.setTracker(Tracker.getInstance(session.getParms().get("tracker")));
		trackerUser.setUsername(session.getParms().get("username"));
		if (session.getParms().get("password") != null && !"".equals(session.getParms().get("password"))) {
			trackerUser.setPassword(session.getParms().get("password"));
		}
		PersistenceManager.getDao(TrackerUser.class).save(trackerUser);

		trackerUserUser.setTrackerUser(trackerUser);
		trackerUserUser.setUser(webServer.getUser());
		trackerUserUser.setShared(Boolean.valueOf(session.getParms().get("shared")));
		PersistenceManager.getDao(TrackerUserUser.class).save(trackerUserUser);

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		TrackerUserUser trackerUserUser = PersistenceManager.getDao(TrackerUserUser.class).findOne(Long.valueOf(session.getParms().get("id")));
		PersistenceManager.getDao(TrackerUserUser.class).remove(trackerUserUser);

		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

}
