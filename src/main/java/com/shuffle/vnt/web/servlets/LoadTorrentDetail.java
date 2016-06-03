package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.core.parser.TrackerConfig;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTorrentDetail implements HttpServlet {

    @Override
    public void setWebServer(WebServer webServer) {

    }

    @Override
    public void doGet(IHTTPSession session, Response response) {
	JsonObject jo = new JsonObject();
	String username = StringUtils.isNotBlank(session.getParms().get("username")) ? session.getParms().get("username") : null;
	Torrent torrent = VntUtil.getGson().fromJson(session.getParms().get("torrent"), Torrent.class);
	Torrent ret = null;
	if (torrent != null) {
	    if (!torrent.isDetailed()) {
		TrackerConfig trackerConfig = VntUtil.getTrackerConfig(torrent.getTracker());
		TrackerManager trackerManager = TrackerManagerFactory.getInstance(trackerConfig.getClass());
		trackerManager.setTrackerUser(PreferenceManager.getInstance().getTrackerUser(torrent.getTracker(), username));
		ret = trackerManager.getDetails(torrent);
	    }

	    if (ret == null) {
		jo.addProperty("success", false);
		jo.addProperty("error", "Error when trying to get details");
	    } else {
		JsonObject jsonObjectData = new JsonObject();
		jo.addProperty("success", true);
		jsonObjectData.add("torrent", VntUtil.getGson().toJsonTree(torrent, Torrent.class));
		jo.add("data", jsonObjectData);
	    }

	    response.setMimeType("application/json");
	    response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(jo)));
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
