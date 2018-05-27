package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.web.WebServer.SecurityFilter;

@SecurityFilter(admin = true)
public class Preferences extends GenericCRUDController<com.shuffle.vnt.core.configuration.model.Preferences> {

}
