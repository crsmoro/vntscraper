package com.shuffle.vnt.core.schedule;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.shuffle.sieve.core.exception.CaptchaException;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.sieve.core.service.TrackerManager;
import com.shuffle.vnt.core.configuration.PreferenceManager;
import com.shuffle.vnt.core.configuration.model.MailConfig;
import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.schedule.model.Job;
import com.shuffle.vnt.core.schedule.model.JobSeedbox;
import com.shuffle.vnt.core.security.VntSecurity;
import com.shuffle.vnt.core.service.Service;
import com.shuffle.vnt.core.service.ServiceFactory;
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

	private String mountHtmlMail(Job job, List<Torrent> torrents) {
		List<Object> torrentTpls = new ArrayList<>();
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (Torrent torrent : torrents) {

			executorService.submit(() -> {
				log.debug("Mounting HTML of " + torrent);
				try {
					Torrent torrentClone = torrent.clone();
					TrackerManager.getInstance(torrentClone.getTracker(), torrentClone.getUsername(), torrentClone.getPassword()).getDetails(torrentClone);

					torrentClone.setContent("");
					Map<String, Object> torrentObject = VntUtil.clazzToObject(torrentClone);
					String stringify = null;
					try {
						stringify = URLEncoder.encode(VntUtil.toJson(torrentClone), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error("Error enconding torrent's stringify", e);
					}
					torrentObject.put("chave", Hex.encodeHexString(VntSecurity.encrypt(stringify, VntSecurity.getTokenKey()).getBytes(StandardCharsets.UTF_8)));
					torrentObject.put("movie", VntUtil.getMovie(torrentClone));
					torrentTpls.add(torrentObject);
				} catch (CloneNotSupportedException e) {

				}
				log.debug("Finished mounting HTML of " + torrent);
			});

		}
		executorService.shutdown();
		try {
			executorService.awaitTermination(30, TimeUnit.MINUTES);

		} catch (InterruptedException e) {

		}

		String template = "";

		InputStream defaultTemplateStream = getClass().getProtectionDomain().getClassLoader().getResourceAsStream("com/shuffle/vnt/web/webapp/template/defaultTemplate.html");
		StringBuilder defaultTemplate = new StringBuilder();
		String templateContent = String.join(System.lineSeparator(), new BufferedReader(new InputStreamReader(defaultTemplateStream)).lines().collect(Collectors.toList()));
		if (templateContent != null && !Optional.ofNullable(templateContent).map(String::trim).map(String::isEmpty).orElse(false)) {
			defaultTemplate.append(templateContent);
		} else {
			log.error("Error getting default template file, using the hardcoded");
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
			defaultTemplate.append("| <a target='_blank' href='{{& baseUrl}}/UploadTorrentToSeedbox.vnt?seedbox={{name}}&chave={{chave}}&username={{schedulerData.trackerUser.username}}&c=true'>{{name}}</a>");
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
		scopes.put("seedboxes", PersistenceManager.getDao(JobSeedbox.class).eq("job", job).findAll().stream().map(js -> js.getSeedbox()).collect(Collectors.toList()));
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
		long diff = Optional.ofNullable(job.getNextRun()).orElse(new Date()).getTime() - now.getTime();
		log.info("Difference between now and next run " + diff);
		if (diff >= 0) {
			initialDelay = (long) Math.ceil((diff / 1000) / 60);
		}
		log.info("Initial delay " + initialDelay);

		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				log.info("Starting schedule");

				MailConfig mailConfig = PreferenceManager.getPreferences().getMailConfig();
				if (mailConfig == null) {
					log.error("MailConfig not set");
				} else {
					try {
						List<Torrent> torrents = new ArrayList<>();
						TrackerManager trackerManager = TrackerManager.getInstance(job.getTrackerUser().getTracker(), job.getTrackerUser().getUsername(),
								VntSecurity.decrypt(job.getTrackerUser().getPassword(), VntSecurity.getPasswordKey()));
						trackerManager.setQueryParameters(job.getQueryParameters());
						if (job.getServiceParser() != null) {
							Service serviceParser = ServiceFactory.getInstance(job.getServiceParser());
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

							}
						} catch (EmailException e) {
							log.error("Problema ao enviar o email do serviço " + job, e);
						}
					} catch (CaptchaException e) {
						log.error("Captcha problem", e);
						try {
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
							email.setSubject("VNT Schedule - Captcha Problem");
							email.setHtmlMsg(e.getMessage());
							email.addTo(job.getEmail().split(";"));
							email.send();
						} catch (EmailException em) {
							log.error("Problema ao enviar o email do serviço " + job, em);
						}
					} catch (Exception e) {
						log.error("Problem during schedule", e);
					}
				}

				Date nextRun = new Date(Optional.ofNullable(job.getNextRun()).orElse(job.getStartDate()).getTime() + (job.getInterval() * 60 * 1000));
				int attempt = 1;
				while (nextRun.before(new Date())) {
					attempt++;
					nextRun = new Date(Optional.ofNullable(job.getNextRun()).orElse(job.getStartDate()).getTime() + (attempt * job.getInterval() * 60 * 1000));
				}
				job.setNextRun(nextRun);
				PersistenceManager.getDao(Job.class).save(job);

				log.info("Finished schedule");

			}
		}, initialDelay, job.getInterval(), TimeUnit.MINUTES);
	}
}
