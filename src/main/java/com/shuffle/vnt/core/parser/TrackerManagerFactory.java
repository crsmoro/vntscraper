package com.shuffle.vnt.core.parser;

import java.util.HashMap;
import java.util.Map;

import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.parser.VntTrackerManager;

public abstract class TrackerManagerFactory {
	
	private static Map<Tracker, TrackerManager> trackerInstance = new HashMap<>();

	private TrackerManagerFactory() {

	}

	public static TrackerManager getInstance(Tracker tracker) {
		TrackerManager trackerManager = trackerInstance.get(tracker);
		if (trackerManager == null) {
			trackerManager = new VntTrackerManager();
			trackerInstance.put(tracker, trackerManager);
		}
		return getInstance(trackerManager, tracker);
	}

	public static TrackerManager getInstance(Class<? extends Tracker> tracker) {
		return getInstance(VntTrackerManager.class, tracker);
	}

	public static TrackerManager getInstance(TrackerManager trackerManager, Tracker tracker) {
		trackerManager.setTracker(tracker);
		trackerManager.setCaptcha(null);
		return trackerManager;
	}

	public static TrackerManager getInstance(Class<? extends TrackerManager> trackerManager, Class<? extends Tracker> tracker) {
		TrackerManager trackerManagerInstance = null;
		Tracker trackerInstance = null;
		try {
			
			trackerInstance = tracker.newInstance();
			trackerManagerInstance = TrackerManagerFactory.trackerInstance.get(trackerInstance);
			if (trackerManagerInstance == null) {
				trackerManagerInstance = trackerManager.newInstance();
				TrackerManagerFactory.trackerInstance.put(trackerInstance, trackerManagerInstance);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return getInstance(trackerManagerInstance, trackerInstance);
	}
}
