package com.shuffle.vnt.services.torrentmanager;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.shuffle.vnt.configuration.bean.Seedbox;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.TorrentManager;
import com.shuffle.vnt.services.torrentmanager.WebClient.AuthenticationType;
import com.shuffle.vnt.util.VntUtil;

public class VntTorrentManager implements TorrentManager {

	private static final Log log = LogFactory.getLog(VntTorrentManager.class);
	
	public InputStream downloadTorrent(Torrent torrent) {
	    return downloadTorrent(torrent, null);
	}

	@Override
	public InputStream downloadTorrent(Torrent torrent, String username) {
		if (torrent.getDownloadLink() == null || torrent.getDownloadLink().equals("")) {
			throw new IllegalArgumentException("Download link not set");
		}
		try {
			CookieStore cookieStore = new BasicCookieStore();

			for (Cookie cookie : VntUtil.getCookies(torrent.getTracker(), username)) {
				cookieStore.addCookie(cookie);
			}

			CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
			HttpGet httpGet = new HttpGet(torrent.getDownloadLink());
			CloseableHttpResponse response = httpClient.execute(httpGet);

			HttpEntity httpEntity = response.getEntity();
			return httpEntity.getContent();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean sendToSeedbox(Seedbox seedboxConfig, Torrent torrent) {
		return sendToSeedbox(seedboxConfig, downloadTorrent(torrent));
	}

	@Override
	public boolean sendToSeedbox(Seedbox seedboxConfig, InputStream torrent) {
		if (torrent == null) {
			throw new IllegalArgumentException("Invalid torrent");
		}
		if (seedboxConfig == null) {
			throw new IllegalArgumentException("Invalid seedbox");
		}
		WebClient webClient = null;
		try {
			webClient = seedboxConfig.getWebClient().newInstance();
		} catch (IllegalAccessException | InstantiationException e) {
			throw new IllegalArgumentException("Invalid webclient configuration");
		}
		try {
			CredentialsProvider credsProvider = null;
			if (webClient.getAuthenticationType().equals(AuthenticationType.BASIC)) {
				credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials(seedboxConfig.getUsername(), seedboxConfig.getPassword()));
			}
			if (credsProvider == null) {
				throw new UnsupportedOperationException("Authentication type not implemented");
			}
			CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			try {
				HttpPost httppost = new HttpPost(seedboxConfig.getUrl()
						+ (seedboxConfig.getUrl().charAt(seedboxConfig.getUrl().length() - 1) == '/' ? "" : "/")
						+ webClient.addUrl());
				log.info(httppost.getURI());
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

				multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				multipartEntityBuilder.addPart(webClient.labelField(),
						new StringBody(seedboxConfig.getLabel(), ContentType.TEXT_PLAIN));
				multipartEntityBuilder.addBinaryBody(webClient.torrentField(), IOUtils.toByteArray(torrent),
						ContentType.APPLICATION_OCTET_STREAM, "vnttorrent.torrent");

				HttpEntity reqEntity = multipartEntityBuilder.build();

				httppost.setEntity(reqEntity);

				log.debug("Executing request " + httppost.getRequestLine());
				CloseableHttpResponse response = httpclient.execute(httppost);
				try {
					log.debug("----------------------------------------");
					log.debug(response.getStatusLine());
					log.debug(EntityUtils.toString(response.getEntity()));
				} finally {
					response.close();
				}
			} finally {
				httpclient.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
