package com.shuffle.vnt.web.servlets;

import java.io.InputStream;

import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.ServiceFactory;
import com.shuffle.vnt.core.service.TorrentManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class DownloadTorrent implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		Torrent torrent = VntUtil.fromJson(session.getParms().get("torrent"), Torrent.class);
		InputStream inputStream = null;
		if (torrent != null) {
			TorrentManager torrentManager = ServiceFactory.getInstance(TorrentManager.class);
			inputStream = torrentManager.downloadTorrent(torrent);
		}

		response.addHeader("Content-Disposition", "attachment; filename=\"" + torrent.getName() + ".torrent\"");
		response.setMimeType("application/octet-stream; charset=UTF-8");
		response.setChunkedTransfer(true);
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
