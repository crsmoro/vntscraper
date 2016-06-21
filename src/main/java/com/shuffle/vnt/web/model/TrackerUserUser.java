package com.shuffle.vnt.web.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.TrackerUser;

@DatabaseTable
public class TrackerUserUser extends GenericEntity {

	private static final long serialVersionUID = 294194393774566374L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private TrackerUser trackerUser;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	@DatabaseField
	private boolean shared;

	public TrackerUser getTrackerUser() {
		return trackerUser;
	}

	public void setTrackerUser(TrackerUser trackerUser) {
		this.trackerUser = trackerUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	@Override
	public String toString() {
		return "TrackerUserUser [trackerUser=" + trackerUser + ", user=" + user + ", shared=" + shared + ", id=" + id + "]";
	}
}
