package com.shuffle.vnt.configuration.bean;

import java.io.Serializable;

import com.shuffle.vnt.services.torrentmanager.WebClient;

public class Seedbox implements Serializable {

	private static final long serialVersionUID = 778532101757538034L;

	private String name;

	private String url;

	private String username;

	private String password;

	private String label;

	private Class<? extends WebClient> webClient;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Class<? extends WebClient> getWebClient() {
		return webClient;
	}

	public void setWebClient(Class<? extends WebClient> webClient) {
		this.webClient = webClient;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Seedbox other = (Seedbox) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Seedbox [name=" + name + ", url=" + url + ", username=" + username + ", password=[Protected], label=" + label + ", webClient=" + webClient + "]";
	}
}
