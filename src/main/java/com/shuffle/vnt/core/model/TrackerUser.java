package com.shuffle.vnt.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.db.persister.ClassPersister;
import com.shuffle.vnt.core.parser.Tracker;

@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable
public class TrackerUser extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 5696427569781451442L;

	@JsonIgnore
	@DatabaseField(persisted = false)
	private Tracker tracker;

	@JsonProperty(value = "tracker")
	@DatabaseField(persisterClass = ClassPersister.class)
	private Class<? extends Tracker> trackerClass;

	@DatabaseField
	private String username;

	@JsonIgnore
	@DatabaseField
	private String password;

	public Tracker getTracker() {
		if (trackerClass == null || tracker == null || !trackerClass.equals(tracker.getClass())) {
			tracker = Tracker.getInstance(getTrackerClass());
		}
		return tracker;
	}

	public void setTracker(Tracker tracker) {
		this.tracker = tracker;
		setTrackerClass(tracker.getClass());
	}

	private Class<? extends Tracker> getTrackerClass() {
		return trackerClass;
	}

	private void setTrackerClass(Class<? extends Tracker> trackerClass) {
		this.trackerClass = trackerClass;
	}

	@JsonProperty(value = "trackerName")
	private String getTrackerName() {
		return getTracker().getName();
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

	@Override
	public String toString() {
		return "TrackerUser [tracker=" + tracker + ", trackerClass=" + trackerClass + ", username=" + username + ", password=[Protected], id=" + id + "]";
	}
}