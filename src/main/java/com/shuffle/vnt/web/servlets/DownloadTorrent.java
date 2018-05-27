package com.shuffle.vnt.web.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.sieve.core.bittorrent.TorrentFile;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class DownloadTorrent implements HttpServlet {

	private final static transient Log log = LogFactory.getLog(DownloadTorrent.class);

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		log.info("Starting");
		String torrentString = null;
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
			TrackerManager trackerManager = TrackerManager.getInstance(torrent.getTracker(), torrent.getUsername(), torrent.getPassword());
			TorrentFile torrentFile = trackerManager.download(torrent);
			response.addHeader("Content-Disposition", "attachment; filename=\"" + torrent.getName() + ".torrent\"");
			response.setMimeType("application/octet-stream; charset=UTF-8");
			response.setChunkedTransfer(true);
			try {
				torrentFile.getInputStream().reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
			response.setData(torrentFile.getInputStream());
		}
		log.info("Finished");
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
