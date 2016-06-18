package com.shuffle.vnt.web.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.TrackerUser;

@Entity
public class TrackerUserUser extends GenericEntity {

	private static final long serialVersionUID = 294194393774566374L;

	@ManyToOne(targetEntity = TrackerUser.class, optional = false, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "trackeruser_id")
	private TrackerUser trackerUser;

	@ManyToOne(targetEntity = User.class, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

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
