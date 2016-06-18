package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.schedule.model.Job;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadSchedulerDatas implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {

		response.setMimeType("application/json");

		String id = session.getParms().get("id");
		boolean count = Boolean.valueOf(session.getParms().get("count"));
		if (count) {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(true, webServer.getUser().getJobs().size()))));
		} else if (StringUtils.isNotBlank(id)) {
			Job job = PersistenceManager.findOne(Job.class, Long.valueOf(id));
			if (job != null) {
				response.setData(VntUtil.getInputStream(VntUtil.toJson(job)));
			}
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(webServer.getUser().getJobs())));
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
