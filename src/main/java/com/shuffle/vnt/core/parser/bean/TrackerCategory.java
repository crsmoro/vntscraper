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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((property == null) ? 0 : property.hashCode());
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
	TrackerCategory other = (TrackerCategory) obj;
	if (code == null) {
	    if (other.code != null)
		return false;
	} else if (!code.equals(other.code))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (property == null) {
	    if (other.property != null)
		return false;
	} else if (!property.equals(other.property))
	    return false;
	return true;
    }
}
