package com.shuffle.vnt.core.schedule;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.shuffle.vnt.core.configuration.PreferenceManager;
import com.shuffle.vnt.core.configuration.model.MailConfig;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.schedule.model.Job;
import com.shuffle.vnt.core.service.ServiceFactory;
import com.shuffle.vnt.core.service.ServiceParser;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;

public class ScheduleManager {

	private static ScheduleManager instance;

	private static final Log log = LogFactory.getLog(ScheduleManager.class);

	private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

	public static ScheduleManager getInstance() {
		if (instance == null) {
			instance = new ScheduleManager();
		}
		return instance;
	}

	public void updateSchedules() {

		List<Job> schedulerDatas = PersistenceManager.getDao(Job.class).findAll();
		for (final Job schedulerData : schedulerDatas) {

			schedule(schedulerData);

			log.debug("after schedular");
		}
	}

	public void clearSchedules() {
		log.debug("init clearSchedules");
		scheduledExecutorService.shutdownNow();
		log.debug("after shutdownNow");
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		log.debug("after newSingleThreadScheduledExecutor");
		log.debug("finish clearSchedules");
	}

	private Map<String, Object> clazzToObject(Object object) {
		Map<String, Object> objectAsMap = new HashMap<String, Object>();
		try {
			BeanInfo info = Introspector.getBeanInfo(object.getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				Method reader = pd.getReadMethod();
				if (reader != null)
					objectAsMap.put(pd.getName(), reader.invoke(object));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			log.error("Error converting Object to Map", e);
		}

		return objectAsMap;
	}

	private String mountHtmlMail(Job job, List<Torrent> torrents) {
		List<Object> torrentTpls = new ArrayList<>();
		for (Torrent torrent : torrents) {
			torrent.setContent("");
			Map<String, Object> torrentObject = clazzToObject(torrent);
			Torrent torrentLink = null;
			try {
				torrentLink = torrent.clone();
				torrentLink.setMovie(null);
			}
			catch (CloneNotSupportedException e)
			{
				torrentLink = torrent;
			}
			try {
				torrentObject.put("stringify", URLEncoder.encode(VntUtil.toJson(torrentLink), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			torrentTpls.add(torrentObject);
		}

		String template = "";

		InputStream defaultTemplateStream = getClass().getProtectionDomain().getClassLoader().getResourceAsStream("com/shuffle/vnt/web/webapp/template/defaultTemplate.html");
		StringBuilder defaultTemplate = new StringBuilder();
		try {
			defaultTemplate.append(IOUtils.toString(defaultTemplateStream));
		} catch (IOException e1) {
			log.error("Error getting default template file, using the hardcoded", e1);
			defaultTemplate.append("<br/>");
			defaultTemplate.append("<table>");
			defaultTemplate.append("<thead>");
			defaultTemplate.append("<tr>");
			defaultTemplate.append("<th style='font-family:Helvetica; font-size: 14px; font-weight: bold; text-align: left;'>Torrent</th>");
			defaultTemplate.append("<th style='font-family:Helvetica; font-size: 14px; font-weight: bold; text-align: left;'>Send to</th>");
			defaultTemplate.append("</tr>");
			defaultTemplate.append("</thead>");
			defaultTemplate.append("<tbody>");
			defaultTemplate.append("{{#torrents}}");
			defaultTemplate.append("<tr>");
			defaultTemplate.append("<td>");
			defaultTemplate.append("<a target='_blank' href='{{link}}' style='color: #337ab7; text-decoration:none;'>{{name}}</a>");
			defaultTemplate.append("</td>");
			defaultTemplate.append("<td>");
			defaultTemplate.append("{{#seedboxes}}");
			defaultTemplate.append("| <a target='_blank' href='{{& baseUrl}}/UploadTorrentToSeedbox.vnt?seedbox={{name}}&torrent={{stringify}}&username={{schedulerData.trackerUser.username}}&c=true'>{{name}}</a>");
			defaultTemplate.append("{{/seedboxes}}");
			defaultTemplate.append("</td>");
			defaultTemplate.append("</tr>");
			defaultTemplate.append("{{/torrents}}");
			defaultTemplate.append("</tbody>");
			defaultTemplate.append("</table>");
		}

		Map<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("baseUrl", PreferenceManager.getPreferences().getBaseUrl());
		scopes.put("job", job);
		scopes.put("torrents", torrentTpls);
		scopes.put("seedboxes", job.getSeedboxes());
		log.debug("scopes");
		log.debug(scopes);

		if (job.getTemplate() != null && job.getTemplate().length > 0) {
			log.info("Using template file");
			try {
				template = new String(job.getTemplate(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.error("Template file with incorrect encode", e);
			}
		} else {
			template = defaultTemplate.toString();
		}
		return VntUtil.compileTemplate(template, scopes);
	}

	private void schedule(final Job job) {
		Date now = new Date();
		long initialDelay = 1;
		long diff = job.getNextRun().getTime() - now.getTime();
		log.info("Difference between now and next run " + diff);
		if (diff >= 0) {
			initialDelay = (long) Math.ceil((diff / 1000) / 60);
		}
		log.info("Initial delay " + initialDelay);

		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				log.info("Starting schedule");

				try {
					List<Torrent> torrents = new ArrayList<>();
					TrackerManager trackerManager = TrackerManagerFactory.getInstance(job.getTrackerUser().getTracker());
					trackerManager.setQueryParameters(job.getQueryParameters());
					trackerManager.setTrackerUser(job.getTrackerUser());
					if (job.getServiceParser() != null) {
						ServiceParser serviceParser = ServiceFactory.getInstance(job.getServiceParser());
						serviceParser.setQueryParameters(job.getQueryParameters());
						serviceParser.setTrackerUserData(job.getTrackerUser());
						serviceParser.setData(job.getServiceParserData());
						torrents.addAll(serviceParser.fetch());
						job.setServiceParserData(serviceParser.getData());
					} else {
						torrents.addAll(trackerManager.fetchTorrents());
					}

					try {
						if (!torrents.isEmpty()) {
							MailConfig mailConfig = PreferenceManager.getPreferences().getMailConfig();
							if (mailConfig != null) {
								HtmlEmail email = new HtmlEmail();
								email.setHostName(mailConfig.getHostname());
								email.setSmtpPort(mailConfig.getPort());
								email.setAuthenticator(new DefaultAuthenticator(mailConfig.getUsername(), mailConfig.getPassword()));
								email.setSSLOnConnect(mailConfig.isSsl());
								if (mailConfig.isSsl()) {
									email.setSslSmtpPort(String.valueOf(mailConfig.getPort()));
								}
								email.setStartTLSEnabled(mailConfig.isTls());
								email.setFrom(mailConfig.getFrom(), mailConfig.getFromName());
								email.setSubject("VNT Schedule - " + job.getName());
								email.setHtmlMsg(mountHtmlMail(job, torrents));
								email.addTo(job.getEmail().split(";"));
								email.send();
							} else {
								log.error("MailConfig not set");
							}
						}
					} catch (EmailException e) {
						log.error("Problema ao enviar o email do serviço " + job, e);
					}
				} catch (Exception e) {
					log.error("Problem during schedule", e);
				}

				Date nextRun = new Date(job.getNextRun().getTime() + (job.getInterval() * 60 * 1000));
				job.setNextRun(nextRun);
				PersistenceManager.getDao(Job.class).save(job);

				log.info("Finished schedule");

			}
		}, initialDelay, job.getInterval(), TimeUnit.MINUTES);
	}
}
