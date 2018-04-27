package org.orcid.core.profileEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.LocaleUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.notification_v2.NotificationType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EmailFrequencyServiceAnnouncement2018 {
    private List<ProfileEventType> eventTypes = Collections.unmodifiableList(Arrays.asList(ProfileEventType.GDPR_EMAIL_FREQUENCY_UPDATES_2018_FAIL,
            ProfileEventType.GDPR_EMAIL_FREQUENCY_UPDATES_2018_SENT, ProfileEventType.GDPR_EMAIL_FREQUENCY_UPDATES_2018_SKIPPED));

    private static Logger LOG = LoggerFactory.getLogger(EmailFrequencyServiceAnnouncement2018.class);

    ExecutorService pool = Executors.newFixedThreadPool(6);

    private ProfileDao profileDaoReadOnly;

    private MessageSource messages;

    private TemplateManager templateManager;

    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    private NotificationManager notificationManager;

    private NotificationDao notificationDao;
    
    private OrcidUrlManager orcidUrlManager;

    private static final int CHUNK_SIZE = 50000;

    @Option(name = "-testSendToOrcids", usage = "Call only on passed ORCID Ids")
    private String orcs;

    public static void main(String... args) {
        EmailFrequencyServiceAnnouncement2018 efsa = new EmailFrequencyServiceAnnouncement2018();

        CmdLineParser parser = new CmdLineParser(efsa);
        if (args == null) {
            parser.printUsage(System.err);
        }
        try {
            parser.parseArgument(args);
            efsa.init();
            efsa.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }
        System.exit(0);
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDaoReadOnly = (ProfileDao) context.getBean("profileDaoReadOnly");
        messages = (MessageSource) context.getBean("messageSource");
        templateManager = (TemplateManager) context.getBean("templateManager");
        profileEventDao = (GenericDao) context.getBean("profileEventDao");
        notificationManager = (NotificationManager) context.getBean("notificationManager");
        notificationDao = (NotificationDao) context.getBean("notificationDao");
        orcidUrlManager = (OrcidUrlManager) context.getBean("orcidUrlManager");
    }

    private void execute() {
        LOG.info("Start");
        List<String> orcids = new ArrayList<String>();
        int doneCount = 0;
        do {
            orcids = profileDaoReadOnly.findByMissingEventTypes(CHUNK_SIZE, eventTypes, null, true);
            Date now = new Date();
            for (String orcid : orcids) {
                try {
                    ProfileEntity profileEntity = profileDaoReadOnly.find(orcid);
                    if (!profileEntity.isAccountNonLocked() || profileEntity.getPrimaryRecord() != null || profileEntity.getDeactivationDate() != null
                            || (profileEntity.getClaimed() != null && !profileEntity.getClaimed())) {
                        profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.GDPR_EMAIL_FREQUENCY_UPDATES_2018_SKIPPED));
                        break;
                    }
                    Locale locale = getUserLocale(profileEntity.getLocale());
                    Map<String, Object> templateParams = new HashMap<String, Object>();
                    String emailName = notificationManager.deriveEmailFriendlyName(profileEntity);
                    templateParams.put("emailName", emailName);
                    templateParams.put("messages", this.messages);
                    templateParams.put("messageArgs", new Object[0]);
                    templateParams.put("locale", locale);
                    templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
                    
                    String subject = messages.getMessage("email.email_frequencies.subject", null, locale);
                    String text = templateManager.processTemplate("gdpr_email_frequencies_service_announcement.ftl", templateParams);
                    String html = templateManager.processTemplate("gdpr_email_frequencies_service_announcement_html.ftl", templateParams);

                    NotificationServiceAnnouncementEntity entity = new NotificationServiceAnnouncementEntity();
                    entity.setBodyHtml(html);
                    entity.setBodyText(text);
                    entity.setSubject(subject);
                    entity.setDateCreated(now);
                    entity.setLastModified(now);
                    entity.setNotificationType(NotificationType.SERVICE_ANNOUNCEMENT.name());
                    entity.setProfile(new ProfileEntity(orcid));
                    notificationDao.persist(entity);
                    profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.GDPR_EMAIL_FREQUENCY_UPDATES_2018_SENT));
                } catch (Exception e) {
                    profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.GDPR_EMAIL_FREQUENCY_UPDATES_2018_FAIL));
                }
                doneCount++;
                LOG.info("Done so far: {}", doneCount);
            }
        } while (!orcids.isEmpty());
    }

    private Locale getUserLocale(String localeStr) {
        if (PojoUtil.isEmpty(localeStr)) {
            return LocaleUtils.toLocale("en");
        }

        org.orcid.jaxb.model.common_v2.Locale locale = org.orcid.jaxb.model.common_v2.Locale.valueOf(localeStr);
        if (locale != null) {
            return LocaleUtils.toLocale(locale.value());
        }

        return LocaleUtils.toLocale("en");
    }

}
