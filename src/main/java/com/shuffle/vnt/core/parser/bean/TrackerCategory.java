package com.shuffle.vnt.core.parser.bean;

import java.io.Serializable;

public class TrackerCategory implements Serializable {

    private static final long serialVersionUID = 6918176449347028915L;

    private String name;

    private String property;

    private String code;

    public TrackerCategory(String name, String property, String code) {
	super();
	this.name = name;
	this.property = property;
	this.code = code;
    }

    public TrackerCategory() {
	super();
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getProperty() {
	return property;
    }

    public void setProperty(String property) {
	this.property = property;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }
}
