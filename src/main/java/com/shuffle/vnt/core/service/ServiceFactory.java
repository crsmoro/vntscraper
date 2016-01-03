package com.shuffle.vnt.core.service;

import java.util.Set;

import org.reflections.Reflections;

public abstract class ServiceFactory {
	
	private ServiceFactory()
	{
		
	}
	
	public static <S extends Service> S getInstance(Class<S> service)
	{
		Reflections reflections = new Reflections("com.shuffle");
		Set<Class<? extends S>> services = reflections.getSubTypesOf(service);
		Class<? extends S> serviceClass = null;
		for (Class<? extends S> serviceItem : services)
		{
			if (!serviceItem.isInterface())
			{
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
