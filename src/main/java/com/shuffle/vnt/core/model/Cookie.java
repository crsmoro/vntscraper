package com.shuffle.vnt.core.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.shuffle.vnt.core.db.model.GenericEntity;

@Entity
public class Cookie extends GenericEntity implements Serializable {
	
	private static final long serialVersionUID = 3995909762051248616L;

	private String name;

	private String value;

	private long expiration;
	
	@ManyToOne(targetEntity = TrackerUser.class, optional = false)
	@JsonBackReference
	@JoinColumn(name = "trackeruser_id")
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