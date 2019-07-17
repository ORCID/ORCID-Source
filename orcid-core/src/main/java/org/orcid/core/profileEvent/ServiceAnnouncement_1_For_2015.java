package org.orcid.core.profileEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.v3.rc1.common.OrcidType;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

public class ServiceAnnouncement_1_For_2015 implements ProfileEvent {

    private static Logger LOG = LoggerFactory.getLogger(ServiceAnnouncement_1_For_2015.class);

    @Resource
    private NotificationManager notificationManager;

    @Resource(name = "messageSource")
    private MessageSource messages;
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private ProfileDao profileDaoReadOnly;

    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private TemplateManager templateManager;
    
    @Resource
    private MailGunManager mailGunManager;
    
    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;
    
    String orcidId;

    /*
     * export MAVEN_OPTS=
     * "-Xmx1024m -XX:MaxPermSize=256m -Dorg.orcid.config.file=file:///Users/rcpeters/git/ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties"
     * ; mvn install; mvn exec:java
     * -Dexec.mainClass="org.orcid.core.profileEvent.ProfileEventManager"
     * -Dexec.args="-bean serviceAnnouncement_1_For_2015 -callOnAll true";
     * 
     * Following https://github.com/ORCID/ORCID-Source/blob/
     * eeae0d0933c68aacc4ef0fbf0846fb99ae9a1257/orcid-core/src/main/java/org/
     * orcid/core/profileEvent/CrossRefEmail.java
     * 
     */

    private List<ProfileEventType> pes = Collections
            .unmodifiableList(Arrays.asList(ProfileEventType.SERVICE_ANNOUNCEMENT_SENT_1_FOR_2015,
                    ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015,
                    ProfileEventType.SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015));

    ServiceAnnouncement_1_For_2015(String orcid) {
        this.orcidId = orcid;
    }

    @Override
    public ProfileEventResult call() throws Exception {
        ProfileEntity entity = profileDaoReadOnly.find(orcidId);

        if (!entity.isAccountNonLocked() || entity.getDeactivationDate() != null || entity.getPrimaryRecord() != null
                || !entity.getClaimed() || OrcidType.USER.equals(OrcidType.valueOf(entity.getOrcidType()))) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017);
        }

        Email primaryEmail = emailManager.findPrimaryEmail(orcidId);

        ProfileEventResult pes = new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_SENT_1_FOR_2015);
        try {
            boolean sent = sendServiceAnnouncement_1_For_2015(orcidId, primaryEmail.getEmail(), entity.getLocale());
            if (!sent)
                pes = new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015);
        } catch (Exception e) {
            LOG.error("ProfileEventType exception trying to send email to: " + orcidId,
                    e);
            pes = new ProfileEventResult(orcidId, ProfileEventType.SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015);
        }
        return pes;
    }

    @Override
    public List<ProfileEventType> outcomes() {
        return pes;
    }
    
    public boolean sendServiceAnnouncement_1_For_2015(String orcid, String email, String localeString) {
        String emailFriendlyName = notificationManager.deriveEmailFriendlyName(orcid);
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = notificationManager.createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);
        String emailFrequencyUrl = notificationManager.createUpdateEmailFrequencyUrl(email);
        templateParams.put("emailFrequencyUrl", emailFrequencyUrl);
        templateParams.put("orcid", orcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        
        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        
        Locale locale =  LocaleUtils
                .toLocale(localeString == null ? org.orcid.jaxb.model.common_v2.Locale.EN.value() : org.orcid.jaxb.model.common_v2.Locale.valueOf(localeString).value());
        
        templateParams.put("messages", this.messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", locale);
        templateParams.put("features", features);
        
        String subject = messages.getMessage("email.service_announcement.subject.imporant_information", null, locale);
        String text = templateManager.processTemplate("service_announcement_1_2015.ftl", templateParams);
        String html = templateManager.processTemplate("service_announcement_1_2015_html.ftl", templateParams);
        return mailGunManager.sendEmail("support@notify.orcid.org", email, subject, text, html);
    }

    @Override
    public void setOrcidId(String orcid) {
        this.orcidId = orcid;
    }

    @Override
    public String getOrcidId() {
        return this.orcidId;
    }

}
