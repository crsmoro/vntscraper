package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.schedule.model.Job;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.User;

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
			User user = PersistenceManager.findOne(User.class, Restrictions.idEq(webServer.getUser().getId()), "jobs");
			response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(true, user.getJobs().size()))));
		} else if (StringUtils.isNotBlank(id)) {
			Job job = PersistenceManager.findOne(Job.class, Long.valueOf(id));
			if (job != null) {
				response.setData(VntUtil.getInputStream(VntUtil.toJson(job)));
			}
		} else {
			User user = PersistenceManager.findOne(User.class, Restrictions.idEq(webServer.getUser().getId()), "jobs");
			response.setData(VntUtil.getInputStream(VntUtil.toJson(user.getJobs())));
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
