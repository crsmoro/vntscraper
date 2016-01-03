package com.shuffle.vnt.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.configuration.bean.FetchNew;
import com.shuffle.vnt.configuration.bean.Preferences;
import com.shuffle.vnt.configuration.bean.Seedbox;
import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.services.schedule.SchedulerData;

public class PreferenceManager {

	private static final Log log = LogFactory.getLog(PreferenceManager.class);

	private Preferences preferences;

	private File preferencesFile = new File("./vnt.conf");

	private static PreferenceManager instance;

	private PreferenceManager() {
		preferences = readConfiguration();
	}

	private Preferences readConfiguration() {
		try {
			if (!preferencesFile.exists()) {
				preferencesFile.createNewFile();
			}
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(preferencesFile));
			Preferences pref = (Preferences) objectInputStream.readObject();
			objectInputStream.close();
			return pref;
		} catch (IOException | ClassNotFoundException e) {
			log.warn("Configuration file not found, creating new one", e);
			return new Preferences();
		}
	}

	private boolean saveConfiguration() {
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(preferencesFile));
			objectOutputStream.writeObject(preferences);
			objectOutputStream.flush();
			objectOutputStream.close();
			return true;
		} catch (IOException e) {
			log.error("Error while saving configuration", e);
			return false;
		}
	}

	public static PreferenceManager getInstance() {
		if (instance == null) {
			instance = new PreferenceManager();
		}
		return instance;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public boolean savePreferences() {
		return saveConfiguration();
	}

	public List<TrackerUser> getTrackerUsers(String tracker) {
		List<TrackerUser> trackerUsers = new ArrayList<>();
		for (TrackerUser trackerUserItem : preferences.getTrackerUsers()) {
			if (trackerUserItem.getTracker().equals(tracker)) {
				trackerUsers.add(trackerUserItem);
			}
		}
		return trackerUsers;
	}

	public TrackerUser getTrackerUser(String tracker) {
		return getTrackerUser(tracker, null);
	}

	public TrackerUser getTrackerUser(String tracker, String username) {
		for (TrackerUser trackerUserItem : preferences.getTrackerUsers()) {
			if (trackerUserItem.getTracker().equals(tracker)
					&& (username == null || trackerUserItem.getUsername().equals(username))) {
				return trackerUserItem;
			}
		}
		return new TrackerUser();
	}

	public FetchNew getFetchNew(TrackerUser trackerUser, QueryParameters queryParameters) {
		for (FetchNew fetchNew : preferences.getFetchNews()) {
			if (fetchNew.getTrackerUser().equals(trackerUser)
					&& fetchNew.getQueryParameters().equals(queryParameters)) {
				return fetchNew;
			}
		}
		return new FetchNew();
	}

	public SchedulerData getScheduleData(String name) {
		for (SchedulerData schedulerData : preferences.getSchedulerDatas()) {
			if (schedulerData.getName().equals(name)) {
				return schedulerData;
			}
		}
		return new SchedulerData();
	}

	public Seedbox getSeedbox(String name) {
		for (Seedbox seedbox : preferences.getSeedboxes()) {
			if (seedbox.getName().equals(name)) {
				return seedbox;
			}
		}
		return null;
	}
}
