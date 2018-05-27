package com.shuffle.vnt.web.servlets;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.schedule.model.Job;
import com.shuffle.vnt.core.security.SecurityContext;
import com.shuffle.vnt.util.VntUtil;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class Jobs extends GenericCRUDController<Job> {

	@Override
	public void doGet(IHTTPSession session, Response response) {
		response.setMimeType("application/json");
		if (StringUtils.isNotBlank(Optional.ofNullable(session.getParameters().get("id")).orElse(Collections.emptyList()).stream().findFirst().orElse(""))) {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).idEq(Long.valueOf(session.getParameters().get("id").stream().findFirst().get())).findOne())));
		} 
		else if (StringUtils.isNotBlank(Optional.ofNullable(session.getParameters().get("count")).orElse(Collections.emptyList()).stream().findFirst().orElse(""))) {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).eq("user", SecurityContext.getUser()).findAll().size())));
		}
		else {
			response.setData(VntUtil.getInputStream(VntUtil.toJson(PersistenceManager.getDao(entityClass).eq("user", SecurityContext.getUser()).findAll())));
		}
	}
}
