package com.shuffle.vnt.core.service;

import com.shuffle.vnt.core.VntContext;

public abstract class ServiceFactory {

	private ServiceFactory() {

	}

	public static <S extends Service> S getInstance(Class<S> service) {
		Class<? extends S> serviceClass = null;
		for (Class<? extends S> serviceItem : VntContext.fetchClasses().getSubTypesOf(service)) {
			if (!serviceItem.isInterface()) {
				serviceClass = serviceItem;
				break;
			}
		}
		S returnService = null;
		try {
			returnService = serviceClass != null ? serviceClass.newInstance() : null;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return returnService;
	}
}
