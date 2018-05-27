package com.shuffle.vnt.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.enumeration.ExternalSource;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.shuffle.sieve.core.parser.Tracker;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.vnt.api.bean.Movie;
import com.shuffle.vnt.api.omdb.OmdbAPI;
import com.shuffle.vnt.api.themoviedb.TheMovieDbApi;
import com.shuffle.vnt.core.configuration.PreferenceManager;
import com.shuffle.vnt.core.configuration.model.TmdbLanguage;

public abstract class VntUtil {

	private transient static Log log = LogFactory.getLog(VntUtil.class);

	private VntUtil() {

	}

	public static String getDomain(String url) {
		try {
			return new URI(url).getHost();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getHomeUrl(String url) {
		try {
			URI uri = new URI(url);
			String homeUrl = uri.getScheme() + "://" + uri.getHost() + (uri.getPort() >= 0 ? ":" + uri.getPort() : "");
			return homeUrl;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getCleanEncodedUrlValue(String value) {
		String encodedValue = "";
		try {
			encodedValue = URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			log.error("Problem enconding value to url");
		}
		return encodedValue;
	}

	private static ObjectMapper objectMapper;

	public static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.findAndRegisterModules();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.setDateFormat(new SimpleDateFormat("dd/MM/yyyy HH:mm"));
			SimpleModule module = new SimpleModule("module", new Version(1, 0, 0, null, "com.shuffle", "vnt"));
			module.addDeserializer(Tracker.class, new JsonDeserializer<Tracker>() {

				@Override
				public Tracker deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
					JsonNode jsonNode = p.getCodec().readTree(p);
					return Tracker.getInstance(jsonNode.get("name").asText());
				}

			});
			module.addSerializer(Tracker.class, new JsonSerializer<Tracker>() {

				@Override
				public void serialize(Tracker value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
					gen.writeStartObject();
					gen.writeStringField("name", value.getName());
					gen.writeEndObject();
				}

			});
			objectMapper.registerModule(module);
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

	public static <E> E fromJson(String json, Class<E> clazz) {
		try {
			return getObjectMapper().readValue(json, clazz);
		} catch (IOException e) {
			log.error("Error decoding json string", e);
		}
		return null;
	}

	public static InputStream getInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
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

	public static Map<String, Object> clazzToObject(Object object) {
		Map<String, Object> objectAsMap = new HashMap<String, Object>();
		try {
			BeanInfo info = Introspector.getBeanInfo(object.getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				if (!pd.getName().equals("class")) {
					Method reader = pd.getReadMethod();
					if (reader != null)
						objectAsMap.put(pd.getName(), reader.invoke(object));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			log.error("Error converting Object to Map", e);
		}

		return objectAsMap;
	}

	public static Movie getMovie(Torrent torrent) {
		if (StringUtils.isNotBlank(torrent.getImdbLink())) {
			return getMovie(torrent.getImdbLink());
		}
		return null;
	}

	public static Movie getMovie(String imdbLink) {
		if (StringUtils.isNotBlank(imdbLink)) {
			Movie movie = OmdbAPI.getMovie(OmdbAPI.getById(VntUtil.getImdbId(imdbLink)));
			log.debug("movie " + movie);

			MovieBasic movieBasic = null;
			Iterator<TmdbLanguage> languagesIterator = PreferenceManager.getPreferences().getTmdbLanguages().stream().sorted((t1, t2) -> t1.getOrder().compareTo(t2.getOrder())).iterator();
			while (movieBasic == null && languagesIterator.hasNext()) {
				try {
					movieBasic = TheMovieDbApi.getInstance().find(VntUtil.getImdbId(imdbLink), ExternalSource.IMDB_ID, languagesIterator.next().getLanguage()).getMovieResults().stream().findFirst().orElse(null);
				} catch (MovieDbException e) {

				}
			}
			log.debug("movieBasic " + movieBasic);
			if (movieBasic != null) {
				movie = TheMovieDbApi.getMovie(movieBasic, movie);
			}
			log.debug("put movie " + movie);
			return movie;
		}
		return null;
	}
}
