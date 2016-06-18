package com.shuffle.vnt.core.service;

import java.io.InputStream;

import com.shuffle.vnt.core.model.Seedbox;
import com.shuffle.vnt.core.parser.bean.Torrent;

public interface TorrentManager extends Service {
	
	InputStream downloadTorrent(Torrent torrent);
	
	boolean sendToSeedbox(Seedbox seedboxConfig, Torrent torrent);
	
	boolean sendToSeedbox(Seedbox seedboxConfig, InputStream torrent);
}
