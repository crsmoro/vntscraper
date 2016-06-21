package com.shuffle.vnt.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.util.ClassPersister;
import com.shuffle.vnt.util.VntUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
@DatabaseTable
public class TrackerUser extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 5696427569781451442L;

	private static final Log log = LogFactory.getLog(TrackerUser.class);

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

	@JsonManagedReference
	@ForeignCollectionField(eager = true, foreignFieldName = "trackerUser")
	private Collection<Cookie> cookies = new HashSet<Cookie>();

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

	public Collection<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(Collection<Cookie> cookies) {
		this.cookies = cookies;
	}

	@JsonIgnore
	public List<org.apache.http.cookie.Cookie> getRealCookies() {
		List<org.apache.http.cookie.Cookie> cookies = new ArrayList<>();
		for (Cookie cookieData : getCookies()) {
			BasicClientCookie cookie = new BasicClientCookie(cookieData.getName(), cookieData.getValue());
			cookie.setPath("/");
			cookie.setDomain(VntUtil.getDomain(getTracker().getUrl()));
			cookies.add(cookie);
		}
		return cookies;
	}

	@JsonIgnore
	public Map<String, String> getMapCookies() {
		Map<String, String> returnCookies = new HashMap<>();
		for (org.apache.http.cookie.Cookie cookie : getRealCookies()) {
			returnCookies.put(cookie.getName(), cookie.getValue());
		}
		return returnCookies;
	}

	public boolean cookieExpired() {
		Date now = new Date();
		for (Cookie cookie : getCookies()) {
			if (cookie.getExpiration() < now.getTime()) {
				return true;
			}
		}
		return getCookies().isEmpty();
	}

	public void updateCookies(List<org.apache.http.cookie.Cookie> cookies) {
		Map<String, String> mapCookies = new HashMap<>();
		for (org.apache.http.cookie.Cookie cookie : cookies) {
			mapCookies.put(cookie.getName(), cookie.getValue());
		}
		updateCookies(mapCookies);
	}

	public void updateCookies(Map<String, String> cookies) {
		getCookies().removeIf(cookie -> {
			PersistenceManager.getDao(Cookie.class).remove(cookie);
			return true;
		});

		for (String cookieName : cookies.keySet()) {
			Cookie cookie = new Cookie();
			cookie.setName(cookieName);
			cookie.setValue(cookies.get(cookieName));
			cookie.setExpiration(new Date().getTime() + (72 * 60 * 60 * 1000));
			cookie.setTrackerUser(this);
			PersistenceManager.getDao(Cookie.class).save(cookie);
			getCookies().add(cookie);
		}
		log.debug("Updating Cookies of : " + this);
	}

	@Override
	public String toString() {
		return "TrackerUser [tracker=" + tracker + ", trackerClass=" + trackerClass + ", username=" + username + ", password=[Protected], cookies=" + cookies + ", id=" + id + "]";
	}
}