package com.shuffle.vnt.core.configuration.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;

@DatabaseTable
public class Preferences extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 7482942706150638896L;

	@DatabaseField
	private String baseUrl;

	@DatabaseField
	private boolean imdbActive;

	@DatabaseField
	private boolean tmdbActive;

	@DatabaseField
	private String tmdbApiKey = "";

	@DatabaseField
	private String tmdbLanguage = "";

	@DatabaseField
	private Long maxSessionsPerUser = 5l;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private MailConfig mailConfig;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean isImdbActive() {
		return imdbActive;
	}

	public void setImdbActive(boolean imdbActive) {
		this.imdbActive = imdbActive;
	}

	public boolean isTmdbActive() {
		return tmdbActive;
	}

	public void setTmdbActive(boolean tmdbActive) {
		this.tmdbActive = tmdbActive;
	}

	public String getTmdbApiKey() {
		return tmdbApiKey;
	}

	public void setTmdbApiKey(String tmdbApiKey) {
		this.tmdbApiKey = tmdbApiKey;
	}

	public String getTmdbLanguage() {
		return tmdbLanguage;
	}

	public void setTmdbLanguage(String tmdbLanguage) {
		this.tmdbLanguage = tmdbLanguage;
	}

	public MailConfig getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(MailConfig mailConfig) {
		this.mailConfig = mailConfig;
	}

	public Long getMaxSessionsPerUser() {
		return maxSessionsPerUser;
	}

	public void setMaxSessionsPerUser(Long maxSessionsPerUser) {
		this.maxSessionsPerUser = maxSessionsPerUser;
	}

	@Override
	public String toString() {
		return "Preferences [baseUrl=" + baseUrl + ", imdbActive=" + imdbActive + ", tmdbActive=" + tmdbActive + ", tmdbApiKey=" + tmdbApiKey + ", tmdbLanguage=" + tmdbLanguage + ", maxSessionsPerUser=" + maxSessionsPerUser + ", mailConfig="
				+ mailConfig + ", id=" + id + "]";
	}
}
