package com.shuffle.vnt.web.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import org.hibernate.criterion.Restrictions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.db.model.GenericEntity;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.schedule.model.Job;

@Entity
public class User extends GenericEntity implements Serializable {

	private static final long serialVersionUID = -1611137472835797283L;

	@Column(unique = true)
	private String username;

	@JsonIgnore
	private String password;

	private boolean admin;

	private String session;
	
	private Date lastRequest;
	
	private String lastIP;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Seedbox.class, fetch = FetchType.EAGER)
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = "seedbox_id") }, joinColumns = { @JoinColumn(name = "user_id") }, name = "user_seedboxes")
	@JsonIgnore
	private Set<Seedbox> seedboxes = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Job.class, fetch = FetchType.EAGER)
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = "job_id") }, joinColumns = { @JoinColumn(name = "user_id") }, name = "user_jobs")
	@JsonIgnore
	private Set<Job> jobs = new HashSet<>();

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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Date getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(Date lastRequest) {
		this.lastRequest = lastRequest;
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public Set<Seedbox> getSeedboxes() {
		return seedboxes;
	}

	public void setSeedboxes(Set<Seedbox> seedboxes) {
		this.seedboxes = seedboxes;
	}

	public Set<Job> getJobs() {
		return jobs;
	}

	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}

	public Set<TrackerUser> getTrackerUsers() {
		Set<TrackerUser> trackerUsers = new HashSet<>();
		List<TrackerUserUser> trackerUserUsers = PersistenceManager.findAll(TrackerUserUser.class, Restrictions.or(Restrictions.eq("user", this), Restrictions.eq("shared", true)));
		for (TrackerUserUser trackerUserUser : trackerUserUsers) {
			trackerUsers.add(trackerUserUser.getTrackerUser());
		}
		return trackerUsers;
	}

	public TrackerUser getTrackerUser(Tracker tracker) {
		return getTrackerUsers().stream().filter(p -> p.getTracker().getClass().equals(tracker.getClass())).findFirst().orElse(null);
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=[Protected], admin=" + admin + ", session=" + session + ", seedboxes=" + seedboxes + ", jobs=" + jobs + ", id=" + id + "]";
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
