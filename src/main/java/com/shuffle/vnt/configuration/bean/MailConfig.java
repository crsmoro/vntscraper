package com.shuffle.vnt.configuration.bean;

import java.io.Serializable;

import com.shuffle.vnt.util.JsonIgnore;

public class MailConfig implements Serializable {

    private static final long serialVersionUID = -3284582415282668480L;

    private String hostname;

    private int port;

    private boolean ssl;

    private boolean tls;

    private String username;

    @JsonIgnore
    private String password;

    private String from;

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
	this.password = password;
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
}
