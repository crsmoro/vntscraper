package com.shuffle.vnt.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;

@DatabaseTable
public class Cookie extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 3995909762051248616L;

	@DatabaseField
	private String name;

	@DatabaseField
	private String value;

	@DatabaseField
	private long expiration;

	@JsonBackReference
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private TrackerUser trackerUser;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	public TrackerUser getTrackerUser() {
		return trackerUser;
	}

	public void setTrackerUser(TrackerUser trackerUser) {
		this.trackerUser = trackerUser;
	}

	@Override
	public String toString() {
		return "Cookie [name=" + name + ", value=" + value + ", expiration=" + expiration + ", id=" + id + "]";
	}
}