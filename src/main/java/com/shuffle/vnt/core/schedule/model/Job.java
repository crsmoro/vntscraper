package com.shuffle.vnt.core.schedule.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.core.service.ServiceParserData;

@Entity
public class Job extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 4832823456379541769L;

	private String name;

	@ManyToOne(targetEntity = TrackerUser.class, optional = false)
	@JoinColumn(name = "trackeruser_id")
	private TrackerUser trackerUser;

	@Column(length = Integer.MAX_VALUE)
	@Lob
	private QueryParameters queryParameters;

	private Class<? extends ServiceParser> serviceParser;

	@ManyToOne(targetEntity = ServiceParserData.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "serviceparserdata_id")
	private ServiceParserData serviceParserData;

	private String email;

	private Date startDate;

	private Date nextRun;

	private long interval;

	@Column(length = Integer.MAX_VALUE)
	@Lob
	private byte[] template;

	@OneToMany(targetEntity = Seedbox.class, fetch = FetchType.EAGER)
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = "seedbox_id") }, joinColumns = { @JoinColumn(name = "job_id") }, name = "job_seedboxes")
	private Set<Seedbox> seedboxes = new HashSet<>();

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

	public ServiceParserData getServiceParserData() {
		return serviceParserData;
	}

	public void setServiceParserData(ServiceParserData serviceParserData) {
		this.serviceParserData = serviceParserData;
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

	public byte[] getTemplate() {
		return template;
	}

	public void setTemplate(byte[] template) {
		this.template = template;
	}

	public Set<Seedbox> getSeedboxes() {
		return seedboxes;
	}

	public void setSeedboxes(Set<Seedbox> seedboxes) {
		this.seedboxes = seedboxes;
	}

	@Override
	public String toString() {
		return "SchedulerData [name=" + name + ", trackerUser=" + trackerUser + ", queryParameters=" + queryParameters + ", serviceParser=" + serviceParser + ", email=" + email + ", startDate=" + startDate + ", nextRun=" + nextRun
				+ ", interval=" + interval + ", template=" + (template != null ? new String(template) : null) + ", seedboxes=" + seedboxes;
	}

}
