package com.shuffle.vnt.web.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.shuffle.vnt.core.db.model.GenericEntity;

@Entity
public class Session extends GenericEntity {

	private static final long serialVersionUID = 4709977177346914929L;

	@Column(unique = true)
	private String session;

	private Date lastRequest;

	private String lastIP;

	@ManyToOne(targetEntity = User.class, optional = false)
	@JoinColumn(name = "user_id")
	@JsonBackReference
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
