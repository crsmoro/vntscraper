package com.shuffle.vnt.core.parser.bean;

import java.io.Serializable;

public class TorrentFilter implements Serializable {

    private static final long serialVersionUID = -1560587325966880062L;

    private String field;

    public enum FilterOperation {
	EQ, NE, LT, GT, LE, GE, LIKE, NLIKE, REGEX
    }

    private FilterOperation operation;

    private Object value;

    public TorrentFilter(String field, FilterOperation operation, Object value) {
	super();
	this.field = field;
	this.operation = operation;
	this.value = value;
    }

    public TorrentFilter() {
	super();
    }

    public String getField() {
	return field;
    }

    public void setField(String field) {
	this.field = field;
    }

    public FilterOperation getOperation() {
	return operation;
    }

    public void setOperation(FilterOperation operation) {
	this.operation = operation;
    }

    public Object getValue() {
	return value;
    }

    public void setValue(Object value) {
	this.value = value;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((field == null) ? 0 : field.hashCode());
	result = prime * result + ((operation == null) ? 0 : operation.hashCode());
	result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	TorrentFilter other = (TorrentFilter) obj;
	if (field == null) {
	    if (other.field != null)
		return false;
	} else if (!field.equals(other.field))
	    return false;
	if (operation != other.operation)
	    return false;
	if (value == null) {
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }
}
