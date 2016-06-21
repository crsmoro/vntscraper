package com.shuffle.vnt.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.web.model.Session;
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
		extensionsMimes.put("js", MIME_JS);
		extensionsMimes.put("css", MIME_CSS);
		extensionsMimes.put("png", MIME_PNG);
		extensionsMimes.put("xml", MIME_XML);
	}

	private Map<String, String> files = new HashMap<>();

	private User user;

	private Session session;

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
			//FIXME
			//Map<String, Object> restrictions = new HashMap<>();
			//restrictions.put("session", session.getCookies().read("session"));
			this.session = PersistenceManager.getDao(Session.class).eq("session", session.getCookies().read("session")).findOne();
			if (this.session != null) {
				user = this.session.getUser();
			}

			if (urlRequested == null || urlRequested.equals("") || urlRequested.equals("/")) {
				urlRequested = "";
				if (!urlRequested.endsWith("/")) {
					urlRequested += "/";
				}
				urlRequested += "index.html";
			}
			extension = urlRequested.split("\\.")[urlRequested.split("\\.").length - 1];
			if (this.session == null && !urlRequested.endsWith("Login.vnt") && !urlRequested.endsWith("UploadTorrentToSeedbox.vnt") && !urlRequested.endsWith("DownloadTorrent.vnt") && !urlRequested.contains("css/")
					&& !urlRequested.contains("js/") && !urlRequested.contains("less/") && !urlRequested.contains("fonts/")) {
				return new Response(Response.Status.OK, MIME_HTML, getClass().getProtectionDomain().getClassLoader().getResourceAsStream(docBase + "/login.html"));
			}

			if (extension.equals("vnt")) {
				if (this.session != null) {
					this.session.setLastRequest(new Date());
					this.session.setLastIP(session.getHeaders().get("remote-addr"));
					PersistenceManager.getDao(Session.class).save(this.session);
				}
				String servlet = urlRequested.replace("." + extension, "");
				Class<?> clazz = Class.forName((servletBase + servlet).replaceAll("/", "."));
				if (ClassUtils.isAssignable(clazz, HttpServlet.class, true)) {
					Class<? extends HttpServlet> servletClazz = clazz.asSubclass(HttpServlet.class);
					HttpServlet httpServlet = servletClazz.newInstance();
					httpServlet.setWebServer(this);
					Response response = new Response("");
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

			return new Response(Response.Status.OK, (extensionsMimes.get(extension) != null ? extensionsMimes.get(extension) : MIME_DEFAULT_BINARY),
					getClass().getProtectionDomain().getClassLoader().getResourceAsStream(docBase + urlRequested));

		} catch (Exception e) {
			log.error("Not found error", e);
			return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
		}
	}

	@Override
	public Map<String, List<String>> decodeParameters(String queryString) {
		return super.decodeParameters(queryString);
	}

	public Map<String, String> getFiles() {
		return files;
	}

	public User getUser() {
		return user;
	}

	public Session getSession() {
		return session;
	}

}
