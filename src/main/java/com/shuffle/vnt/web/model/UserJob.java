package com.shuffle.vnt.web.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.schedule.model.Job;

@DatabaseTable
public class UserJob extends GenericEntity {

	private static final long serialVersionUID = -3042240885283727106L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Job job;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
}
