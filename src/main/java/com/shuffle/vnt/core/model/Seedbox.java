package com.shuffle.vnt.core.model;

import java.io.Serializable;

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

@DatabaseTable
public class Seedbox extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 778532101757538034L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	@DatabaseField
	private String name;

	@DatabaseField
	private String url;

	@DatabaseField
	private String username;

	@JsonProperty(access = Access.WRITE_ONLY)
	@JsonDeserialize(using = TwoWayPasswordJsonDeserializer.class)
	@DatabaseField
	private String password;

	@DatabaseField
	private String label;

	@DatabaseField
	private String webClient;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getWebClient() {
		return webClient;
	}

	public void setWebClient(String webClient) {
		this.webClient = webClient;
	}

	@Override
	public String toString() {
		return "Seedbox [user=" + user + ", name=" + name + ", url=" + url + ", username=" + username + ", password=" + password + ", label=" + label + ", webClient=" + webClient + ", id=" + id + "]";
	}
}
