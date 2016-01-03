package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.services.schedule.SchedulerData;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.ReturnObject;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadSchedulerDatas implements HttpServlet {

	@Override
    public void setWebServer(WebServer webServer) {
	
    }

	@Override
	public void doGet(IHTTPSession session, Response response) {
		
		response.setMimeType("application/json");
		
		String pkname = session.getParms().get("pkname");
		boolean count = Boolean.valueOf(session.getParms().get("count"));
		if (count) {
			response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(new ReturnObject(true, PreferenceManager.getInstance().getPreferences().getSchedulerDatas().size()))));
		}
		else if (pkname != null && !"".equals(pkname)) {
			SchedulerData scheduleData = PreferenceManager.getInstance().getScheduleData(pkname);
			if (scheduleData != null) {
				response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(scheduleData)));
			}
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(PreferenceManager.getInstance().getPreferences().getSchedulerDatas())));
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
