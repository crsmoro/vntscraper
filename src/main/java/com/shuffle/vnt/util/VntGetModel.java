package com.shuffle.vnt.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;

import com.shuffle.vnt.core.configuration.model.Preferences;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.model.TrackerUser;

public abstract class VntGetModel {
	private VntGetModel() {

	}

	public static TrackerUser getTrackerUser(String trackerName) {
		return getTrackerUser(trackerName, null);
	}

	public static TrackerUser getTrackerUser(String trackerName, String username) {
		List<TrackerUser> trackerUsers = getTrackerUsers(trackerName, username);
		return trackerUsers != null && !trackerUsers.isEmpty() ? trackerUsers.get(0) : null;
	}

	public static List<TrackerUser> getTrackerUsers(String trackerName) {
		return getTrackerUsers(trackerName, null);
	}

	private static List<TrackerUser> getTrackerUsers(String trackerName, String username) {
		Conjunction and = Restrictions.and();
		try {
			and.add(Restrictions.eq("trackerClass", Class.forName(trackerName)));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (StringUtils.isNotBlank(username)) {
			and.add(Restrictions.eq("username", username));
		}
		return PersistenceManager.findAll(TrackerUser.class, and);
	}

	public static Seedbox getSeedbox(String name) {
		return PersistenceManager.findOne(Seedbox.class, Restrictions.eq("name", name));
	}

	public static Preferences getPreferences() {
		return PersistenceManager.findOne(Preferences.class, Restrictions.and());
	}
}
