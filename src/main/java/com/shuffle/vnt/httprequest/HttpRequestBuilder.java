package com.shuffle.vnt.httprequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;

import com.shuffle.vnt.core.exception.TimeoutException;
import com.shuffle.vnt.util.VntUtil;

public class HttpRequestBuilder implements Cloneable {

	private static final transient Log log = LogFactory.getLog(HttpRequestBuilder.class);

	private String url;

	private CredentialsProvider credentialsProvider;

	private String httpMethod = "GET";

	private CloseableHttpClient httpClient;

	private CloseableHttpResponse response;

	private List<NameValuePair> parameters = new ArrayList<>();

	private BasicCookieStore cookieStore;

	private HttpEntity httpEntity;

	private String httpEntityString;
	
	private byte[] httpEntityFile;
	
	private StatusLine statusLine;
	
	public HttpRequestBuilder() {
		cookieStore = new BasicCookieStore();
		parameters = new ArrayList<>();
	}

	public HttpRequestBuilder(String url) {
		this();
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public HttpRequestBuilder setUrl(String url) {
		this.url = url;
		return this;
	}

	public HttpRequestBuilder addCredentials(CredentialsProvider credentialsProvider, String user, String passowrd) {
		this.credentialsProvider = credentialsProvider;
		this.credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, passowrd));
		return this;
	}

	public HttpRequestBuilder addCredentials(String user, String passowrd) {
		addCredentials(new BasicCredentialsProvider(), user, passowrd);
		return this;
	}

	public HttpRequestBuilder setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
		return this;
	}

	public HttpRequestBuilder request() {
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCredentialsProvider(credentialsProvider).build();
		HttpUriRequest httpUriRequest = null;
		if (httpMethod.equals("GET")) {
			httpUriRequest = makeGETRequest();
		} else if (httpMethod.equals("POST")) {
			httpUriRequest = makePOSTRequest();
		}
		addDefaultHeaders(httpUriRequest);
		try {
			response = httpClient.execute(httpUriRequest);
			log.trace("Executing request " + httpUriRequest.getRequestLine());
			log.trace("----------------------------------------");
			statusLine = response.getStatusLine();
			log.trace(statusLine);
			httpEntity = response.getEntity();
			ContentType contentType = ContentType.get(httpEntity);
			httpEntityFile = IOUtils.toByteArray(httpEntity.getContent());
			if (contentType.getCharset() != null) {
				httpEntityString = new String(httpEntityFile, contentType.getCharset());
			}
			else if (contentType.getMimeType().equals(ContentType.TEXT_HTML.getMimeType())) {
				httpEntityString = new String(httpEntityFile);
			}
		} catch (IOException e) {
			if (e instanceof SocketTimeoutException) {
				TimeoutException exception = new TimeoutException(e.getMessage());
				exception.addSuppressed(e);
				throw exception;
			}
			else {
				throw new RuntimeException(e);
			}
			
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				log.warn("Problem closing http client", e);
			}
		}
		return this;
	}

	private void addDefaultHeaders(HttpUriRequest httpUriRequest) {
		httpUriRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
		httpUriRequest.addHeader("Accept-Encoding", "gzip");
	}

	public HttpRequestBuilder addParameter(String key, String value) {
		parameters.add(new BasicNameValuePair(key, value));
		return this;
	}

	public List<NameValuePair> getParameters() {
		return parameters;
	}

	public HttpRequestBuilder addCookie(String name, String value) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
		cookie.setPath("/");
		cookie.setDomain(VntUtil.getDomain(url));
		cookieStore.addCookie(cookie);
		return this;
	}
	
	public HttpRequestBuilder addCookie(Cookie cookie) {
		cookieStore.addCookie(cookie);
		return this;
	}
	
	public HttpRequestBuilder addCookies(Collection<Cookie> cookies) {
		cookies.forEach(this::addCookie);
		return this;
	}
	
	public HttpRequestBuilder addCookies(Cookie[] cookie) {
		cookieStore.addCookies(cookie);
		return this;
	}

	public List<Cookie> getCookies() {
		return cookieStore.getCookies();
	}

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	private HttpUriRequest makeGETRequest() {
		HttpGet httpGet = new HttpGet(this.url + URLEncodedUtils.format(getParameters(), "UTF-8"));
		return httpGet;
	}

	private HttpUriRequest makePOSTRequest() {
		HttpPost httpPost = new HttpPost(this.url);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(getParameters()));
		} catch (UnsupportedEncodingException e) {
			log.info("Encoding of parameters error", e);
		}
		return httpPost;

	}

	public HttpEntity getResponse() {
		return httpEntity;
	}

	public String getStringResponse() {
		return httpEntityString;
	}

	public byte[] getByteResponse() {
		return httpEntityFile;
	}

	public StatusLine getStatusLine() {
		return statusLine;
	}

	@Override
	public HttpRequestBuilder clone() throws CloneNotSupportedException {
		return (HttpRequestBuilder)super.clone();
	}
}