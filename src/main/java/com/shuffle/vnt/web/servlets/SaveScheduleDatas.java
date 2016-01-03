package com.shuffle.vnt.web.servlets;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.TorrentFilter;
import com.shuffle.vnt.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.services.schedule.ScheduleManager;
import com.shuffle.vnt.services.schedule.SchedulerData;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.ReturnObject;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SaveScheduleDatas implements HttpServlet {

    private static final Log log = LogFactory.getLog(SaveScheduleDatas.class);

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

	Map<String, List<String>> parameters = webServer.decodeParameters(session.getQueryParameterString());
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	response.setMimeType("application/json");
	ReturnObject returnObject;
	SchedulerData scheduleData = null;
	boolean isNew = Boolean.valueOf(session.getParms().get("new"));

	if (isNew) {
	    scheduleData = new SchedulerData();
	    PreferenceManager.getInstance().getPreferences().getSchedulerDatas().add(scheduleData);
	} else {
	    scheduleData = PreferenceManager.getInstance().getScheduleData(session.getParms().get("pkname"));
	}

	if (!isNew && scheduleData == null) {
	    returnObject = new ReturnObject(false, "Schedule not found to update", new IllegalArgumentException("Invalid Schedule data"));
	    response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	    return;
	}

	scheduleData.setName(session.getParms().get("name"));
	scheduleData.setTrackerUser(PreferenceManager.getInstance().getTrackerUser(session.getParms().get("tracker"), session.getParms().get("trackerUser")));
	scheduleData.setEmail(session.getParms().get("email"));
	QueryParameters queryParameters = new QueryParameters();
	queryParameters.setSearch(session.getParms().get("search"));
	if (parameters.get("category") != null) {
	    for (String category : parameters.get("category")) {
		TrackerCategory trackerCategory = VntUtil.getTrackerCategory(session.getParms().get("tracker"), category);
		if (trackerCategory != null) {
		    queryParameters.getTrackerCategories().add(trackerCategory);
		}
	    }
	}

	if (parameters.get("torrentfiltername") != null) {
	    int torrentfilternameiterator = 0;
	    for (String torrentfiltername : parameters.get("torrentfiltername")) {
		queryParameters.getTorrentFilters()
			.add(new TorrentFilter(torrentfiltername, FilterOperation.valueOf(parameters.get("torrentfilteroperation").get(torrentfilternameiterator)), parameters.get("torrentfiltervalue").get(torrentfilternameiterator)));
		torrentfilternameiterator++;
	    }
	}
	scheduleData.setQueryParameters(queryParameters);
	Date nextRun = null;
	try {
	    nextRun = dateFormat.parse(session.getParms().get("startDate"));
	} catch (ParseException e1) {
	    e1.printStackTrace();
	}
	scheduleData.setStartDate(nextRun);
	scheduleData.setNextRun(nextRun);
	scheduleData.setInterval(Long.valueOf(session.getParms().get("interval")));
	try {
	    scheduleData.setServiceParser(session.getParms().get("serviceParser") != null ? Class.forName(session.getParms().get("serviceParser")).asSubclass(ServiceParser.class) : null);
	} catch (ClassNotFoundException e) {
	    log.error("ServiceParser not found", e);
	}
	PreferenceManager.getInstance().savePreferences();

	ScheduleManager.getInstance().clearSchedules();
	ScheduleManager.getInstance().updateSchedules();

	returnObject = new ReturnObject(true, null);
	response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
    }

    @Override
    public void doPut(IHTTPSession session, Response response) {

    }

    @Override
    public void doDelete(IHTTPSession session, Response response) {
	SchedulerData schedulerData = PreferenceManager.getInstance().getScheduleData(session.getParms().get("pkname"));
	PreferenceManager.getInstance().getPreferences().getSchedulerDatas().remove(schedulerData);
	PreferenceManager.getInstance().savePreferences();

	response.setMimeType("application/json");
	ReturnObject returnObject = new ReturnObject(true, null);
	response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
    }

}
