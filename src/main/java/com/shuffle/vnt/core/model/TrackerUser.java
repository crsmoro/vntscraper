package com.shuffle.vnt.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.PersistenceManager.PrePersist;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.security.SecurityContext;
import com.shuffle.vnt.core.security.TwoWayPasswordJsonDeserializer;
import com.shuffle.vnt.web.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable
public class TrackerUser extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 5696427569781451442L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	@DatabaseField
	private String tracker;

	@DatabaseField
	private String username;

	@JsonProperty(access = Access.WRITE_ONLY)
	@JsonDeserialize(using = TwoWayPasswordJsonDeserializer.class)
	@DatabaseField
	private String password;

	@DatabaseField
	private boolean shared;

	@PrePersist
	public void beforePersist() {
		setUser(SecurityContext.getUser());
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	@Override
	public String toString() {
		return "TrackerUser [user=" + user + ", tracker=" + tracker + ", username=" + username + ", password=[Protected], shared=" + shared + ", id=" + id + "]";
	}

}