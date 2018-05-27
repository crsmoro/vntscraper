package com.shuffle.vnt.core.configuration.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.model.GenericEntity;

@DatabaseTable
public class MailConfig extends GenericEntity implements Serializable {

	private static final long serialVersionUID = -3284582415282668480L;

	@DatabaseField
	private String hostname;

	@DatabaseField
	private int port;

	@DatabaseField
	private boolean ssl;

	@DatabaseField
	private boolean tls;

	@DatabaseField
	private String username;

	@DatabaseField
	private String password;

	@DatabaseField(columnName = "frommail")
	private String from;

	@DatabaseField
	private String fromName;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public boolean isTls() {
		return tls;
	}

	public void setTls(boolean tls) {
		this.tls = tls;
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
		if (StringUtils.isNotBlank(password)) {
			this.password = password;
		}
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	@Override
	public String toString() {
		return "MailConfig [hostname=" + hostname + ", port=" + port + ", ssl=" + ssl + ", tls=" + tls + ", username=" + username + ", password=" + password + ", from=" + from + ", fromName=" + fromName + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((fromName == null) ? 0 : fromName.hashCode());
		result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result + (ssl ? 1231 : 1237);
		result = prime * result + (tls ? 1231 : 1237);
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailConfig other = (MailConfig) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (fromName == null) {
			if (other.fromName != null)
				return false;
		} else if (!fromName.equals(other.fromName))
			return false;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port != other.port)
			return false;
		if (ssl != other.ssl)
			return false;
		if (tls != other.tls)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
