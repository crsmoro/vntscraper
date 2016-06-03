package com.shuffle.vnt.services.schedule;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.MailConfig;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.Torrent;
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

	List<SchedulerData> schedulerDatas = PreferenceManager.getInstance().getPreferences().getSchedulerDatas();
	for (final SchedulerData schedulerData : schedulerDatas) {

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

    private Map<String, Object> ClazzToObject(Object object) {
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

    private String mountHtmlMail(SchedulerData schedulerData, List<Torrent> torrents) {
	List<Object> torrentTpls = new ArrayList<>();
	for (Torrent torrent : torrents) {
	    Map<String, Object> torrentObject = ClazzToObject(torrent);
	    torrent.setContent("");
	    try {
		torrentObject.put("stringify", URLEncoder.encode(VntUtil.getGson().toJson(torrent), "UTF-8"));
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }
	    torrentTpls.add(torrentObject);
	}

	String template = "";

	StringBuilder defaultTemplate = new StringBuilder();
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

	Map<String, Object> scopes = new HashMap<String, Object>();
	scopes.put("baseUrl", PreferenceManager.getInstance().getPreferences().getBaseUrl());
	scopes.put("schedulerData", schedulerData);
	scopes.put("torrents", torrentTpls);
	scopes.put("seedboxes", PreferenceManager.getInstance().getPreferences().getSeedboxes());
	log.debug("scopes");
	log.debug(scopes);

	if (schedulerData.getTemplate() != null && schedulerData.getTemplate().length > 0) {
	    log.info("Using template file");
	    try {
		template = new String(schedulerData.getTemplate(), "UTF-8");
	    } catch (UnsupportedEncodingException e) {
		log.error("Template file with incorrect encode", e);
	    }
	}
	return VntUtil.compileTemplate(template, scopes);
    }

    private void schedule(final SchedulerData schedulerData) {
	Date now = new Date();
	long initialDelay = 1;
	long diff = schedulerData.getNextRun().getTime() - now.getTime();
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
		    TrackerManager trackerManager = TrackerManagerFactory.getInstance(VntUtil.getTrackerConfig(schedulerData.getTrackerUser().getTracker()).getClass());
		    trackerManager.setQueryParameters(schedulerData.getQueryParameters());
		    trackerManager.setTrackerUser(schedulerData.getTrackerUser());
		    if (schedulerData.getServiceParser() != null) {
			ServiceParser serviceParser = ServiceFactory.getInstance(schedulerData.getServiceParser());
			serviceParser.setQueryParameters(schedulerData.getQueryParameters());
			serviceParser.setTrackerUserData(schedulerData.getTrackerUser());
			torrents.addAll(serviceParser.fetch());
		    } else {
			torrents.addAll(trackerManager.fetchTorrents());
		    }

		    try {
			if (!torrents.isEmpty()) {
			    MailConfig mailConfig = PreferenceManager.getInstance().getPreferences().getMailConfig();
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
				email.setSubject("VNT Schedule - " + schedulerData.getName());
				email.setHtmlMsg(mountHtmlMail(schedulerData, torrents));
				email.addTo(schedulerData.getEmail().split(";"));
				email.send();
			    } else {
				log.error("MailConfig not set");
			    }
			}
		    } catch (EmailException e) {
			log.error("Problema ao enviar o email do serviço " + schedulerData, e);
		    }
		} catch (Exception e) {
		    log.error("Problem during schedule", e);
		}

		Date nextRun = new Date(schedulerData.getNextRun().getTime() + (schedulerData.getInterval() * 60 * 1000));
		schedulerData.setNextRun(nextRun);
		PreferenceManager.getInstance().getScheduleData(schedulerData.getName()).setNextRun(nextRun);
		PreferenceManager.getInstance().savePreferences();

		log.info("Finished schedule");

	    }
	}, initialDelay, schedulerData.getInterval(), TimeUnit.MINUTES);
    }
}
