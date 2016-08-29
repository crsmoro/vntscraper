package com.shuffle.vnt.web.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.parser.bean.TorrentFilter;
import com.shuffle.vnt.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.TrackerUserUser;

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
		Tracker trackerInstance = null;
		if (StringUtils.isNoneBlank(tracker)) {
			trackerInstance = Tracker.getInstance(tracker);
			TrackerManager trackerManager = TrackerManagerFactory.getInstance(trackerInstance);
			QueryParameters queryParameters = new QueryParameters();
			queryParameters.setSearch(session.getParms().get("search"));
			if (parameters.get("category") != null) {
				for (String category : parameters.get("category")) {
					String[] catSpl = category.split("\\|");
					String trac = catSpl[0];
					String cat = catSpl[1];
					if (tracker.equals(trac)) {
						TrackerCategory trackerCategory = trackerInstance.getCategory(cat);
						if (trackerCategory != null) {
							queryParameters.getTrackerCategories().add(trackerCategory);
						}
					}

				}
			}

			if (parameters.get("torrentfiltername") != null) {
				int torrentfilternameiterator = 0;
				for (String torrentfiltername : parameters.get("torrentfiltername")) {
					queryParameters.getTorrentFilters().add(new TorrentFilter(torrentfiltername, FilterOperation.valueOf(parameters.get("torrentfilteroperation").get(torrentfilternameiterator)),
							parameters.get("torrentfiltervalue").get(torrentfilternameiterator)));
					torrentfilternameiterator++;
				}
			}

			trackerManager.setQueryParameters(queryParameters);
			TrackerUserUser trackerUserUser = PersistenceManager.getDao(TrackerUserUser.class).eq("user", webServer.getUser()).eq("shared", true).or(2).eq("trackerUser", webServer.getUser().getTrackerUser(trackerInstance)).and(2).findOne();
			trackerManager.setUser(trackerUserUser.getTrackerUser().getUsername(), trackerUserUser.getTrackerUser().getPassword());
			trackerManager.setPage(0);
			if (StringUtils.isNotBlank(session.getParms().get("page"))) {
				trackerManager.setPage(Long.valueOf(session.getParms().get("page")));
			}
			torrents.addAll(trackerManager.fetchTorrents());
			torrents.forEach(torrent -> torrent.setContent(""));
		}

		response.setMimeType("application/json; charset=UTF-8");
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("tracker", trackerInstance.getName());
		mapData.put("trackerClass", trackerInstance.getClass());
		mapData.put("torrents", torrents);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(true, mapData))));
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
