package com.shuffle.vnt.core;

import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.service.ServiceParser;

public class VntContext {
	private VntContext() {

	}

	private static Reflections reflections = null;

	private static List<Tracker> trackers = new ArrayList<>();

	private static List<Class<? extends ServiceParser>> serviceParsers = new ArrayList<>();

	static {
		fetchClasses();
		loadTrackers();
	}

	public static Reflections fetchClasses() {
		return fetchClasses(false);
	}

	public static Reflections fetchClasses(boolean forceReload) {
		if (reflections == null || forceReload) {
			reflections = new Reflections("com.shuffle");
		}
		return reflections;
	}

	public static void loadTrackers() {
		trackers.clear();
		for (Class<? extends Tracker> trackerClass : fetchClasses().getSubTypesOf(Tracker.class)) {
			if (!trackerClass.isInterface()) {
				Tracker tracker = Tracker.getInstance(trackerClass);
				trackers.add(tracker);
			}

		}
	}

	public static List<Tracker> getTrackers() {
		return getTrackers(false);
	}

	public static List<Tracker> getTrackers(boolean forceReload) {
		if (trackers.isEmpty() || forceReload) {
			loadTrackers();
		}
		return trackers;
	}

	public static void loadServiceParsers() {
		serviceParsers.clear();
		for (Class<? extends ServiceParser> serviceParserClass : fetchClasses().getSubTypesOf(ServiceParser.class)) {
			if (serviceParserClass.isInterface()) {
				serviceParsers.add(serviceParserClass);
			}
		}
	}

	public static List<Class<? extends ServiceParser>> getServiceParsers() {
		return getServiceParsers(false);
	}

	public static List<Class<? extends ServiceParser>> getServiceParsers(boolean forceReload) {
		if (serviceParsers.isEmpty() || forceReload) {
			loadServiceParsers();
		}
		return serviceParsers;
	}
}
