package com.shuffle.vnt.core.schedule.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.PersistenceManager.PostPersist;
import com.shuffle.vnt.core.db.PersistenceManager.PostRemove;
import com.shuffle.vnt.core.db.PersistenceManager.PostUpdate;
import com.shuffle.vnt.core.db.PersistenceManager.PrePersist;
import com.shuffle.vnt.core.db.PersistenceManager.PreUpdate;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.db.persister.ServiceParserDataPersister;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.schedule.ScheduleManager;
import com.shuffle.vnt.core.security.SecurityContext;
import com.shuffle.vnt.core.service.Service;
import com.shuffle.vnt.core.service.ServiceParserData;
import com.shuffle.vnt.web.model.User;

@DatabaseTable
public class Job extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 4832823456379541769L;

	@DatabaseField
	private String name;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private TrackerUser trackerUser;

	@DatabaseField(width = Integer.MAX_VALUE, dataType = DataType.SERIALIZABLE)
	private QueryParameters queryParameters;

	@DatabaseField
	private Class<? extends Service> serviceParser;

	@JsonIgnore
	@DatabaseField(columnName = "serviceParserData_id", persisterClass = ServiceParserDataPersister.class)
	private ServiceParserData serviceParserData;

	@DatabaseField
	private String email;

	@DatabaseField
	private Date startDate;

	@DatabaseField
	private Date nextRun;

	@DatabaseField
	private long interval;

	@DatabaseField(width = Integer.MAX_VALUE, dataType = DataType.BYTE_ARRAY)
	private byte[] template;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private User user;

	@PrePersist
	public void beforePersist() {
		setUser(SecurityContext.getUser());
	}

	@PrePersist
	@PreUpdate
	public void beforePersistUpdate() {
		if (SecurityContext.getUser() != null) {
			
			if (getId() != null) {
				Job old = PersistenceManager.getDao(Job.class).findOne(getId());
				if (old.getStartDate().compareTo(getStartDate()) != 0) {
					setNextRun(getStartDate());
				}
				List<JobSeedbox> jobSeedboxs = PersistenceManager.getDao(JobSeedbox.class).eq("job", this).findAll();
				jobSeedboxs.stream().forEach(PersistenceManager.getDao(JobSeedbox.class)::remove);
			}
			else {
				setNextRun(getStartDate());
			}
		}
	}
	
	@PostPersist
	@PostUpdate
	@PostRemove
	private void updateSchedule() {
		if (SecurityContext.getUser() != null) {
			ScheduleManager.getInstance().clearSchedules();
			ScheduleManager.getInstance().updateSchedules();
			List<Seedbox> seedboxs = PersistenceManager.getDao(Seedbox.class).eq("user", SecurityContext.getUser()).findAll();
			seedboxs.stream().forEach(s -> {
				JobSeedbox jobSeedbox = new JobSeedbox();
				jobSeedbox.setJob(this);
				jobSeedbox.setSeedbox(s);
				PersistenceManager.getDao(JobSeedbox.class).save(jobSeedbox);
			});
		}
	}

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

	public Class<? extends Service> getServiceParser() {
		return serviceParser;
	}

	public void setServiceParser(Class<? extends Service> serviceParser) {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Job [name=" + name + ", trackerUser=" + trackerUser + ", queryParameters=" + queryParameters + ", serviceParser=" + serviceParser + ", serviceParserData=" + serviceParserData + ", email=" + email + ", startDate=" + startDate
				+ ", nextRun=" + nextRun + ", interval=" + interval + ", template=" + Arrays.toString(template) + ", user=" + user + ", id=" + id + "]";
	}

}
