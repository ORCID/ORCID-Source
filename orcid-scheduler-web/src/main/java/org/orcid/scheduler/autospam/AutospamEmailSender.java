package org.orcid.scheduler.autospam;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.RecordNameManager;

import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;

import org.orcid.core.utils.VerifyEmailUtils;
import org.orcid.jaxb.model.common.AvailableLocales;


import org.orcid.persistence.jpa.entities.ProfileEntity;


import org.orcid.utils.email.MailGunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

//TODO refactor the RecordEmailSender for orcid-web and move it under orcid-core package as it was before jersey upgrade
@Component
public class AutospamEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutospamEmailSender.class);

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private TemplateManager templateManager;


    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManager;

    @Resource
    private MailGunManager mailgunManager;
    
    @Resource
    private VerifyEmailUtils verifyEmailUtils;

  
    public void sendOrcidLockedEmail(String orcidToLock) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcidToLock);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);

        String subject = verifyEmailUtils.getSubject("email.subject.locked", userLocale);
        String email = emailManager.findPrimaryEmail(orcidToLock).getEmail();
        String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(orcidToLock);

        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", orcidToLock);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("locked_orcid_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("locked_orcid_email_html.ftl", templateParams);

        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, subject, body, html);
    }
      
    private Locale getUserLocaleFromProfileEntity(ProfileEntity profile) {
        String locale = profile.getLocale();
        try {
            if (locale != null) {
                return LocaleUtils.toLocale(AvailableLocales.valueOf(locale).value());
            }
        }
        catch(Exception ex) {
            LOGGER.error("Locale is not supported in the available locales, defaulting to en", ex);
        }
        return LocaleUtils.toLocale("en");
    }       
}
