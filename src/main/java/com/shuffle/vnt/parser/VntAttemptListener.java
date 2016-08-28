package com.shuffle.vnt.parser;

import org.apache.http.StatusLine;

public interface VntAttemptListener {

	void contentLoaded(String content);

	void loadFailed(StatusLine statusLine, String content);
}