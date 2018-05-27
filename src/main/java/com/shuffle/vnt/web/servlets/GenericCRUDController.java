package com.shuffle.vnt.web.servlets;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class GenericCRUDController<E extends GenericEntity> implements HttpServlet {

	protected Class<E> entityClass;

	private WebServer webServer;

	@SuppressWarnings("unchecked")
	public GenericCRUDController() {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.entityClass = (Class<E>) genericSuperclass.getActualTypeArguments()[0];
	}

	@Override
	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		if (StringUtils.isNotBlank(Optional.ofNullable(session.getParameters().get("id")).orElse(Collections.emptyList()).stream().findFirst().orElse(""))) {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).idEq(Long.valueOf(session.getParameters().get("id").stream().findFirst().get())).findOne())));
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).findAll())));
		}
	}

	@Override
	public void doPost(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		saveEntityFromPayload(session, response);
	}

	@Override
	public void doPut(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		saveEntityFromPayload(session, response);
	}

	private void saveEntityFromPayload(IHTTPSession session, Response response) {
		try {

			String json = webServer.getFiles().get("postData");
			if (StringUtils.isBlank(json)) {
				json = Files.lines(Paths.get(webServer.getFiles().get("content"))).collect(Collectors.joining(System.lineSeparator()));
			}
			E entity = VntUtil.fromJson(json, entityClass);
			if (entity.getId() != null)
			{
				entity = VntUtil.getObjectMapper().readerForUpdating(PersistenceManager.getDao(entityClass).idEq(entity.getId()).findOne()).readValue(json);
			}
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).save(entity))));
		} catch (IOException e) {
			response.setStatus(Status.BAD_REQUEST);
		}
	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		PersistenceManager.getDao(entityClass).remove(Long.valueOf(Optional.ofNullable(session.getParameters().get("id")).orElse(Collections.emptyList()).stream().findFirst().orElse("-1")));
		response.setData(VntUtil.getInputStream(VntUtil.toJson(new ReturnObject(true, null))));
	}

}
