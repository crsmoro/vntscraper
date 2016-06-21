package com.shuffle.vnt.web.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;

@DatabaseTable
public class Session extends GenericEntity {

	private static final long serialVersionUID = 4709977177346914929L;

	@DatabaseField
	private String session;

	@DatabaseField
	private Date lastRequest;

	@DatabaseField
	private String lastIP;

	@JsonBackReference
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Date getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Date lastRequest) {
		this.lastRequest = lastRequest;
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Session [session=" + session + ", lastRequest=" + lastRequest + ", lastIP=" + lastIP + ", user=" + user + ", id=" + id + "]";
	}
}
