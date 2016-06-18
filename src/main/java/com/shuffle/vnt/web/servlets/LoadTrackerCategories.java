package com.shuffle.vnt.web.servlets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTrackerCategories implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		List<TrackerCategoryTracker> categories = new ArrayList<>();
		List<Tracker> trackerConfigs = new ArrayList<>();
		String tracker = session.getParms().get("tracker");
		for (Class<? extends Tracker> trackerClass : VntUtil.fetchClasses().getSubTypesOf(Tracker.class)) {
			Tracker trackerInstance = Tracker.getInstance(trackerClass);
			if (webServer.getUser().getTrackerUser(trackerInstance) != null) {
				if (StringUtils.isBlank(tracker) || trackerClass.getName().equals(tracker)) {
					trackerConfigs.add(trackerInstance);
				}
			}
		}
		for (Tracker trackerConfig : trackerConfigs) {
			for (TrackerCategory trackerCategory : trackerConfig.getCategories()) {
				TrackerCategoryTracker categoryTracker = new TrackerCategoryTracker();
				categoryTracker.setTracker(trackerConfig.getName());
				categoryTracker.setTrackerClass(trackerConfig.getClass().getName());
				categoryTracker.setCode(trackerCategory.getCode());
				categoryTracker.setName(trackerCategory.getName());
				categoryTracker.setProperty(trackerCategory.getProperty());
				categories.add(categoryTracker);
			}
		}
		response.setMimeType("application/json");
		response.setData(VntUtil.getInputStream(VntUtil.toJson(categories)));
	}

	public class TrackerCategoryTracker extends TrackerCategory implements Serializable {

		private static final long serialVersionUID = -6971247970893048809L;

		private String tracker;

		private String trackerClass;

		public String getTracker() {
			return tracker;
		}

		public void setTracker(String tracker) {
			this.tracker = tracker;
		}

		public String getTrackerClass() {
			return trackerClass;
		}

		public void setTrackerClass(String trackerClass) {
			this.trackerClass = trackerClass;
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
