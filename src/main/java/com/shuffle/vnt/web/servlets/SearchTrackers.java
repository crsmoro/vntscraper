package com.shuffle.vnt.web.servlets;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.sieve.core.parser.bean.TorrentFilter;
import com.shuffle.sieve.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.sieve.core.parser.bean.TrackerCategory;
import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

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

	}

	@Override
	public void doPost(IHTTPSession session, Response response) {

		List<Map<String, Object>> torrents = new ArrayList<>();
		String tracker = session.getParameters().getOrDefault("tracker", Collections.emptyList()).stream().findFirst().orElse(null);
		if (StringUtils.isNotBlank(tracker)) {
			TrackerUser trackerUser = PersistenceManager.getDao(TrackerUser.class).eq("user", webServer.getUser()).eq("shared", true).or(2).eq("tracker", tracker).and(2).findOne();

			if (trackerUser != null) {
				TrackerManager trackerManager = TrackerManager.getInstance(tracker, trackerUser.getUsername(), VntSecurity.decrypt(trackerUser.getPassword(), VntSecurity.getPasswordKey()));
				QueryParameters queryParameters = new QueryParameters();
				queryParameters.setSearch(session.getParameters().getOrDefault("search", Collections.emptyList()).stream().findFirst().orElse(null));
				if (session.getParameters().get("category") != null) {
					for (String category : session.getParameters().get("category")) {
						String[] catSpl = category.split("\\|");
						String trac = catSpl[0];
						String cat = catSpl[1];
						if (tracker.equals(trac)) {
							TrackerCategory trackerCategory = Tracker.getInstance(tracker).getCategory(cat);
							if (trackerCategory != null) {
								queryParameters.getTrackerCategories().add(trackerCategory);
							}
						}

					}
				}

				if (session.getParameters().get("torrentfiltername") != null) {
					int torrentfilternameiterator = 0;
					for (String torrentfiltername : session.getParameters().get("torrentfiltername")) {
						queryParameters.getTorrentFilters().add(new TorrentFilter(torrentfiltername, FilterOperation.valueOf(session.getParameters().get("torrentfilteroperation").get(torrentfilternameiterator)),
								session.getParameters().get("torrentfiltervalue").get(torrentfilternameiterator)));
						torrentfilternameiterator++;
					}
				}

				trackerManager.setQueryParameters(queryParameters);

				trackerManager.setPage(0);
				if (StringUtils.isNotBlank(session.getParameters().getOrDefault("page", Collections.emptyList()).stream().findFirst().orElse(null))) {
					trackerManager.setPage(Long.valueOf(session.getParameters().getOrDefault("page", Collections.emptyList()).stream().findFirst().orElse(null)));
				}
				try {
					torrents.addAll(trackerManager.fetchTorrents().stream().map(VntUtil::clazzToObject).peek(m -> {
						try {
							m.put("chave", Hex.encodeHexString(VntSecurity.encrypt(URLEncoder.encode(VntUtil.toJson(m), "UTF-8"), VntSecurity.getTokenKey()).getBytes(StandardCharsets.UTF_8)));
							m.put("content", "");
							m.put("password", VntSecurity.encrypt(m.get("password").toString(), VntSecurity.getPasswordKey()));
						} catch (UnsupportedEncodingException dontcare) {
						}
					}).collect(Collectors.toList())); //peek(t -> t.setPassword(VntSecurity.encrypt(t.getPassword(), VntSecurity.getPasswordKey()))).collect(Collectors.toList()));
				} catch (SieveException e) {
					response.setMimeType("application/json; charset=UTF-8");
					response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(false, e.getMessage(), e))));
					return;
				}
			} else {
				response.setMimeType("application/json; charset=UTF-8");
				response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(false, "User doesnt have account to this tracker"))));
			}
		}

		response.setMimeType("application/json; charset=UTF-8");
		Map<String, Object> mapData = new HashMap<>();
		Map<String, String> jsonObjectTracker = new HashMap<>();
		jsonObjectTracker.put("name", tracker);
		mapData.put("tracker", jsonObjectTracker);
		mapData.put("torrents", torrents);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(true, mapData))));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
