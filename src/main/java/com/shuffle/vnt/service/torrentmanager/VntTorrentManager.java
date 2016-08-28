package com.shuffle.vnt.service.torrentmanager;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.TorrentManager;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.core.service.WebClient;
import com.shuffle.vnt.core.service.WebClient.AuthenticationType;

public class VntTorrentManager implements TorrentManager {

	private static final Log log = LogFactory.getLog(VntTorrentManager.class);

	@Override
	public InputStream downloadTorrent(Torrent torrent) {
		if (torrent.getDownloadLink() == null || torrent.getDownloadLink().equals("")) {
			throw new IllegalArgumentException("Download link not set");
		}
		TrackerManager trackerManager = TrackerManagerFactory.getInstance(torrent.getTracker());
		trackerManager.setUser(torrent.getUsername(), torrent.getPassword());
		return trackerManager.download(torrent);
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
				credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(seedboxConfig.getUsername(), seedboxConfig.getPassword()));
			}
			if (credsProvider == null) {
				throw new UnsupportedOperationException("Authentication type not implemented");
			}
			CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
			try {
				HttpPost httppost = new HttpPost(seedboxConfig.getUrl() + (seedboxConfig.getUrl().charAt(seedboxConfig.getUrl().length() - 1) == '/' ? "" : "/") + webClient.addUrl());
				log.info(httppost.getURI());
				MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

				multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				multipartEntityBuilder.addPart(webClient.labelField(), new StringBody(seedboxConfig.getLabel(), ContentType.TEXT_PLAIN));
				multipartEntityBuilder.addBinaryBody(webClient.torrentField(), IOUtils.toByteArray(torrent), ContentType.APPLICATION_OCTET_STREAM, "vnttorrent.torrent");

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
