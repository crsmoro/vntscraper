package com.shuffle.vnt.configuration.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.shuffle.vnt.util.JsonIgnore;

public class TrackerUser implements Serializable {
	
	private static final long serialVersionUID = 5696427569781451442L;

	private String tracker;

	private String username;

	@JsonIgnore
	private String password;

	private List<Cookie> cookies = new ArrayList<Cookie>();

	public String getTracker() {
		return tracker;
	}

	public void setTracker(String tracker) {
		this.tracker = tracker;
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

	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	@Override
	public String toString() {
		return "TrackerData [name=" + tracker + ", username=" + username + ", password=[Protected], cookies="
				+ cookies + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((tracker == null) ? 0 : tracker.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackerUser other = (TrackerUser) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (tracker == null) {
			if (other.tracker != null)
				return false;
		} else if (!tracker.equals(other.tracker))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}