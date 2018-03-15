/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.profileEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventType;

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

    private OrcidProfile orcidProfile;

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

    VerifiedRequiredAnnouncement2017(OrcidProfile op) {
        this.orcidProfile = op;
    }

    @Override
    public OrcidProfile getOrcidProfile() {
        return orcidProfile;
    }

    @Override
    public ProfileEventResult call() throws Exception {
        String orcidId = getOrcidProfile().getOrcidIdentifier().getPath();

        if (orcidProfile.getOrcidBio() == null || orcidProfile.getOrcidBio().getContactDetails() == null
                || orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() == null
                || orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() == null) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017);
        }

        if (orcidProfile.isLocked() || orcidProfile.isDeactivated() || orcidProfile.getOrcidDeprecated() != null
                || !orcidProfile.getOrcidHistory().getClaimed().isValue()) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SKIPPED_2017);
        }

        Email primaryEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        if (primaryEmail.isVerified()) {
            return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_HAS_VALIDATED_2017);
        } else {
            try {
                boolean sent = sendVerifiedRequiredAnnouncement2017(orcidProfile);
                if (sent) {
                    return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_SENT_2017);
                } else {
                    return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017);
                }
            } catch (Exception e) {
                LOG.error("ProfileEventType exception trying to send email to: " + orcidProfile.retrieveOrcidUriAsString(), e);
                return new ProfileEventResult(orcidId, ProfileEventType.VERIFIED_REQUIRED_FAIL_2017);
            }
        }
    }

    @Override
    public List<ProfileEventType> outcomes() {
        return pes;
    }
    
    public boolean sendVerifiedRequiredAnnouncement2017(OrcidProfile orcidProfile) {
        String orcid = orcidProfile.getOrcidIdentifier().getPath();
        String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        String emailFriendlyName = notificationManager.deriveEmailFriendlyName(profileDaoReadOnly.find(orcid));
        String verificationUrl = notificationManager.createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        String emailFrequencyUrl = notificationManager.createUpdateEmailFrequencyUrl(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());

        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        
        Locale locale = localeManager.getLocaleFromOrcidProfile(orcidProfile);
        
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

}
