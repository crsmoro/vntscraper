package com.shuffle.vnt.configuration.bean;

import java.io.Serializable;

import com.shuffle.vnt.core.parser.bean.QueryParameters;

public class FetchNew implements Serializable {

	private static final long serialVersionUID = -6213577732237570031L;

	private long last;

	private TrackerUser trackerUser;

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
		return "FetchNew [last=" + last + ", trackerUser=" + trackerUser + ", queryParameters=" + queryParameters + "]";
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
