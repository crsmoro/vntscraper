package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.model.User;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadUsers implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		if (!webServer.getUser().isAdmin()) {
			response.setStatus(Response.Status.FORBIDDEN);
			return;
		}

		response.setMimeType("application/json");

		String pkname = session.getParms().get("id");
		if (StringUtils.isNotBlank(pkname)) {
			//FIXME
			//Restrictions.idEq(Long.valueOf(pkname))
			User user = PersistenceManager.getDao(User.class).findOne(Long.valueOf(pkname));
			if (user != null) {
				response.setData(VntUtil.getInputStream(VntUtil.toJson(user)));
			}
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(User.class).findAll())));
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
