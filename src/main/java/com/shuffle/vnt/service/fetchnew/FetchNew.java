package com.shuffle.vnt.service.fetchnew;

import com.shuffle.vnt.core.service.Service;

public interface FetchNew extends Service {

	long getLast();

	void setLast(long last);
}