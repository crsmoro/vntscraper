package com.shuffle.vnt.core.configuration.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.configuration.PreferenceManager;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.PersistenceManager.PostPersist;
import com.shuffle.vnt.core.db.PersistenceManager.PostUpdate;
import com.shuffle.vnt.core.db.model.GenericEntity;

@DatabaseTable
public class Preferences extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 7482942706150638896L;

	@DatabaseField
	@JsonIgnore
	private String tokenKey;

	@DatabaseField
	@JsonIgnore
	private String refreshTokenKey;

	@DatabaseField
	@JsonIgnore
	private String passwordKey;

	@DatabaseField
	private String baseUrl;

	@DatabaseField
	private boolean omdbActive;

	@DatabaseField
	private String omdbApiKey;

	@DatabaseField
	private boolean tmdbActive;

	@DatabaseField
	private String tmdbApiKey;

	@ForeignCollectionField(orderColumnName = "order")
	@JsonManagedReference
	private Collection<TmdbLanguage> tmdbLanguages;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
	private MailConfig mailConfig;

	public String getTokenKey() {
		return tokenKey;
	}

	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}

	public String getRefreshTokenKey() {
		return refreshTokenKey;
	}

	public void setRefreshTokenKey(String refreshTokenKey) {
		this.refreshTokenKey = refreshTokenKey;
	}

	public String getPasswordKey() {
		return passwordKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public boolean isOmdbActive() {
		return omdbActive;
	}

	public void setOmdbActive(boolean omdbActive) {
		this.omdbActive = omdbActive;
	}

	public String getOmdbApiKey() {
		return omdbApiKey;
	}

	public void setOmdbApiKey(String omdbApiKey) {
		this.omdbApiKey = omdbApiKey;
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

	public Collection<TmdbLanguage> getTmdbLanguages() {
		return tmdbLanguages;
	}

	public void setTmdbLanguages(Collection<TmdbLanguage> tmdbLanguages) {
		List<TmdbLanguage> allTmdbLanguages = PersistenceManager.getDao(TmdbLanguage.class).findAll();
		for (TmdbLanguage tmdbLanguage : tmdbLanguages) {
			if (tmdbLanguage.getId() == null || allTmdbLanguages.contains(tmdbLanguage)) {
				allTmdbLanguages.remove(tmdbLanguage);
			}
			tmdbLanguage.setPreferences(this);
			PersistenceManager.getDao(TmdbLanguage.class).save(tmdbLanguage);
		}
		allTmdbLanguages.forEach(PersistenceManager.getDao(TmdbLanguage.class)::remove);
		this.tmdbLanguages = tmdbLanguages;
	}

	public MailConfig getMailConfig() {
		return mailConfig;
	}

	public void setMailConfig(MailConfig mailConfig) {
		if (mailConfig != null) {
			PersistenceManager.getDao(MailConfig.class).save(mailConfig);
		}
		this.mailConfig = mailConfig;
	}
	
	@PostPersist
	@PostUpdate
	private void afterSave() {
		PreferenceManager.reloadPreferences();
	}

	@Override
	public String toString() {
		return "Preferences [tokenKey=" + tokenKey + ", refreshTokenKey=" + refreshTokenKey + ", passwordKey=" + passwordKey + ", baseUrl=" + baseUrl + ", omdbActive=" + omdbActive + ", tmdbActive=" + tmdbActive + ", tmdbApiKey=" + tmdbApiKey
				+ ", tmdbLanguages=" + tmdbLanguages + ", mailConfig=" + mailConfig + ", id=" + id + "]";
	}
}
