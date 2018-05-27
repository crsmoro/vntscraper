package com.shuffle.vnt.core.configuration.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;

@DatabaseTable
public class TmdbLanguage extends GenericEntity implements Serializable {

	private static final long serialVersionUID = -6660958023885011360L;

	@DatabaseField
	private Long order;
	
	@DatabaseField
	private String language;
	
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	@JsonBackReference
	private Preferences preferences;

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}
}
