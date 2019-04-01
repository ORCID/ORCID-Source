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
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

public class VerifiedRequiredAnnouncement2017 implements ProfileEvent {

    private static Logger LOG = LoggerFactory.getLogger(VerifiedRequiredAnnouncement2017.class);

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
     * "-Xmx1024m -XX:MaxPermSize=256m -Dorg.orcid.config.file=file:///.../ORCID-Source/orcid-persistence/src/main/resources/staging-persistence.properties"
     * ; mvn install; mvn exec:java
     * -Dexec.mainClass="org.orcid.core.profileEvent.ProfileEventManager"
     * -Dexec.args="-bean verifiedRequiredAnnouncement2017 -callOnAll true";
     * 
     */

    private List<ProfileEventType> pes = Collections.unmodifiableList(Arrays.asList(ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017,
            ProfileEventType.VERIFIED_REQUIRED_HAS_VALIDATED_2017, ProfileEventType.VERIFIED_REQUIRED_SENT_2017, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017));

    @Override
    public ProfileEventResult call() throws Exception {
        ProfileEntity entity = profileDaoReadOnly.find(orcidId);

        if (!entity.isAccountNonLocked() || entity.getDeactivationDate() != null || entity.getPrimaryRecord() != null
                || !entity.getClaimed()) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017);
        }

        Email primaryEmail = emailManager.findPrimaryEmail(orcidId);
        if (primaryEmail.isVerified()) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_HAS_VALIDATED_2017);
        } else {
            try {
                boolean sent = sendVerifiedRequiredAnnouncement2017(orcidId, primaryEmail.getEmail(), entity.getLocale());
                if (sent) {
                    return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SENT_2017);
                } else {
                    return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017);
                }
            } catch (Exception e) {
                LOG.error("ProfileEventType exception trying to send email to: " + orcidId, e);
                return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017);
            }
        }
    }

    @Override
    public List<ProfileEventType> outcomes() {
        return pes;
    }
    
    public boolean sendVerifiedRequiredAnnouncement2017(String orcid, String email, String localeString) {
        String emailFriendlyName = notificationManager.deriveEmailFriendlyName(profileDaoReadOnly.find(orcid));
        String verificationUrl = notificationManager.createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        String emailFrequencyUrl = notificationManager.createUpdateEmailFrequencyUrl(email);

        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        
        Locale locale =  LocaleUtils
                .toLocale(localeString == null ? org.orcid.jaxb.model.common_v2.Locale.EN.value() : org.orcid.jaxb.model.common_v2.Locale.valueOf(localeString).value());
        
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("emailFrequencyUrl", emailFrequencyUrl);
        templateParams.put("orcid", orcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("messages", this.messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", locale);
        templateParams.put("features", features);
        

        String subject = messages.getMessage("email.service_announcement.subject.imporant_information", null, locale);
        String text = templateManager.processTemplate("verified_required_announcement_2017.ftl", templateParams);
        String html = templateManager.processTemplate("verified_required_announcement_2017_html.ftl", templateParams);

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
