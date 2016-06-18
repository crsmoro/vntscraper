package com.shuffle.vnt.web.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.TorrentFilter;
import com.shuffle.vnt.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.core.schedule.ScheduleManager;
import com.shuffle.vnt.core.schedule.model.Job;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

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
		Job job = null;
		boolean isNew = Boolean.valueOf(session.getParms().get("new"));

		if (isNew) {
			job = new Job();
		} else {
			job = PersistenceManager.findOne(Job.class, Long.valueOf(session.getParms().get("id")));
		}

		if (!isNew && job == null) {
			returnObject = new ReturnObject(false, "Schedule not found to update", new IllegalArgumentException("Invalid Schedule data"));
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
			return;
		}

		job.setName(session.getParms().get("name"));
		Tracker tracker = Tracker.getInstance(session.getParms().get("tracker"));
		TrackerUser trackerUser = PersistenceManager.findOne(TrackerUser.class, Long.valueOf(session.getParms().get("trackerUser")));
		job.setTrackerUser(trackerUser);
		job.setEmail(session.getParms().get("email"));
		QueryParameters queryParameters = new QueryParameters();
		queryParameters.setSearch(session.getParms().get("search"));
		if (parameters.get("category") != null) {
			for (String category : parameters.get("category")) {
				TrackerCategory trackerCategory = tracker.getCategory(category);
				if (trackerCategory != null) {
					queryParameters.getTrackerCategories().add(trackerCategory);
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
		job.setQueryParameters(queryParameters);
		Date nextRun = null;
		try {
			nextRun = dateFormat.parse(session.getParms().get("startDate"));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		job.setStartDate(nextRun);
		job.setNextRun(nextRun);
		job.setInterval(Long.valueOf(session.getParms().get("interval")));
		try {
			job.setServiceParser(session.getParms().get("serviceParser") != null ? Class.forName(session.getParms().get("serviceParser")).asSubclass(ServiceParser.class) : null);
		} catch (ClassNotFoundException | LinkageError e) {
			log.error("ServiceParser not found", e);
		}

		if (webServer.getFiles().get("template") != null && !"".equals(webServer.getFiles().get("template"))) {
			try {
				FileInputStream fileInputStream = new FileInputStream(webServer.getFiles().get("template"));
				job.setTemplate(IOUtils.toByteArray(fileInputStream));
				log.info(IOUtils.toString(fileInputStream));
			} catch (IOException e2) {
				log.error("Error saving schedule template", e2);
			}
		}
		if (parameters.get("seedboxes") != null) {
			for (String seedbox : parameters.get("seedboxes")) {
				job.getSeedboxes().add(PersistenceManager.findOne(Seedbox.class, Long.valueOf(seedbox)));
			}
		} else {
			job.getSeedboxes().addAll(webServer.getUser().getSeedboxes());
		}

		PersistenceManager.save(job);
		if (!webServer.getUser().getJobs().contains(job)) {
			webServer.getUser().getJobs().add(job);
			PersistenceManager.save(webServer.getUser());
		}

		ScheduleManager.getInstance().clearSchedules();
		ScheduleManager.getInstance().updateSchedules();

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		Job job = PersistenceManager.findOne(Job.class, Long.valueOf(session.getParms().get("id")));
		webServer.getUser().getJobs().remove(job);
		PersistenceManager.save(webServer.getUser());

		ScheduleManager.getInstance().clearSchedules();
		ScheduleManager.getInstance().updateSchedules();

		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

}
