package com.shuffle.vnt.service.parser.fetchnew;

import com.shuffle.vnt.core.service.ServiceParser;

public interface FetchNew extends ServiceParser {

	long getLast();

	void setLast(long last);
}