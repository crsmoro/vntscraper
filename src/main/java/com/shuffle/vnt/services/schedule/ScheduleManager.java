package com.shuffle.vnt.services.schedule;

import java.io.UnsupportedEncodingException;
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

    //FIXME find a better way to do this
    private class TorrentTpl extends Torrent {
	public TorrentTpl(Torrent torrent) {
	    setAdded(torrent.getAdded());
	    setCategory(torrent.getCategory());
	    setDetailed(torrent.isDetailed());
	    setDownloadLink(torrent.getDownloadLink());
	    setId(torrent.getId());
	    setImdbLink(torrent.getImdbLink());
	    setLink(torrent.getLink());
	    setName(torrent.getName());
	    setSize(torrent.getSize());
	    setTracker(torrent.getTracker());
	    setYear(torrent.getYear());
	    setYoutubeLink(torrent.getYoutubeLink());
	}

	private String stringify;

	@SuppressWarnings("unused")
	public String getStringify() {
	    return stringify;
	}

	public void setStringify(String stringify) {
	    this.stringify = stringify;
	}
    }

    private String mountHtmlMail(SchedulerData schedulerData, List<Torrent> torrents) {
	List<TorrentTpl> torrentTpls = new ArrayList<>();
	for (Torrent torrent : torrents) {
	    TorrentTpl torrentTpl = new TorrentTpl(torrent);
	    try {
		torrentTpl.setStringify(URLEncoder.encode(VntUtil.getGson().toJson(torrent), "UTF-8"));
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    }
	    torrentTpls.add(torrentTpl);
	}

	//TODO Use template files
	StringBuilder template = new StringBuilder();
	template.append("<br/>");
	template.append("<table>");
	template.append("<thead>");
	template.append("<tr>");
	template.append("<th style='font-family:Helvetica; font-size: 14px; font-weight: bold; text-align: left;'>Torrent</th>");
	template.append("<th style='font-family:Helvetica; font-size: 14px; font-weight: bold; text-align: left;'>Send to</th>");
	template.append("</tr>");
	template.append("</thead>");
	template.append("<tbody>");
	template.append("{{#torrents}}");
	template.append("<tr>");
	template.append("<td>");
	template.append("<a target='_blank' href='{{link}}' style='color: #337ab7; text-decoration:none;'>{{name}}</a>");
	template.append("</td>");
	template.append("<td>");
	template.append("{{#seedboxes}}");
	template.append("| <a target='_blank' href='{{& baseUrl}}/UploadTorrentToSeedbox.vnt?seedbox={{name}}&torrent={{stringify}}&username={{schedulerData.trackerUser.username}}&c=true'>{{name}}</a>");
	template.append("{{/seedboxes}}");
	template.append("</td>");
	template.append("</tr>");
	template.append("{{/torrents}}");
	template.append("</tbody>");
	template.append("</table>");

	Map<String, Object> scopes = new HashMap<String, Object>();
	scopes.put("baseUrl", PreferenceManager.getInstance().getPreferences().getBaseUrl());
	scopes.put("schedulerData", schedulerData);
	scopes.put("torrents", torrentTpls);
	scopes.put("seedboxes", PreferenceManager.getInstance().getPreferences().getSeedboxes());

	return VntUtil.compileTemplate(template.toString(), scopes);
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
				    email.addTo(schedulerData.getEmail());
				    email.send();
				} else {
				    log.error("MailConfig not set");
				}
			    }
			} catch (EmailException e) {
			    log.error("Problema ao enviar o email do serviço " + schedulerData, e);
			}
		}
		catch (Exception e) {
		    log.error("Problem during schedule", e);
		}
		
		Date nextRun = new Date(new Date().getTime() + (schedulerData.getInterval() * 60 * 1000));
		schedulerData.setNextRun(nextRun);
		PreferenceManager.getInstance().getScheduleData(schedulerData.getName()).setNextRun(nextRun);
		PreferenceManager.getInstance().savePreferences();
		

		log.info("Finished schedule");

	    }
	}, initialDelay, schedulerData.getInterval(), TimeUnit.MINUTES);
    }
}
