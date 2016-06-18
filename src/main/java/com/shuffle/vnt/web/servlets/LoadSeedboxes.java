package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadSeedboxes implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		response.setMimeType("application/json");

		String pkname = session.getParms().get("id");
		if (StringUtils.isNotBlank(pkname)) {
			Seedbox seedbox = PersistenceManager.findOne(Seedbox.class, Long.valueOf(pkname));
			if (seedbox != null) {
				response.setData(VntUtil.getInputStream(VntUtil.toJson(seedbox)));
			}
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(webServer.getUser().getSeedboxes())));
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
