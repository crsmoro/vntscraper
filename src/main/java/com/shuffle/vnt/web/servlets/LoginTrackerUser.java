package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.TrackerUserUser;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoginTrackerUser implements HttpServlet {
	
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
		TrackerUserUser trackerUserUser = null;
		boolean isNew = Boolean.valueOf(session.getParms().get("new"));
		
		Tracker tracker = Tracker.getInstance(session.getParms().get("tracker"));
		String username = session.getParms().get("username");
		String password = session.getParms().get("password");
		String captcha = session.getParms().get("captcha");

		if (!isNew) {
			trackerUserUser = PersistenceManager.getDao(TrackerUserUser.class).findOne(Long.valueOf(session.getParms().get("id")));
			trackerUser = trackerUserUser.getTrackerUser();
			if (StringUtils.isBlank(password))
			{
				password = trackerUser.getPassword();				
			}
		}

		if (!isNew && trackerUser == null) {
			returnObject = new ReturnObject(false, "Tracker user not found to update", new IllegalArgumentException("Invalid Tracker User"));
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
			return;
		}		
		
		TrackerManager trackerManager = TrackerManagerFactory.getInstance(tracker);
		trackerManager.setUser(username, password, captcha);
		
		returnObject = new ReturnObject(trackerManager.authenticate(), null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
