package com.shuffle.vnt.web.servlets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import com.shuffle.vnt.core.parser.TrackerConfig;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadTrackerCategories implements HttpServlet {

    @Override
    public void setWebServer(WebServer webServer) {
	
    }

    @Override
    public void doGet(IHTTPSession session, Response response) {
	List<TrackerCategoryTracker> categories = new ArrayList<>();
	List<TrackerConfig> trackerConfigs = new ArrayList<>();
	String tracker = session.getParms().get("tracker");
	Reflections reflections = new Reflections("com.shuffle.vnt.tracker");
	for (Class<? extends TrackerConfig> trackerConfigClass : reflections.getSubTypesOf(TrackerConfig.class)) {
	    try {
		TrackerConfig trackerConfig = trackerConfigClass.newInstance();
		if (StringUtils.isBlank(tracker) || trackerConfig.getName().equals(tracker)) {
		    trackerConfigs.add(trackerConfig);
		}

	    } catch (InstantiationException | IllegalAccessException e) {
		e.printStackTrace();
	    }
	}
	for (TrackerConfig trackerConfig : trackerConfigs) {
	    for (TrackerCategory trackerCategory : trackerConfig.getCategories()) {
		TrackerCategoryTracker categoryTracker = new TrackerCategoryTracker();
		    categoryTracker.setTracker(trackerConfig.getName());
		    categoryTracker.setCode(trackerCategory.getCode());
		    categoryTracker.setName(trackerCategory.getName());
		    categoryTracker.setProperty(trackerCategory.getProperty());
		    categories.add(categoryTracker);
	    }
	}
	response.setMimeType("application/json");
	response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(categories)));
    }

    public class TrackerCategoryTracker extends TrackerCategory implements Serializable {

	private static final long serialVersionUID = -6971247970893048809L;

	private String tracker;

	public String getTracker() {
	    return tracker;
	}

	public void setTracker(String tracker) {
	    this.tracker = tracker;
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
