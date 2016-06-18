package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTorrentDetail implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		ReturnObject returnObject = null;
		Torrent torrent = VntUtil.fromJson(session.getParms().get("torrent"), Torrent.class);
		Torrent ret = null;
		if (torrent != null) {
			if (!torrent.isDetailed()) {
				TrackerManager trackerManager = TrackerManagerFactory.getInstance(torrent.getTrackerUser().getTracker());
				trackerManager.setTrackerUser(torrent.getTrackerUser());
				ret = trackerManager.getDetails(torrent);
			}

			if (ret == null) {
				returnObject = new ReturnObject(false, "Error when trying to get details", null);
			} else {
				returnObject = new ReturnObject(true, torrent);
			}

			response.setMimeType("application/json");
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		}
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
