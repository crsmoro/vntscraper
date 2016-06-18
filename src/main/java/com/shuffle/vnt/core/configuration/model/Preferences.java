package com.shuffle.vnt.core.configuration.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.shuffle.vnt.core.db.model.GenericEntity;

@Entity
public class Preferences extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 7482942706150638896L;

	private String baseUrl;

	private boolean imdbActive;

	private boolean tmdbActive;

	private String tmdbApiKey = "";

	private String tmdbLanguage = "";

	@ManyToOne(targetEntity = MailConfig.class, optional = true)
	@JoinColumn(name = "mailconfig_id")
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

	@Override
	public String toString() {
		return "Preferences [baseUrl=" + baseUrl + ", imdbActive=" + imdbActive + ", tmdbActive=" + tmdbActive + ", tmdbApiKey=" + tmdbApiKey + ", tmdbLanguage=" + tmdbLanguage + ", mailConfig=" + mailConfig + ", id=" + id + "]";
	}
}
