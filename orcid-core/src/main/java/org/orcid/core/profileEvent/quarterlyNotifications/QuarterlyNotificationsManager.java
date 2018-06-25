package org.orcid.core.profileEvent.quarterlyNotifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.notification_v2.NotificationType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class QuarterlyNotificationsManager {

    private static Logger LOG = LoggerFactory.getLogger(QuarterlyNotificationsManager.class);
    
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    private ProfileDao profileDaoReadOnly;
    
    private NotificationDao notificationDao;
    
    private OrcidUrlManager orcidUrlManager;
    
    protected MessageSource messages;
    
    ExecutorService pool;
    
    private final ProfileEventType CREATED;
    
    private final ProfileEventType SKIPPED;
    
    private final ProfileEventType FAILED;
    
    private final String NOTIFICATION_FAMILY;
    
    private final Boolean LOCALIZED;
    
    public QuarterlyNotificationsManager(ProfileEventType created, ProfileEventType skipped, ProfileEventType failed, String notificationFamily, Boolean localized, Integer maxThreads) {
        this.CREATED = created;
        this.SKIPPED = skipped;
        this.FAILED = failed;
        this.NOTIFICATION_FAMILY = notificationFamily;
        this.LOCALIZED = localized;
        
        if(maxThreads == null || maxThreads > 64 || maxThreads < 1) {
            pool = Executors.newFixedThreadPool(8);
        } else {
            pool = Executors.newFixedThreadPool(maxThreads);
        }
        
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDaoReadOnly = (ProfileDao) context.getBean("profileDaoReadOnly");
        notificationDao = (NotificationDao) context.getBean("notificationDao");        
        orcidUrlManager = (OrcidUrlManager) context.getBean("orcidUrlManager");
        messages = (MessageSource) context.getBean("messageSource");
    }
    
    public void execute(String htmlTemplateName, String plainTextTemplateName, Integer chunkSize, Integer poolSize) throws InterruptedException {
        LOG.info("Start");
        List<String> orcids = new ArrayList<String>();  
        int doneCount = 0;
        
        List<ProfileEventType> eventTypes = Arrays.asList(CREATED, SKIPPED, FAILED);
        
        do {
            long startTime = System.currentTimeMillis();
            orcids = profileDaoReadOnly.findByMissingEventTypes(chunkSize, eventTypes, null, true);
            Set<Callable<Boolean>> callables = new HashSet<Callable<Boolean>>();
            for (String orcid : orcids) {
                callables.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        processNotification(orcid);
                        return true;
                    }
                    
                });
            }
            // Runthem all 
            pool.invokeAll(callables);
            doneCount += callables.size();
            long endTime = System.currentTimeMillis();
            String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
            LOG.info("DoneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
        } while (!orcids.isEmpty());
    }
    
    private void processNotification(String orcid) {
        LOG.info("Processing {}", orcid);
        Date now = new Date();
        try {
            ProfileEntity profileEntity = profileDaoReadOnly.find(orcid);
            if (!profileEntity.isAccountNonLocked() || profileEntity.getPrimaryRecord() != null || profileEntity.getDeactivationDate() != null
                    || (profileEntity.getClaimed() != null && !profileEntity.getClaimed())) {
                profileEventDao.persistIgnoringProfileLastModifiedUpdate(new ProfileEventEntity(orcid, SKIPPED));
                return;
            }
            
            Locale locale = getUserLocale(profileEntity.getLocale()); 
            
            Map<String, Object> templateParams = generateTemplateParams(profileEntity, locale);
            templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
            templateParams.put("locale", locale);
            templateParams.put("messages", this.messages);
            templateParams.put("messageArgs", new Object[0]);
            
            String text = generatePlainTextNotification(templateParams);
            String html = generateHtmlNotification(templateParams);

            NotificationTipEntity entity = new NotificationTipEntity();
            entity.setSubject(getSubject(locale));
            entity.setBodyHtml(html);
            entity.setBodyText(text);
            entity.setDateCreated(now);
            entity.setLastModified(now);
            entity.setNotificationType(NotificationType.TIP.name());
            entity.setNotificationFamily(NOTIFICATION_FAMILY);
            entity.setProfile(new ProfileEntity(orcid));
            entity.setSendable(true);
            notificationDao.persistIgnoringProfileLastModifiedUpdate(entity);
            profileEventDao.persistIgnoringProfileLastModifiedUpdate(new ProfileEventEntity(orcid, CREATED));
        } catch (Exception e) {
            LOG.error("Exception for: {}", orcid);
            LOG.error("Error", e);
            profileEventDao.persistIgnoringProfileLastModifiedUpdate(new ProfileEventEntity(orcid, FAILED));
        }
    }
    
    protected Locale getUserLocale(String localeStr) {
        if(LOCALIZED) {
            return Locale.ENGLISH;
        } 
        
        if (PojoUtil.isEmpty(localeStr)) {
            return LocaleUtils.toLocale("en");
        }

        org.orcid.jaxb.model.common_v2.Locale locale = org.orcid.jaxb.model.common_v2.Locale.valueOf(localeStr);
        if (locale != null) {
            return LocaleUtils.toLocale(locale.value());
        }

        return LocaleUtils.toLocale("en");
    } 
    
    public abstract String getSubject(Locale locale);
    
    public abstract Map<String, Object> generateTemplateParams(ProfileEntity profileEntity, Locale locale);
    
    public abstract String generatePlainTextNotification(Map<String, Object> templateParams);
    
    public abstract String generateHtmlNotification(Map<String, Object> templateParams);        
}
