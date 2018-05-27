package com.shuffle.vnt.web;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.GsonBuilder;
import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.vnt.core.exception.VntException;
import com.shuffle.vnt.core.security.SecurityContext;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.vnt.web.bean.Token;
import com.shuffle.vnt.web.model.User;

import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

	private static final transient Log log = LogFactory.getLog(WebServer.class);

	private static final String MIME_PLAINTEXT = "text/plain", MIME_HTML = "text/html", MIME_JS = "application/javascript", MIME_CSS = "text/css", MIME_PNG = "image/png", MIME_DEFAULT_BINARY = "application/octet-stream", MIME_XML = "text/xml";

	private static final Map<String, String> extensionsMimes = new HashMap<String, String>();

	static {
		extensionsMimes.put("txt", MIME_PLAINTEXT);
		extensionsMimes.put("htm", MIME_HTML);
		extensionsMimes.put("html", MIME_HTML);
		extensionsMimes.put("", MIME_HTML);
		extensionsMimes.put("js", MIME_JS);
		extensionsMimes.put("css", MIME_CSS);
		extensionsMimes.put("png", MIME_PNG);
		extensionsMimes.put("xml", MIME_XML);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(value = ElementType.TYPE)
	public @interface SecurityFilter {
		boolean admin() default false;
	}

	private Map<String, String> files = new HashMap<>();

	public WebServer(int port) {
		super(port);
	}

	@Override
	public Response serve(IHTTPSession session) {
		try {
			String docBase = "com/shuffle/vnt/web/webapp";
			String servletBase = "com/shuffle/vnt/web/servlets";
			String urlRequested = session.getUri();
			String extension = "";

			String token = null;
			if (StringUtils.isNotBlank(session.getCookies().read("token"))) {
				token = session.getCookies().read("token");
			} else if (StringUtils.isNotBlank(session.getHeaders().get("token"))) {
				token = session.getHeaders().get("token");
			} else if (StringUtils.isNotBlank(Optional.ofNullable(session.getParameters().get("token")).orElse(Collections.emptyList()).stream().findFirst().orElse(""))) {
				token = session.getParameters().get("token").stream().findFirst().get();
			}

			if (token != null && VntSecurity.getTokenKey() != null) {
				String decryptedToken = VntSecurity.decrypt(token, VntSecurity.getTokenKey());
				if (decryptedToken != null) {
					SecurityContext.setToken(VntUtil.fromJson(decryptedToken, Token.class));					
				}
			}

			if (SecurityContext.getToken() != null) {
				SecurityContext.setUser(getToken().getUser());
			}

			if (urlRequested != null && urlRequested.endsWith(".vnt")) {
				String servlet = urlRequested.replace(".vnt", "");
				Class<?> clazz = Class.forName((servletBase + servlet).replaceAll("/", "."));
				if (ClassUtils.isAssignable(clazz, HttpServlet.class, true)) {
					Class<? extends HttpServlet> servletClazz = clazz.asSubclass(HttpServlet.class);
					SecurityFilter securityFilter = servletClazz.getDeclaredAnnotation(SecurityFilter.class);
					if (SecurityContext.getToken() == null && securityFilter != null) {
						return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_HTML, "You need to login");
					} else if (SecurityContext.getToken() != null && securityFilter != null && securityFilter.admin() && !getToken().getUser().isAdmin()) {
						return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_HTML, "You need to be an admin");
					}
					HttpServlet httpServlet = servletClazz.newInstance();
					httpServlet.setWebServer(this);
					Response response = newFixedLengthResponse("");
					files = new HashMap<>();
					if (session.getMethod().equals(Method.GET)) {
						httpServlet.doGet(session, response);
					} else if (session.getMethod().equals(Method.POST)) {
						session.parseBody(files);
						httpServlet.doPost(session, response);
					} else if (session.getMethod().equals(Method.DELETE)) {
						session.parseBody(files);
						httpServlet.doDelete(session, response);
					} else if (session.getMethod().equals(Method.PUT)) {
						session.parseBody(files);
						httpServlet.doPut(session, response);
					}
					return response;
				}
			}
			if (StringUtils.isNotBlank(urlRequested) && urlRequested.contains("favicon.ico")) {
				return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_DEFAULT_BINARY, "");
			}
			if (StringUtils.isBlank(urlRequested) || urlRequested.equals("/")) {
				urlRequested = "/index.html";
			}
			InputStream requestInputStream = getClass().getProtectionDomain().getClassLoader().getResourceAsStream(docBase + urlRequested);
			if (requestInputStream == null) {
				urlRequested = "/index.html";
				requestInputStream = getClass().getProtectionDomain().getClassLoader().getResourceAsStream(docBase + urlRequested);
			}
			extension = urlRequested.split("\\.")[urlRequested.split("\\.").length - 1];
			return newFixedLengthResponse(Response.Status.OK, (extensionsMimes.get(extension) != null ? extensionsMimes.get(extension) : MIME_DEFAULT_BINARY), requestInputStream, requestInputStream.available());

		} catch (SieveException e) {
			log.error("Sieve error", e);
			return newFixedLengthResponse(Response.Status.OK, "application/json", buildVntError(e));
		} catch (VntException e) {
			log.error("Vnt error", e);
			return newFixedLengthResponse(Response.Status.OK, "application/json", buildVntError(e));
		} catch (Exception e) {
			log.error("Not found error", e);
			return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
		}
	}

	private String buildVntError(RuntimeException e) {
		return new GsonBuilder().create().toJson(new ReturnObject(false, e.getMessage(), e.getSuppressed().getClass()));
	}

	public Map<String, List<String>> decodeQueryParameters(String queryString) {
		return super.decodeParameters(queryString);
	}

	public Map<String, String> getFiles() {
		return files;
	}

	public User getUser() {
		return SecurityContext.getUser();
	}

	public Token getToken() {
		return SecurityContext.getToken();
	}

}
