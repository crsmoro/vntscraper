package com.shuffle.vnt.web.servlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.service.WebClient;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.UserSeedbox;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SaveSeedboxes implements HttpServlet {

	private static final Log log = LogFactory.getLog(SaveSeedboxes.class);

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

		response.setMimeType("application/json");
		ReturnObject returnObject;
		Seedbox seedbox = null;
		boolean isNew = Boolean.valueOf(session.getParms().get("new"));

		if (isNew) {
			seedbox = new Seedbox();
		} else {
			seedbox = PersistenceManager.getDao(Seedbox.class).findOne(Long.valueOf(session.getParms().get("id")));
		}

		if (!isNew && seedbox == null) {
			returnObject = new ReturnObject(false, "Seedbox not found to update", new IllegalArgumentException("Invalid Seedbox"));
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
			return;
		}

		seedbox.setName(session.getParms().get("name"));
		seedbox.setUrl(session.getParms().get("url"));
		seedbox.setUsername(session.getParms().get("username"));
		if (session.getParms().get("password") != null && !"".equals(session.getParms().get("password"))) {
			seedbox.setPassword(session.getParms().get("password"));
		}
		seedbox.setLabel(session.getParms().get("label"));
		try {
			seedbox.setWebClient(Class.forName(session.getParms().get("webClient")).asSubclass(WebClient.class));
		} catch (ClassNotFoundException e) {
			log.error("Webclient not found", e);
		}
		PersistenceManager.getDao(Seedbox.class).save(seedbox);
		UserSeedbox userSeedbox = PersistenceManager.getDao(UserSeedbox.class).eq("user", webServer.getUser()).eq("seedbox", seedbox).and(2).findOne();
		if (userSeedbox == null) {
			userSeedbox = new UserSeedbox();
		}
		userSeedbox.setUser(webServer.getUser());
		userSeedbox.setSeedbox(seedbox);
		PersistenceManager.getDao(UserSeedbox.class).save(userSeedbox);

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		Seedbox seedbox = PersistenceManager.getDao(Seedbox.class).findOne(Long.valueOf(session.getParms().get("id")));
		UserSeedbox userSeedbox = PersistenceManager.getDao(UserSeedbox.class).eq("user", webServer.getUser()).eq("seedbox", seedbox).and(2).findOne();
		PersistenceManager.getDao(UserSeedbox.class).remove(userSeedbox);

		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

}
