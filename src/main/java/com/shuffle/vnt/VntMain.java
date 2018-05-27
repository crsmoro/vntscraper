package com.shuffle.vnt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.h2.tools.Server;
import org.mindrot.jbcrypt.BCrypt;

import com.shuffle.vnt.core.VntContext;
import com.shuffle.vnt.core.configuration.model.Preferences;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.schedule.ScheduleManager;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.model.User;

import fi.iki.elonen.NanoHTTPD;

public class VntMain {

	private static final Log log = LogFactory.getLog(VntMain.class);
	
	public static void main(String[] args) {
		
		Date start = new Date();
		
		initLogger();
		
		Logger.getRootLogger().setLevel(Level.toLevel(System.getProperty("log", "WARN")));
		
		// Logger.getLogger("com.shuffle").setLevel(Level.DEBUG);
		// Logger.getLogger("com.shuffle.vnt.httprequest").setLevel(Level.TRACE);
		
		log.info("Starting...");

		// FIXME check better solution
		VntContext.fetchClasses();

		if (Boolean.getBoolean("db.webadmin")) {
			try {
				Server.createWebServer("-webAllowOthers").start();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		if (PersistenceManager.getDao(User.class).findAll().isEmpty()) {
			User user = new User();
			user.setUsername("adm");
			user.setPassword(BCrypt.hashpw("adm", BCrypt.gensalt()));
			user.setAdmin(true);
			PersistenceManager.getDao(User.class).save(user);
			Preferences preferences = new Preferences();
			preferences.setTokenKey(RandomStringUtils.randomAlphanumeric(20));
			preferences.setPasswordKey(RandomStringUtils.randomAlphanumeric(20));
			preferences.setRefreshTokenKey(RandomStringUtils.randomAlphanumeric(20));
			PersistenceManager.getDao(Preferences.class).save(preferences);
		}

		final WebServer webServer = new WebServer(Integer.getInteger("web.port", 7337));
		try {
			
			webServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ScheduleManager.getInstance().updateSchedules();

		Date finish = new Date();

		log.info("Finished startup");
		log.info("Took " + (finish.getTime() - start.getTime()) + " ms");
	}

	private static void initLogger() {
		Logger rootLogger = Logger.getRootLogger();
		PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");

		try {
			RollingFileAppender fileAppender = new RollingFileAppender(layout, System.getProperty("log.file", "./vnt.log"));
			fileAppender.setImmediateFlush(true);
			fileAppender.setThreshold(Level.DEBUG);
			fileAppender.setAppend(true);
			fileAppender.setMaxFileSize("5MB");
			fileAppender.setMaxBackupIndex(2);

			rootLogger.addAppender(fileAppender);
		} catch (IOException e) {
			log.error("Failed to add appender !!", e);
		}
	}
}
