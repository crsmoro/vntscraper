package com.shuffle.vnt.service.fetchnew.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.service.ServiceParserData;

@DatabaseTable
public class FetchNew extends ServiceParserData implements Serializable {

	private static final long serialVersionUID = -6213577732237570031L;

	@DatabaseField
	private long last;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private TrackerUser trackerUser;

	@DatabaseField(width = Integer.MAX_VALUE, dataType = DataType.SERIALIZABLE)
	private QueryParameters queryParameters;

	public long getLast() {
		return last;
	}

	public void setLast(long last) {
		this.last = last;
	}

	public TrackerUser getTrackerUser() {
		return trackerUser;
	}

	public void setTrackerUser(TrackerUser trackerUser) {
		this.trackerUser = trackerUser;
	}

	public QueryParameters getQueryParameters() {
		return queryParameters;
	}

	public void setQueryParameters(QueryParameters queryParameters) {
		this.queryParameters = queryParameters;
	}

	@Override
	public String toString() {
		return "FetchNew [last=" + last + ", trackerUser=" + trackerUser + ", queryParameters=" + queryParameters + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((queryParameters == null) ? 0 : queryParameters.hashCode());
		result = prime * result + ((trackerUser == null) ? 0 : trackerUser.hashCode());
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
		FetchNew other = (FetchNew) obj;
		if (queryParameters == null) {
			if (other.queryParameters != null)
				return false;
		} else if (!queryParameters.equals(other.queryParameters))
			return false;
		if (trackerUser == null) {
			if (other.trackerUser != null)
				return false;
		} else if (!trackerUser.equals(other.trackerUser))
			return false;
		return true;
	}
}
