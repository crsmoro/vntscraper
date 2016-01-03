package com.shuffle.vnt.web.servlets;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.Seedbox;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class LoadSeedboxes implements HttpServlet {

	@Override
    public void setWebServer(WebServer webServer) {
	
    }

	@Override
	public void doGet(IHTTPSession session, Response response) {
		response.setMimeType("application/json");

		String pkname = session.getParms().get("pkname");
		if (pkname != null && !"".equals(pkname)) {
			Seedbox seedbox = PreferenceManager.getInstance().getSeedbox(pkname);
			if (seedbox != null) {
				response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(buildSeedbox(seedbox))));
			}
		} else {
			List<JsonElement> seedboxes = new ArrayList<>();
			for (Seedbox seedbox : PreferenceManager.getInstance().getPreferences().getSeedboxes()) {
				seedboxes.add(buildSeedbox(seedbox));
			}
			response.setData(VntUtil.getInputStream(VntUtil.getGson().toJson(seedboxes)));
		}
	}

	private JsonElement buildSeedbox(Seedbox seedbox) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", seedbox.getName());
		jsonObject.addProperty("url", seedbox.getUrl());
		jsonObject.addProperty("username", seedbox.getUsername());
		jsonObject.addProperty("label", seedbox.getLabel());
		jsonObject.addProperty("webClient", seedbox.getWebClient() != null ?seedbox.getWebClient().getName(): "");
		return jsonObject;
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
