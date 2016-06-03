package com.shuffle.vnt.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.reflections.Reflections;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.Cookie;
import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.TrackerConfig;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;

public abstract class VntUtil {
    private VntUtil() {

    }

    private static Reflections reflections = null;
    
    public static void fetchClasses() {
	reflections = new Reflections("com.shuffle");
    }

    public static boolean cookieExpired(String trackerName, String username) {
	TrackerUser trackerData = PreferenceManager.getInstance().getTrackerUser(trackerName, username);
	if (trackerData != null) {
	    Date now = new Date();
	    for (Cookie cookie : trackerData.getCookies()) {
		if (cookie.getExpiration() < now.getTime()) {
		    return true;
		}
	    }
	    return trackerData.getCookies().isEmpty();
	} else {
	    return true;
	}
    }

    public static boolean cookieExpired(String trackerName) {
	return cookieExpired(trackerName, null);
    }

    public static void updateCookies(String trackerName, String username, Map<String, String> cookies) {
	TrackerUser trackerData = PreferenceManager.getInstance().getTrackerUser(trackerName, username);
	if (trackerData != null) {
	    trackerData.getCookies().clear();
	    for (String cookieName : cookies.keySet()) {
		Cookie cookie = new Cookie();
		cookie.setName(cookieName);
		cookie.setValue(cookies.get(cookieName));
		cookie.setExpiration(new Date().getTime() + (72 * 60 * 60 * 1000));
		trackerData.getCookies().add(cookie);
	    }
	    PreferenceManager.getInstance().savePreferences();
	}
    }

    public static void updateCookies(String trackerName, Map<String, String> cookies) {
	updateCookies(trackerName, null, cookies);
    }

    public static List<org.apache.http.cookie.Cookie> getCookies(String trackerName, String username) {
	List<org.apache.http.cookie.Cookie> cookies = new ArrayList<>();
	TrackerUser trackerData = PreferenceManager.getInstance().getTrackerUser(trackerName, username);
	TrackerConfig trackerConfig = getTrackerConfig(trackerName);
	if (trackerData != null) {
	    for (Cookie cookieData : trackerData.getCookies()) {
		BasicClientCookie cookie = new BasicClientCookie(cookieData.getName(), cookieData.getValue());
		cookie.setPath("/");
		cookie.setDomain(getDomain(trackerConfig.getUrl()));
		cookies.add(cookie);
	    }
	}
	return cookies;
    }

    public static List<org.apache.http.cookie.Cookie> getCookies(String trackerName) {
	return getCookies(trackerName, null);
    }

    public static Map<String, String> getCookies(List<org.apache.http.cookie.Cookie> cookies) {
	Map<String, String> returnCookies = new HashMap<>();
	for (org.apache.http.cookie.Cookie cookie : cookies) {
	    returnCookies.put(cookie.getName(), cookie.getValue());
	}
	return returnCookies;
    }

    public static TrackerConfig getTrackerConfig(String trackerName) {
	if (reflections == null) {
	    fetchClasses();
	}
	Set<Class<? extends TrackerConfig>> trackerConfigs = reflections.getSubTypesOf(TrackerConfig.class);
	TrackerConfig returnTrackerConfig = null;
	for (Class<? extends TrackerConfig> trackerConfigItem : trackerConfigs) {

	    if (!trackerConfigItem.isInterface()) {
		try {
		    TrackerConfig trackerConfig = trackerConfigItem.newInstance();
		    if (trackerConfig.getName().equals(trackerName)) {
			returnTrackerConfig = trackerConfig;
			break;
		    }
		} catch (InstantiationException | IllegalAccessException e) {
		    e.printStackTrace();
		}
	    }
	}
	return returnTrackerConfig;
    }

    public static String getDomain(String url) {
	try {
	    return new URI(url).getHost();
	} catch (URISyntaxException e) {
	    e.printStackTrace();
	    return "";
	}
    }

    public static Gson getGson() {
	GsonBuilder gsonBuilder = new GsonBuilder();
	gsonBuilder.serializeNulls();
	gsonBuilder.setDateFormat("dd/MM/yyyy HH:mm");
	gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {

	    @Override
	    public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(JsonIgnore.class) != null;
	    }

	    @Override
	    public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	    }
	});
	gsonBuilder.registerTypeAdapter(Class.class, new JsonSerializer<Class<?>>() {

	    @Override
	    public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.getName());
	    }
	});
	return gsonBuilder.create();
    }

    public static InputStream getInputStream(String content) {
	return new ByteArrayInputStream(content.getBytes());
    }

    private final static long KB_FACTOR = 1024;
    private final static long MB_FACTOR = 1024 * KB_FACTOR;
    private final static long GB_FACTOR = 1024 * MB_FACTOR;

    public static double parseSize(String size) {
	int spaceNdx = size.indexOf(" ");
	double ret = Double.parseDouble(size.substring(0, spaceNdx));
	switch (size.substring(spaceNdx + 1)) {
	case "GB":
	    return ret * GB_FACTOR;
	case "MB":
	    return ret * MB_FACTOR;
	case "KB":
	    return ret * KB_FACTOR;
	}
	return -1;
    }

    public static TrackerCategory getTrackerCategory(String trackerName, String code) {
	TrackerConfig trackerConfig = getTrackerConfig(trackerName);
	for (TrackerCategory trackerCategory : trackerConfig.getCategories()) {
	    if (trackerCategory.getCode().equals(code)) {
		return trackerCategory;
	    }
	}
	return null;
    }

    public static String compileTemplate(String template, Map<String, Object> scopes) {
	StringWriter writer = new StringWriter();
	MustacheFactory mf = new DefaultMustacheFactory();
	Mustache mustache = mf.compile(new StringReader(template), "genericTemplate");
	mustache.execute(writer, scopes);
	writer.flush();

	StringBuilder compiledTemplate = new StringBuilder();
	compiledTemplate.append(writer.getBuffer());
	return compiledTemplate.toString();
    }
    
    public static String getImdbId(String url) {
	Pattern pattern = Pattern.compile("\\/(tt(\\d+))", Pattern.CASE_INSENSITIVE);
	Matcher matcher = pattern.matcher(url);
	if (matcher.find() && matcher.groupCount() > 1) {
	    return matcher.group(1);
	}
	return "";
    }
}
