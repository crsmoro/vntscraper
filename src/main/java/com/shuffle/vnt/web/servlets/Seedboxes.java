package com.shuffle.vnt.web.servlets;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.security.SecurityContext;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.WebServer.SecurityFilter;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

@SecurityFilter
public class Seedboxes extends GenericCRUDController<Seedbox> {

	@Override
	public void doGet(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		if (StringUtils.isNotBlank(Optional.ofNullable(session.getParameters().get("id")).orElse(Collections.emptyList()).stream().findFirst().orElse(""))) {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).idEq(Long.valueOf(session.getParameters().get("id").stream().findFirst().get())).findOne())));
		} else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).eq("user", SecurityContext.getUser()).findAll())));
		}
	}
}
