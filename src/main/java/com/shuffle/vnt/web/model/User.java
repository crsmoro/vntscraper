package com.shuffle.vnt.web.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.security.OneWayPasswordJsonDeserializer;

@DatabaseTable
public class User extends GenericEntity implements Serializable {

	private static final long serialVersionUID = -1611137472835797283L;

	@DatabaseField
	private String username;

	@JsonProperty(access = Access.WRITE_ONLY)
	@JsonDeserialize(using = OneWayPasswordJsonDeserializer.class)
	@DatabaseField
	private String password;

	@DatabaseField
	private boolean admin;

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

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=[Protected], admin=" + admin + ", id=" + id + "]";
	}
}
