package com.shuffle.vnt.web.servlets;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.MailConfig;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.ReturnObject;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Settings implements HttpServlet {

    @Override
    public void setWebServer(WebServer webServer) {

    }

    @Override
    public void doGet(IHTTPSession session, Response response) {
	String configuration = session.getParms().get("configuration");
	if (configuration.equals("mailConfig")) {
	    MailConfig mailConfig = PreferenceManager.getInstance().getPreferences().getMailConfig();
	    response.setMimeType("application/json; charset=UTF-8");
	    ReturnObject returnObject = new ReturnObject(true, mailConfig);
	    response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}
	else if (configuration.equals("generalConfig")) {
	    response.setMimeType("application/json; charset=UTF-8");
	    Map<String, Object> generalConfig = new HashMap<>();
	    generalConfig.put("baseUrl", PreferenceManager.getInstance().getPreferences().getBaseUrl());
	    ReturnObject returnObject = new ReturnObject(true, generalConfig);
	    response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}
    }

    @Override
    public void doPost(IHTTPSession session, Response response) {
	String configuration = session.getParms().get("configuration");
	if (configuration.equals("mailConfig")) {
	    MailConfig mailConfig = PreferenceManager.getInstance().getPreferences().getMailConfig();
	    if (mailConfig == null) {
		mailConfig = new MailConfig();
		PreferenceManager.getInstance().getPreferences().setMailConfig(mailConfig);
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
	    PreferenceManager.getInstance().savePreferences();
	    
	    response.setMimeType("application/json; charset=UTF-8");
	    ReturnObject returnObject = new ReturnObject(true, mailConfig);
	    response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}
	else if (configuration.equals("generalConfig")) {
	    String baseUrl = session.getParms().get("baseUrl");
	    if (StringUtils.isNotBlank(baseUrl)) {
		PreferenceManager.getInstance().getPreferences().setBaseUrl(baseUrl);
	    }
	    
	    PreferenceManager.getInstance().savePreferences();
	    
	    response.setMimeType("application/json; charset=UTF-8");
	    Map<String, Object> generalConfig = new HashMap<>();
	    generalConfig.put("baseUrl", PreferenceManager.getInstance().getPreferences().getBaseUrl());
	    ReturnObject returnObject = new ReturnObject(true, generalConfig);
	    response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(returnObject)));
	}
    }

    @Override
    public void doPut(IHTTPSession session, Response response) {

    }

    @Override
    public void doDelete(IHTTPSession session, Response response) {

    }

}
