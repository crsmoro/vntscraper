package com.shuffle.vnt.core.parser;

import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.parser.VntTrackerManager;

public abstract class TrackerManagerFactory {

	private TrackerManagerFactory() {

	}

	public static TrackerManager getInstance(Tracker tracker) {
		return getInstance(new VntTrackerManager(), tracker);
	}

	public static TrackerManager getInstance(Class<? extends Tracker> tracker) {
		return getInstance(VntTrackerManager.class, tracker);
	}

	public static TrackerManager getInstance(TrackerManager trackerManager, Tracker tracker) {
		trackerManager.setTrackerConfig(tracker);
		return trackerManager;
	}

	public static TrackerManager getInstance(Class<? extends TrackerManager> trackerManager, Class<? extends Tracker> tracker) {
		TrackerManager trackerManagerInstance = null;
		Tracker trackerInstance = null;
		try {
			trackerManagerInstance = trackerManager.newInstance();
			trackerInstance = tracker.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return getInstance(trackerManagerInstance, trackerInstance);
	}
}
