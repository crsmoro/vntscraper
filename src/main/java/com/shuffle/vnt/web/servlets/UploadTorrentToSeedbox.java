package com.shuffle.vnt.web.servlets;

import org.apache.commons.lang3.StringUtils;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.HttpServlet;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.bean.ReturnObject;
import com.shuffle.wicker.Wicker;
import com.shuffle.wicker.WickerFactory;

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
			TrackerManager trackerManager = TrackerManagerFactory.getInstance(torrent.getTracker());
			trackerManager.setUser(torrent.getUsername(), torrent.getPassword());
			
			Seedbox seedboxConfig = PersistenceManager.getDao(Seedbox.class).findOne(Long.valueOf(seedbox));
			Wicker wicker = new WickerFactory(seedboxConfig.getWebClient().getSimpleName()).newInstance(seedboxConfig.getUrl(), seedboxConfig.getUsername(), seedboxConfig.getPassword());
			wicker.uploadTorrent(trackerManager.download(torrent), seedboxConfig.getLabel());
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
