package com.shuffle.vnt.web.servlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.service.WebClient;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.model.User;

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
			seedbox = PersistenceManager.findOne(Seedbox.class, Long.valueOf(session.getParms().get("id")));
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
		PersistenceManager.save(seedbox);
		User user = PersistenceManager.findOne(User.class, Restrictions.idEq(webServer.getUser().getId()), "seedboxes");
		if (!user.getSeedboxes().contains(seedbox)) {
			user.getSeedboxes().add(seedbox);
			PersistenceManager.save(user);
		}

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		Seedbox seedbox = PersistenceManager.findOne(Seedbox.class, Long.valueOf(session.getParms().get("id")));
		User user = PersistenceManager.findOne(User.class, Restrictions.idEq(webServer.getUser().getId()), "seedboxes");
		user.getSeedboxes().remove(seedbox);
		PersistenceManager.save(user);

		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
	}

}
