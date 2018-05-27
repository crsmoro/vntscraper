package com.shuffle.vnt.core.service;

import com.shuffle.vnt.core.VntContext;

public interface ServiceFactory {

	public static Service getInstance(Class<? extends Service> service) { 
		return VntContext.getServiceParsers().stream().filter(s -> !s.isInterface()).findFirst().map(s -> {
			try {
				return s.newInstance();	
			}
			catch (InstantiationException | IllegalAccessException e) {
				return null;				
			}
		}).filter(s -> s != null).orElse(null);
	}
}
