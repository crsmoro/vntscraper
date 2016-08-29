package com.shuffle.vnt.web.servlets;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.ServiceFactory;
import com.shuffle.vnt.core.service.TorrentManager;
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
		String torrentString = null;
		try {
			torrentString = URLDecoder.decode(session.getParms().get("torrent"), "UTF-8");
			torrentString = session.getParms().get("torrent");
			log.info(torrentString);
		}
		catch (UnsupportedEncodingException e) {
			log.error("Error decoding torrent param", e);
		}
		Torrent torrent = VntUtil.fromJson(torrentString, Torrent.class);
		InputStream inputStream = null;
		if (torrent != null) {
			TorrentManager torrentManager = ServiceFactory.getInstance(TorrentManager.class);
			inputStream = torrentManager.downloadTorrent(torrent);
			response.addHeader("Content-Disposition", "attachment; filename=\"" + torrent.getName() + ".torrent\"");
			response.setMimeType("application/octet-stream; charset=UTF-8");
			response.setChunkedTransfer(true);
		}

		response.setData(inputStream);
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
