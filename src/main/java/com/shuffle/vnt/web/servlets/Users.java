package com.shuffle.vnt.web.servlets;

import com.shuffle.vnt.web.WebServer.SecurityFilter;
import com.shuffle.vnt.web.model.User;

@SecurityFilter(admin = true)
public class Users extends GenericCRUDController<User> {
	
}
