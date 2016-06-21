package com.shuffle.vnt.core.schedule.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.core.service.ServiceParserData;
import com.shuffle.vnt.util.ClassPersister;

@DatabaseTable
public class Job extends GenericEntity implements Serializable {

	private static final long serialVersionUID = 4832823456379541769L;

	@DatabaseField
	private String name;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	private TrackerUser trackerUser;

	@DatabaseField(width = Integer.MAX_VALUE, dataType = DataType.SERIALIZABLE)
	private QueryParameters queryParameters;

	@DatabaseField(persisterClass = ClassPersister.class)
	private Class<? extends ServiceParser> serviceParser;

	@DatabaseField(foreign = true, foreignAutoRefresh = true)
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

	@DatabaseField(persisted = false)
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
		seedboxes.clear();
		PersistenceManager.getDao(JobSeedbox.class).eq("job", this).findAll().stream().forEach(js -> seedboxes.add(js.getSeedbox()));
		return seedboxes;
	}

	public void setSeedboxes(Set<Seedbox> seedboxes) {
		seedboxes.forEach(seedbox -> {
			JobSeedbox jobSeedbox = PersistenceManager.getDao(JobSeedbox.class).eq("job", this).eq("seedbox", seedboxes).and(2).findOne();
			if (jobSeedbox == null) {
				jobSeedbox = new JobSeedbox();
			}
			jobSeedbox.setJob(this);
			jobSeedbox.setSeedbox(seedbox);
			PersistenceManager.getDao(JobSeedbox.class).save(jobSeedbox);
		});
		this.seedboxes = seedboxes;
	}

	@Override
	public String toString() {
		return "SchedulerData [name=" + name + ", trackerUser=" + trackerUser + ", queryParameters=" + queryParameters + ", serviceParser=" + serviceParser + ", email=" + email + ", startDate=" + startDate + ", nextRun=" + nextRun
				+ ", interval=" + interval + ", template=" + (template != null ? new String(template) : null) + ", seedboxes=" + seedboxes;
	}

}
