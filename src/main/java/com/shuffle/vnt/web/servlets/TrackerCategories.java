package com.shuffle.vnt.web.servlets;

import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class TrackerCategories implements HttpServlet {

	private WebServer webServer;

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		String tracker = Optional.ofNullable(session.getParameters().get("tracker")).orElse(Collections.emptyList()).stream().findFirst().orElse(null);

		List<Map<String, String>> mapCategories = Tracker.loadedTrackers.stream().filter(t -> PersistenceManager.getDao(TrackerUser.class).eq("tracker", t.getName()).eq("user", webServer.getUser()).and(2).findOne() != null)
				.filter(t -> !Optional.ofNullable(tracker).map(String::trim).filter(s -> s.length() > 0).isPresent() || t.getName().equalsIgnoreCase(tracker)).flatMap(t -> {

					return t.getCategories().stream().flatMap(c -> {
						Map<String, String> values = new HashMap<String, String>();
						Arrays.stream(c.getClass().getDeclaredFields()).peek(f -> f.setAccessible(true)).filter(f -> !Modifier.isStatic(f.getModifiers())).map(f -> {
							String value = "";
							try {
								value = Optional.ofNullable(f.get(c)).map(Object::toString).orElse("");
							} catch (Exception dontcare) {

							}
							return new AbstractMap.SimpleEntry<String, String>(f.getName(), value);

						}).forEach(e -> {
							values.put(e.getKey(), e.getValue());
						});
						values.put("tracker", t.getName());
						return Stream.of(values);
					});

				}).collect(Collectors.toList());

		response.setMimeType("application/json");
		response.setData(VntUtil.getInputStream(VntUtil.toJson(mapCategories)));
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
