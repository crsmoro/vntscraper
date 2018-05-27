package com.shuffle.vnt.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.reflections.Reflections;

import com.shuffle.vnt.core.service.Service;

public class VntContext {
	private VntContext() {

	}
	
	private static Reflections reflections = null;

	private static final List<Class<? extends Service>> serviceParsers = new ArrayList<>();

	static {
		fetchClasses();
		loadServiceParsers();
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

	private static void loadServiceParsers() {
		serviceParsers.clear();
		fetchClasses().getSubTypesOf(Service.class).stream().filter(s -> !s.isInterface()).forEach(serviceParsers::add);
	}

	public static List<Class<? extends Service>> getServiceParsers() {
		return getServiceParsers(false);
	}

	public static List<Class<? extends Service>> getServiceParsers(boolean forceReload) {
		if (serviceParsers.isEmpty() || forceReload) {
			loadServiceParsers();
		}
		return Collections.unmodifiableList(serviceParsers);
	}
}
