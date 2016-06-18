package com.shuffle.vnt.web.servlets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.configuration.PreferenceManager;
import com.shuffle.vnt.core.configuration.model.MailConfig;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Settings implements HttpServlet {

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
		
		String configuration = session.getParms().get("configuration");
		if (configuration.equals("mailConfig")) {
			MailConfig mailConfig = PreferenceManager.getPreferences().getMailConfig();
			response.setMimeType("application/json; charset=UTF-8");
			ReturnObject returnObject = new ReturnObject(true, mailConfig);
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		} else if (configuration.equals("generalConfig")) {
			response.setMimeType("application/json; charset=UTF-8");
			Map<String, Object> generalConfig = new HashMap<>();
			generalConfig.put("baseUrl", PreferenceManager.getPreferences().getBaseUrl());
			generalConfig.put("imdbActive", PreferenceManager.getPreferences().isImdbActive());
			generalConfig.put("tmdbActive", PreferenceManager.getPreferences().isTmdbActive());
			generalConfig.put("tmdbapikey", PreferenceManager.getPreferences().getTmdbApiKey());
			generalConfig.put("tmdbLanguage", Arrays.asList(PreferenceManager.getPreferences().getTmdbLanguage().split(",")));

			ReturnObject returnObject = new ReturnObject(true, generalConfig);
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		}
	}

	@Override
	public void doPost(IHTTPSession session, Response response) {
		if (!webServer.getUser().isAdmin()) {
			response.setStatus(Response.Status.FORBIDDEN);
			return;
		}
		
		String configuration = session.getParms().get("configuration");
		if (configuration.equals("mailConfig")) {
			MailConfig mailConfig = PreferenceManager.getPreferences().getMailConfig();
			if (mailConfig == null) {
				mailConfig = new MailConfig();
				PreferenceManager.getPreferences().setMailConfig(mailConfig);
			}
			mailConfig.setFrom(session.getParms().get("from"));
			mailConfig.setFromName(session.getParms().get("fromName"));
			mailConfig.setHostname(session.getParms().get("hostname"));
			mailConfig.setUsername((session.getParms().get("username")));
			if (StringUtils.isNoneBlank(session.getParms().get("password"))) {
				mailConfig.setPassword(session.getParms().get("password"));
			}
			mailConfig.setPort(Integer.valueOf(session.getParms().get("port")));
			mailConfig.setSsl(Boolean.valueOf(session.getParms().get("ssl")));
			mailConfig.setTls(Boolean.valueOf(session.getParms().get("tls")));
			PersistenceManager.save(mailConfig);
			PreferenceManager.savePreferences();

			response.setMimeType("application/json; charset=UTF-8");
			ReturnObject returnObject = new ReturnObject(true, mailConfig);
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		} else if (configuration.equals("generalConfig")) {
			Map<String, List<String>> parameters = webServer.decodeParameters(session.getQueryParameterString());

			String baseUrl = session.getParms().get("baseUrl");
			boolean imdbActive = Boolean.parseBoolean(session.getParms().get("imdbActive"));
			boolean tmdbActive = Boolean.parseBoolean(session.getParms().get("tmdbActive"));
			String tmdbapikey = session.getParms().get("tmdbapikey");
			String tmdbLanguage = StringUtils.join(parameters.get("tmdbLanguage"), ",");
			if (StringUtils.isNotBlank(baseUrl)) {
				PreferenceManager.getPreferences().setBaseUrl(baseUrl);
				PreferenceManager.getPreferences().setImdbActive(imdbActive);
				PreferenceManager.getPreferences().setTmdbActive(tmdbActive);
				PreferenceManager.getPreferences().setTmdbApiKey(tmdbapikey);
				PreferenceManager.getPreferences().setTmdbLanguage(tmdbLanguage);
			}

			PreferenceManager.savePreferences();

			response.setMimeType("application/json; charset=UTF-8");
			Map<String, Object> generalConfig = new HashMap<>();
			generalConfig.put("baseUrl", PreferenceManager.getPreferences().getBaseUrl());
			generalConfig.put("imdbActive", PreferenceManager.getPreferences().isImdbActive());
			generalConfig.put("tmdbActive", PreferenceManager.getPreferences().isTmdbActive());
			generalConfig.put("tmdbapikey", PreferenceManager.getPreferences().getTmdbApiKey());
			generalConfig.put("tmdbLanguage", Arrays.asList(PreferenceManager.getPreferences().getTmdbLanguage().split(",")));
			ReturnObject returnObject = new ReturnObject(true, generalConfig);
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		}
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
