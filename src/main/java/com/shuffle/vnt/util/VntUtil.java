package com.shuffle.vnt.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reflections.Reflections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public abstract class VntUtil {
	private VntUtil() {

	}

	private static Reflections reflections = null;

	public static Reflections fetchClasses() {
		return fetchClasses(false);
	}

	public static Reflections fetchClasses(boolean forceReload) {
		if (reflections == null || forceReload) {
			reflections = new Reflections("com.shuffle");
		}
		return reflections;
	}

	public static String getDomain(String url) {
		try {
			return new URI(url).getHost();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	private static ObjectMapper objectMapper;

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm"));
		}
		return objectMapper;
	}

	public static String toJson(Object object) {
		String jsonString = "";
		try {
			jsonString = getObjectMapper().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return jsonString;
	}

	public static <E extends Object> E fromJson(String json, Class<E> clazz) {
		try {
			return getObjectMapper().readValue(json, clazz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
