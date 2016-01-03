package com.shuffle.vnt.services.schedule;

import java.io.Serializable;
import java.util.Date;

import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.service.ServiceParser;

public class SchedulerData implements Serializable {

	private static final long serialVersionUID = 4832823456379541769L;

	private String name;

	private TrackerUser trackerUser;

	private QueryParameters queryParameters;

	private Class<? extends ServiceParser> serviceParser;

	private String email;

	private Date startDate;

	private Date nextRun;

	private long interval;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Class<? extends ServiceParser> getServiceParser() {
		return serviceParser;
	}

	public void setServiceParser(Class<? extends ServiceParser> serviceParser) {
		this.serviceParser = serviceParser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getNextRun() {
		return nextRun;
	}

	public void setNextRun(Date nextRun) {
		this.nextRun = nextRun;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	@Override
	public String toString() {
		return "SchedulerData [trackerUser=" + trackerUser + ", queryParameters=" + queryParameters + ", serviceParser="
				+ serviceParser + ", email=" + email + ", startDate=" + startDate + ", nextRun=" + nextRun
				+ ", interval=" + interval + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((queryParameters == null) ? 0 : queryParameters.hashCode());
		result = prime * result + ((serviceParser == null) ? 0 : serviceParser.hashCode());
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
		SchedulerData other = (SchedulerData) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (queryParameters == null) {
			if (other.queryParameters != null)
				return false;
		} else if (!queryParameters.equals(other.queryParameters))
			return false;
		if (serviceParser == null) {
			if (other.serviceParser != null)
				return false;
		} else if (!serviceParser.equals(other.serviceParser))
			return false;
		if (trackerUser == null) {
			if (other.trackerUser != null)
				return false;
		} else if (!trackerUser.equals(other.trackerUser))
			return false;
		return true;
	}

}
