package com.shuffle.vnt.web.servlets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Trackers implements HttpServlet {

	private WebServer webServer;

	private final static transient Log log = LogFactory.getLog(Trackers.class);

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		List<Map<String, Object>> jsonArray = Tracker.loadedTrackers.stream().map(tracker -> {
			Map<String, Object> mapReturn = new HashMap<>();
			mapReturn.put("name", tracker.getName());
			mapReturn.put("hasCaptcha", tracker.hasCaptcha());
			boolean avaliable = false;
			try {
				TrackerUser trackerUser = PersistenceManager.getDao(TrackerUser.class).eq("tracker", tracker.getName()).eq("user", webServer.getUser()).and(2).findOne();
				avaliable = trackerUser != null && (!tracker.hasCaptcha()
						|| (tracker.hasCaptcha() && TrackerManager.getInstance(tracker.getName(), trackerUser.getUsername(), VntSecurity.decrypt(trackerUser.getPassword(), VntSecurity.getPasswordKey())).authenticate()));
			} catch (SieveException e) {
				log.warn("Error verifying login", e);
			}
			mapReturn.put("avaliable", avaliable);
			return mapReturn;
		}).collect(Collectors.toList());
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
