package com.shuffle.vnt.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.schedule.model.Job;

@DatabaseTable
public class User extends GenericEntity implements Serializable {

	private static final long serialVersionUID = -1611137472835797283L;

	@DatabaseField
	private String username;

	@JsonIgnore
	@DatabaseField
	private String password;

	@DatabaseField
	private boolean admin;

	@JsonManagedReference
	@ForeignCollectionField(foreignFieldName = "user", eager = true)
	private Collection<Session> sessions = new ArrayList<>();

	@JsonIgnore
	@DatabaseField(persisted = false)
	private Collection<Seedbox> seedboxes = new HashSet<>();

	@JsonIgnore
	@DatabaseField(persisted = false)
	private Collection<Job> jobs = new HashSet<>();

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

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public List<Session> getSessions() {
		return sessions.stream().sorted(Comparator.comparing(Session::getLastRequest).reversed()).collect(Collectors.toList());
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public Collection<Seedbox> getSeedboxes() {
		seedboxes.clear();
		PersistenceManager.getDao(UserSeedbox.class).eq("user", this).findAll().forEach(us -> seedboxes.add(us.getSeedbox()));
		return seedboxes;
	}

	public void setSeedboxes(Collection<Seedbox> seedboxes) {
		seedboxes.forEach(seedbox -> {
			UserSeedbox userSeedbox = PersistenceManager.getDao(UserSeedbox.class).eq("user", this).eq("seedbox", seedbox).and(2).findOne();
			if (userSeedbox == null) {
				userSeedbox = new UserSeedbox();
			}
			userSeedbox.setUser(this);
			userSeedbox.setSeedbox(seedbox);
			PersistenceManager.getDao(UserSeedbox.class).save(userSeedbox);
		});
		this.seedboxes = seedboxes;
	}

	public Collection<Job> getJobs() {
		jobs.clear();
		PersistenceManager.getDao(UserJob.class).eq("user", this).findAll().forEach(uj -> {
			jobs.add(uj.getJob());
		});
		return jobs;
	}

	public void setJobs(Collection<Job> jobs) {
		jobs.forEach(job -> {
			UserJob userJob = PersistenceManager.getDao(UserJob.class).eq("user", this).eq("job", job).and(2).findOne();
			if (userJob == null) {
				userJob = new UserJob();
			}
			userJob.setUser(this);
			userJob.setJob(job);
			PersistenceManager.getDao(UserJob.class).save(userJob);
		});
		this.jobs = jobs;
	}

	@DatabaseField(persisted = false)
	@JsonIgnore
	private Set<TrackerUser> trackerUsers = new HashSet<>();

	public Set<TrackerUser> getTrackerUsers() {
		if (trackerUsers.isEmpty()) {
			// FIXME
			// Restrictions.or(Restrictions.eq("user", this),
			// Restrictions.eq("shared", true))
			List<TrackerUserUser> trackerUserUsers = PersistenceManager.getDao(TrackerUserUser.class).eq("user", this).eq("shared", true).and(2).findAll();
			for (TrackerUserUser trackerUserUser : trackerUserUsers) {
				trackerUsers.add(trackerUserUser.getTrackerUser());
			}
		}
		return trackerUsers;
	}

	public TrackerUser getTrackerUser(Tracker tracker) {
		return getTrackerUsers().stream().filter(trackerUser -> trackerUser.getTracker().getClass().equals(tracker.getClass())).findFirst().orElse(null);
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=[Protected], admin=" + admin + ", seedboxes=" + seedboxes + ", jobs=" + jobs + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
