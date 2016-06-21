package com.shuffle.vnt.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.service.WebClient;
import com.shuffle.vnt.util.ClassPersister;

@DatabaseTable
public class Seedbox extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 778532101757538034L;

	@DatabaseField
	private String name;

	@DatabaseField
	private String url;

	@DatabaseField
	private String username;

	@JsonIgnore
	@DatabaseField
	private String password;

	@DatabaseField
	private String label;

	@DatabaseField(persisterClass = ClassPersister.class)
	private Class<? extends WebClient> webClient;

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

	public Class<? extends WebClient> getWebClient() {
		return webClient;
	}

	public void setWebClient(Class<? extends WebClient> webClient) {
		this.webClient = webClient;
	}

	@Override
	public String toString() {
		return "Seedbox [name=" + name + ", url=" + url + ", username=" + username + ", password=[Protected], label=" + label + ", webClient=" + webClient + ", id=" + id + "]";
	}
}
