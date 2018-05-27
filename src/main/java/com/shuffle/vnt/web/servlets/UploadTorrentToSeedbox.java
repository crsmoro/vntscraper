package com.shuffle.vnt.web.servlets;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.shuffle.sieve.core.bittorrent.TorrentFile;
import com.shuffle.sieve.core.exception.SieveException;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.security.VntSecurity;
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
		Boolean close = Boolean.valueOf(Optional.ofNullable(session.getParameters().get("c")).orElse(Collections.emptyList()).stream().findFirst().orElse("false"));
		String seedbox = Optional.ofNullable(session.getParameters().get("seedbox")).orElse(Collections.emptyList()).stream().findFirst().orElse("");
		String torrentString = Optional.ofNullable(session.getParameters().get("torrent")).orElse(Collections.emptyList()).stream().findFirst().orElse(null);
		if (torrentString == null)
		{
			try {
				torrentString = URLDecoder.decode(VntSecurity.decrypt(new String(Hex.decodeHex(Optional.ofNullable(session.getParameters().get("chave")).orElse(Collections.emptyList()).stream().findFirst().orElse("").toCharArray()), StandardCharsets.UTF_8), VntSecurity.getTokenKey()), StandardCharsets.UTF_8.name());
			} catch (DecoderException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Torrent torrent = VntUtil.fromJson(torrentString, Torrent.class);
		if (StringUtils.isNotBlank(seedbox) && torrent != null) {
			TrackerManager trackerManager = TrackerManager.getInstance(torrent.getTracker(), torrent.getUsername(), torrent.getPassword());

			Seedbox seedboxConfig = PersistenceManager.getDao(Seedbox.class).findOne(Long.valueOf(seedbox));
			Wicker wicker = new WickerFactory(seedboxConfig.getWebClient()).newInstance(seedboxConfig.getUrl(), seedboxConfig.getUsername(), VntSecurity.decrypt(seedboxConfig.getPassword(), VntSecurity.getPasswordKey()));
			wicker.uploadTorrent(Optional.ofNullable(trackerManager.download(torrent)).map(TorrentFile::getInputStream).orElseThrow(() -> new SieveException("No torrent file found")), seedboxConfig.getLabel());
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
