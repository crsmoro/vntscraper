package com.shuffle.vnt.web.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.parser.bean.TorrentFilter;
import com.shuffle.vnt.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.ReturnObject;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SearchTrackers implements HttpServlet {

    private WebServer webServer;

    @Override
    public void setWebServer(WebServer webServer) {
	this.webServer = webServer;
    }

    @Override
    public void doGet(IHTTPSession session, Response response) {

	Map<String, List<String>> parameters = webServer.decodeParameters(session.getQueryParameterString());
	List<Torrent> torrents = new ArrayList<>();
	String tracker = session.getParms().get("tracker");
	if (tracker != null && !"".equals(tracker)) {
	    TrackerManager trackerManager = TrackerManagerFactory.getInstance(VntUtil.getTrackerConfig(tracker).getClass());
	    QueryParameters queryParameters = new QueryParameters();
	    queryParameters.setSearch(session.getParms().get("search"));
	    if (parameters.get("category") != null) {
		for (String category : parameters.get("category")) {
			TrackerCategory trackerCategory = VntUtil.getTrackerCategory(tracker, category);
			if (trackerCategory != null) {
			    queryParameters.getTrackerCategories().add(trackerCategory);
			}
		    }
	    }
	    
	    if (parameters.get("torrentfiltername") != null) {
		int torrentfilternameiterator = 0;
		    for (String torrentfiltername : parameters.get("torrentfiltername")) {
			queryParameters.getTorrentFilters().add(new TorrentFilter(torrentfiltername, FilterOperation.valueOf(parameters.get("torrentfilteroperation").get(torrentfilternameiterator)), parameters.get("torrentfiltervalue").get(torrentfilternameiterator)));
			torrentfilternameiterator++;
		    }
	    }
	    
	    trackerManager.setQueryParameters(queryParameters);
	    trackerManager.setTrackerUser(PreferenceManager.getInstance().getTrackerUser(tracker));
	    if (StringUtils.isNotBlank(session.getParms().get("page"))) {
		trackerManager.setPage(Long.valueOf(session.getParms().get("page")));
	    }
	    torrents.addAll(trackerManager.fetchTorrents());
	}

	response.setMimeType("application/json; charset=UTF-8");
	Map<String, Object> mapData = new HashMap<>();
	mapData.put("tracker", tracker);
	mapData.put("torrents", torrents);
	response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(new ReturnObject(true, mapData))));
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
