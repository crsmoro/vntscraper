package com.shuffle.vnt.web.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.Seedbox;

@DatabaseTable
public class UserSeedbox extends GenericEntity {

	private static final long serialVersionUID = 1199527451743760860L;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private Seedbox seedbox;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Seedbox getSeedbox() {
		return seedbox;
	}

	public void setSeedbox(Seedbox seedbox) {
		this.seedbox = seedbox;
	}
}
