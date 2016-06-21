package com.shuffle.vnt.core.configuration;

import com.shuffle.vnt.core.configuration.model.Preferences;
import com.shuffle.vnt.core.db.PersistenceManager;

public abstract class PreferenceManager {
	private PreferenceManager() {

	}

	private static Preferences instance;

	static {
		reloadPreferences();
	}

	public static void reloadPreferences() {
		instance = PersistenceManager.getDao(Preferences.class).findOne();
		if (instance == null) {
			instance = new Preferences();
		}
	}

	public static Preferences getPreferences() {
		return instance;
	}

	public static void savePreferences() {
		PersistenceManager.getDao(Preferences.class).save(instance);
		reloadPreferences();
	}
}
