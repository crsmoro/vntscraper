package com.shuffle.vnt.core.schedule.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.Seedbox;

@DatabaseTable
public class JobSeedbox extends GenericEntity {

	private static final long serialVersionUID = -7295732891012958305L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Job job;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Seedbox seedbox;

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Seedbox getSeedbox() {
		return seedbox;
	}

	public void setSeedbox(Seedbox seedbox) {
		this.seedbox = seedbox;
	}
}