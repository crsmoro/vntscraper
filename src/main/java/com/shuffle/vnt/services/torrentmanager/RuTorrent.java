package com.shuffle.vnt.services.torrentmanager;

public class RuTorrent implements WebClient {

    private final String addUrl = "php/addtorrent.php";

    private final String labelField = "label";

    private final String torrentField = "torrent_file";

    @Override
    public String addUrl() {
	return addUrl;
    }

    @Override
    public String labelField() {
	return labelField;
    }

    @Override
    public String torrentField() {
	return torrentField;
    }

    @Override
    public AuthenticationType getAuthenticationType() {
	return AuthenticationType.BASIC;
    }

}
