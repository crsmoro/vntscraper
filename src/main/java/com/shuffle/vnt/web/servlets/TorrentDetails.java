package com.shuffle.vnt.web.servlets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class TorrentDetails implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		ReturnObject returnObject = null;
		String torrentString = Optional.ofNullable(session.getParameters().get("torrent")).orElse(Collections.emptyList()).stream().findFirst().orElse(null);
		if (torrentString == null) {
			try {
				torrentString = URLDecoder.decode(VntSecurity.decrypt(
						new String(Hex.decodeHex(Optional.ofNullable(session.getParameters().get("chave")).orElse(Collections.emptyList()).stream().findFirst().orElse("").toCharArray()), StandardCharsets.UTF_8),
						VntSecurity.getTokenKey()), StandardCharsets.UTF_8.name());
			} catch (DecoderException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Torrent torrent = VntUtil.fromJson(torrentString, Torrent.class);
		if (torrent != null) {
			if (!torrent.isDetailed()) {
				TrackerManager trackerManager = TrackerManager.getInstance(torrent.getTracker(), torrent.getUsername(), torrent.getPassword());
				try {
					trackerManager.getDetails(torrent);
					Map<String, Object> mapData = new HashMap<>();
					torrent.setPassword(VntSecurity.encrypt(torrent.getPassword(), VntSecurity.getPasswordKey()));
					mapData.put("torrent", torrent);
					mapData.put("movie", VntUtil.getMovie(torrent));

					returnObject = new ReturnObject(true, mapData);
				} catch (SieveException e) {
					returnObject = new ReturnObject(false, "Error when trying to get details", e.getStackTrace());
				}
			}

			response.setMimeType("application/json");
			response.setData(VntUtil.getInputStream(VntUtil.toJson(returnObject)));
		}
	}

	@Override
	public void doPost(IHTTPSession session, Response response) {

	}

	@Override
	public void doPut(IHTTPSession session, Response response) {

	}

	@Override
	public void doDelete(IHTTPSession session, Response response) {

	}

}
