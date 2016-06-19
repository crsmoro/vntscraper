package com.shuffle.vnt.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.criterion.Restrictions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

	@OneToMany(orphanRemoval = true, mappedBy = "user", targetEntity = Session.class)
	@JsonManagedReference
	private List<Session> sessions = new ArrayList<>();

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Seedbox.class)
	@JoinTable(inverseJoinColumns = { @JoinColumn(name = "seedbox_id") }, joinColumns = { @JoinColumn(name = "user_id") }, name = "user_seedboxes")
	@JsonIgnore
	private Set<Seedbox> seedboxes = new HashSet<>();

	@ManyToMany(cascade = CascadeType.ALL, targetEntity = Job.class)
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

	public List<Session> getSessions() {
		return sessions.stream().sorted(Comparator.comparing(Session::getLastRequest).reversed()).collect(Collectors.toList());
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
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

	@Transient
	@JsonIgnore
	private Set<TrackerUser> trackerUsers = new HashSet<>();

	public Set<TrackerUser> getTrackerUsers() {
		if (trackerUsers.isEmpty()) {
			List<TrackerUserUser> trackerUserUsers = PersistenceManager.findAll(TrackerUserUser.class, Restrictions.or(Restrictions.eq("user", this), Restrictions.eq("shared", true)));
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
