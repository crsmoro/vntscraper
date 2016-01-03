package com.shuffle.vnt.core.service;

import java.io.InputStream;

import com.shuffle.vnt.configuration.bean.Seedbox;
import com.shuffle.vnt.core.parser.bean.Torrent;

public interface TorrentManager extends Service {
	
	InputStream downloadTorrent(Torrent torrent);
	
	InputStream downloadTorrent(Torrent torrent, String username);
	
	boolean sendToSeedbox(Seedbox seedboxConfig, Torrent torrent);
	
	boolean sendToSeedbox(Seedbox seedboxConfig, InputStream torrent);
}
