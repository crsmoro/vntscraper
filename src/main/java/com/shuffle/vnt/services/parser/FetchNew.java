package com.shuffle.vnt.services.parser;

import com.shuffle.vnt.core.service.ServiceParser;

public interface FetchNew extends ServiceParser {

	long getLast();

	void setLast(long last);

}