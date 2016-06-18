package com.shuffle.vnt;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.h2.tools.Server;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.schedule.ScheduleManager;
import com.shuffle.vnt.util.VntUtil;
import com.shuffle.vnt.web.WebServer;
import com.shuffle.vnt.web.model.User;

public class VntMain {

	private static final Log log = LogFactory.getLog(VntMain.class);

	public static void main(String[] args) {

		initLogger();

		Logger.getRootLogger().setLevel(Level.toLevel(System.getProperty("level", "WARN")));

		Logger.getLogger("com.shuffle").setLevel(Level.DEBUG);

		// FIXME check better solution
		VntUtil.fetchClasses();

		if (Boolean.getBoolean("db.webadmin")) {
			try {
				Server.createWebServer("-webAllowOthers").start();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		if (PersistenceManager.findAll(User.class).isEmpty()) {
			User user = new User();
			user.setUsername("adm");
			user.setPassword("adm");
			user.setAdmin(true);
			PersistenceManager.save(user);
		}

		final WebServer webServer = new WebServer(Integer.getInteger("web.port", 7337));

		Thread webServerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					webServer.start();
					// FIXME workaround to run as service on
					// linux
					while (!Thread.currentThread().isInterrupted()) {
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {

						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		webServerThread.start();

		ScheduleManager.getInstance().updateSchedules();
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
