package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.ServiceFactory;
import com.shuffle.vnt.core.service.TorrentManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;

public class UploadTorrentToSeedbox implements HttpServlet {

	@Override
	public void setWebServer(WebServer webServer) {

	}

	@Override
	public void doGet(IHTTPSession session, Response response) {
		Boolean close = Boolean.valueOf(session.getParms().get("c"));
		String seedbox = session.getParms().get("seedbox");
		Torrent torrent = VntUtil.fromJson(session.getParms().get("torrent"), Torrent.class);
		if (StringUtils.isNotBlank(seedbox) && torrent != null) {
			TorrentManager torrentManager = ServiceFactory.getInstance(TorrentManager.class);
			torrentManager.sendToSeedbox(PersistenceManager.findOne(Seedbox.class, Long.valueOf(seedbox)), torrentManager.downloadTorrent(torrent));
		}
		if (close) {
			response.setData(VntUtil.getInputStream("<script type='text/javascript'>alert('Torrent sent with success.');window.close();</script>"));
		} else {
			response.setMimeType("application/json; charset=UTF-8");
			ReturnObject returnObject = new ReturnObject(true, null);
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
