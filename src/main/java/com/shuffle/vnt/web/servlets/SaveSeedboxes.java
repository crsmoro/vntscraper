package com.shuffle.vnt.web.servlets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.Seedbox;
import com.shuffle.vnt.services.torrentmanager.WebClient;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.ReturnObject;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class SaveSeedboxes implements HttpServlet {
	
	private static final Log log = LogFactory.getLog(SaveSeedboxes.class);

	@Override
	public void setWebServer(WebServer webServer) {
	    
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
			PreferenceManager.getInstance().getPreferences().getSeedboxes().add(seedbox);
		}
		else {
			seedbox = PreferenceManager.getInstance().getSeedbox(session.getParms().get("pkname"));
		}
		
		if (!isNew && seedbox == null) {
			returnObject = new ReturnObject(false, "Seedbox not found to update", new IllegalArgumentException("Invalid Seedbox"));
			response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
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
		}
		catch (ClassNotFoundException e) {
			log.error("Webclient not found", e);
		}
		PreferenceManager.getInstance().savePreferences();

		returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {
		
	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		Seedbox seedbox = PreferenceManager.getInstance().getSeedbox(session.getParms().get("pkname"));
		PreferenceManager.getInstance().getPreferences().getSeedboxes().remove(seedbox);
		PreferenceManager.getInstance().savePreferences();
		
		response.setMimeType("application/json");
		ReturnObject returnObject = new ReturnObject(true, null);
		response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}

}
