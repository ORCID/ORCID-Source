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
package org.orcid.core.manager.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.CustomEmailManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.notification.amended_rc3.AmendedSection;
import org.orcid.jaxb.model.notification.amended_rc3.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_rc3.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_rc3.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_rc3.Item;
import org.orcid.jaxb.model.notification.permission_rc3.Items;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermissions;
import org.orcid.jaxb.model.notification_rc3.Notification;
import org.orcid.jaxb.model.notification_rc3.NotificationType;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.model.notification.institutional_sign_in_rc3.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Will Simpson
 */
public class NotificationManagerImpl implements NotificationManager {

    private static final String UPDATE_NOTIFY_ORCID_ORG = "ORCID <update@notify.orcid.org>";

    private static final String SUPPORT_VERIFY_ORCID_ORG = "ORCID <support@verify.orcid.org>";

    private static final String RESET_NOTIFY_ORCID_ORG = "ORCID <reset@notify.orcid.org>";

    private static final String CLAIM_NOTIFY_ORCID_ORG = "ORCID <claim@notify.orcid.org>";

    private static final String DEACTIVATE_NOTIFY_ORCID_ORG = "ORCID <deactivate@notify.orcid.org>";

    private static final String LOCKED_NOTIFY_ORCID_ORG = "ORCID <locked@notify.orcid.org>";

    private static final String AMEND_NOTIFY_ORCID_ORG = "ORCID <amend@notify.orcid.org>";

    private static final String DELEGATE_NOTIFY_ORCID_ORG = "ORCID <delegate@notify.orcid.org>";

    private static final String EMAIL_CHANGED_NOTIFY_ORCID_ORG = "ORCID <email-changed@notify.orcid.org>";

    private static final String WILDCARD_MEMBER_NAME = "${name}";

    private static final String WILDCARD_USER_NAME = "${user_name}";

    private static final String WILDCARD_WEBSITE = "${website}";

    private static final String WILDCARD_DESCRIPTION = "${description}";

    private static final String AUTHORIZATION_END_POINT = "{0}/oauth/authorize?response_type=code&client_id={1}&scope={2}&redirect_uri={3}";

    @Resource
    private MessageSource messages;

    @Resource
    private MailGunManager mailGunManager;

    private String LAST_RESORT_ORCID_USER_EMAIL_NAME = "ORCID Registry User";

    private String ORCID_PRIVACY_POLICY_UPDATES = "ORCID - Privacy Policy Updates";

    @Resource
    private OrcidUrlManager orcidUrlManager;

    private boolean apiRecordCreationEmailEnabled;

    private TemplateManager templateManager;

    private EncryptionManager encryptionManager;

    @Resource
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private CustomEmailManager customEmailManager;

    @Resource
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private EmailManager emailManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManagerImpl.class);

    public boolean isApiRecordCreationEmailEnabled() {
        return apiRecordCreationEmailEnabled;
    }

    public void setApiRecordCreationEmailEnabled(boolean apiRecordCreationEmailEnabled) {
        this.apiRecordCreationEmailEnabled = apiRecordCreationEmailEnabled;
    }

    @Required
    public void setTemplateManager(TemplateManager templateManager) {
        this.templateManager = templateManager;
    }

    @Required
    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    public void setProfileEventDao(GenericDao<ProfileEventEntity, Long> profileEventDao) {
        this.profileEventDao = profileEventDao;
    }

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    public void setNotificationDao(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    public void setMailGunManager(MailGunManager mailGunManager) {
        this.mailGunManager = mailGunManager;
    }

    @Override
    public void sendWelcomeEmail(OrcidProfile orcidProfile, String email) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String subject = getSubject("email.subject.register.thanks", orcidProfile);
        String emailName = deriveEmailFriendlyName(orcidProfile);
        String verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        String orcidId = orcidProfile.getOrcidIdentifier().getPath();
        String baseUri = orcidUrlManager.getBaseUrl();
        String baseUriHttp = orcidUrlManager.getBaseUriHttp();

        templateParams.put("subject", subject);
        templateParams.put("emailName", emailName);
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcidId", orcidId);
        templateParams.put("baseUri", baseUri);
        templateParams.put("baseUriHttp", baseUriHttp);

        SourceEntity source = sourceManager.retrieveSourceEntity();
        if (source != null) {
            String sourceId = source.getSourceId();
            String sourceName = source.getSourceName();
            // If the source is not the user itself
            if (sourceId != null && !sourceId.equals(orcidId)) {
                if (!PojoUtil.isEmpty(sourceName)) {
                    String paramValue = " " + localeManager.resolveMessage("common.through") + " " + sourceName + ".";
                    templateParams.put("source_name_if_exists", paramValue);
                } else {
                    templateParams.put("source_name_if_exists", ".");
                }
            } else {
                templateParams.put("source_name_if_exists", ".");
            }
        } else {
            templateParams.put("source_name_if_exists", ".");
        }

        addMessageParams(templateParams, orcidProfile);

        // Generate body from template
        String body = templateManager.processTemplate("welcome_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("welcome_email_html.ftl", templateParams);

        mailGunManager.sendEmail(SUPPORT_VERIFY_ORCID_ORG, email, subject, body, html);
    }

    @Override
    public void sendOrcidDeactivateEmail(OrcidProfile orcidToDeactivate) {
        // Create verification url

        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = getSubject("email.subject.deactivate", orcidToDeactivate);
        String email = orcidToDeactivate.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        String encryptedEmail = encryptionManager.encryptForExternalUse(email);
        String base64EncodedEmail = Base64.encodeBase64URLSafeString(encryptedEmail.getBytes());
        String deactivateUrlEndpointPath = "/account/confirm-deactivate-orcid";

        String emailFriendlyName = deriveEmailFriendlyName(orcidToDeactivate);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", orcidToDeactivate.getOrcidIdentifier().getPath());
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("deactivateUrlEndpoint", deactivateUrlEndpointPath + "/" + base64EncodedEmail);
        templateParams.put("deactivateUrlEndpointUrl", deactivateUrlEndpointPath);
        templateParams.put("subject", subject);

        addMessageParams(templateParams, orcidToDeactivate);

        // Generate body from template
        String body = templateManager.processTemplate("deactivate_orcid_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("deactivate_orcid_email_html.ftl", templateParams);

        mailGunManager.sendEmail(DEACTIVATE_NOTIFY_ORCID_ORG, email, subject, body, html);
    }

    @Override
    public void sendOrcidLockedEmail(OrcidProfile orcidToLock) {

        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = getSubject("email.subject.locked", orcidToLock);
        String email = orcidToLock.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();

        String emailFriendlyName = deriveEmailFriendlyName(orcidToLock);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", orcidToLock.getOrcidIdentifier().getPath());
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        addMessageParams(templateParams, orcidToLock);

        // Generate body from template
        String body = templateManager.processTemplate("locked_orcid_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("locked_orcid_email_html.ftl", templateParams);

        mailGunManager.sendEmail(LOCKED_NOTIFY_ORCID_ORG, email, subject, body, html);
    }

    // look like the following is our best best for i18n emails
    // http://stackoverflow.com/questions/9605828/email-internationalization-using-velocity-freemarker-templates
    public void sendVerificationEmail(OrcidProfile orcidProfile, String email) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String primaryEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        templateParams.put("primaryEmail", primaryEmail);
        String emailFriendlyName = deriveEmailFriendlyName(orcidProfile);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("subject", getSubject("email.subject.verify_reminder", orcidProfile));
        String verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());

        addMessageParams(templateParams, orcidProfile);

        // Generate body from template
        String body = templateManager.processTemplate("verification_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("verification_email_html.ftl", templateParams);
        mailGunManager.sendEmail(SUPPORT_VERIFY_ORCID_ORG, email, getSubject("email.subject.verify_reminder", orcidProfile), body, htmlBody);
    }

    public boolean sendServiceAnnouncement_1_For_2015(OrcidProfile orcidProfile) {
        String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        String emailFriendlyName = deriveEmailFriendlyName(orcidProfile);
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = null;
        verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        boolean needsVerification = !orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified() && orcidProfile.getType().equals(OrcidType.USER)
                && !orcidProfile.isDeactivated();
        if (needsVerification) {
            templateParams.put("verificationUrl", verificationUrl);
        }
        String emailFrequencyUrl = createUpdateEmailFrequencyUrl(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        templateParams.put("emailFrequencyUrl", emailFrequencyUrl);
        templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        addMessageParams(templateParams, orcidProfile);
        String subject = getSubject("email.service_announcement.subject.imporant_information", orcidProfile);
        String text = templateManager.processTemplate("service_announcement_1_2015.ftl", templateParams);
        String html = templateManager.processTemplate("service_announcement_1_2015_html.ftl", templateParams);
        boolean sent = mailGunManager.sendEmail("support@notify.orcid.org", email, subject, text, html);
        return sent;
    }

    public String createUpdateEmailFrequencyUrl(String email) {
        return createEmailBaseUrl(email, orcidUrlManager.getBaseUrl(), "notifications/frequencies");
    }

    // look like the following is our best best for i18n emails
    // http://stackoverflow.com/questions/9605828/email-internationalization-using-velocity-freemarker-templates
    public boolean sendPrivPolicyEmail2014_03(OrcidProfile orcidProfile) {
        String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String emailFriendlyName = deriveEmailFriendlyName(orcidProfile);
        templateParams.put("emailName", emailFriendlyName);
        if (!orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified()) {
            String verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
            templateParams.put("verificationUrl", verificationUrl);
        }
        templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());

        addMessageParams(templateParams, orcidProfile);

        String text = templateManager.processTemplate("priv_policy_upate_2014_03.ftl", templateParams);
        String html = templateManager.processTemplate("priv_policy_upate_2014_03_html.ftl", templateParams);

        return mailGunManager.sendEmail(UPDATE_NOTIFY_ORCID_ORG, email, ORCID_PRIVACY_POLICY_UPDATES, text, html);
    }

    public void addMessageParams(Map<String, Object> templateParams, OrcidProfile orcidProfile) {
        Locale locale = localeManager.getLocaleFromOrcidProfile(orcidProfile);
        templateParams.put("messages", this.messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", locale);
    }

    public void addMessageParams(Map<String, Object> templateParams, Locale locale) {
        templateParams.put("messages", this.messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", locale);
    }

    public String getSubject(String code, OrcidProfile orcidProfile) {
        Locale locale = localeManager.getLocaleFromOrcidProfile(orcidProfile);
        return messages.getMessage(code, null, locale);
    }

    private String getSubject(String code, Locale locale) {
        return messages.getMessage(code, null, locale);
    }

    private String getSubject(String code, OrcidProfile orcidProfile, String... args) {
        Locale locale = localeManager.getLocaleFromOrcidProfile(orcidProfile);
        return messages.getMessage(code, args, locale);
    }

    public void sendVerificationReminderEmail(OrcidProfile orcidProfile, String email) {
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String primaryEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        templateParams.put("primaryEmail", primaryEmail);
        String emailFriendlyName = deriveEmailFriendlyName(orcidProfile);
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
        templateParams.put("email", email);
        templateParams.put("subject", getSubject("email.subject.verify_reminder", orcidProfile));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());

        addMessageParams(templateParams, orcidProfile);

        // Generate body from template
        String body = templateManager.processTemplate("verification_reminder_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("verification_reminder_email_html.ftl", templateParams);
        mailGunManager.sendEmail(SUPPORT_VERIFY_ORCID_ORG, email, getSubject("email.subject.verify_reminder", orcidProfile), body, htmlBody);
    }

    @Override
    public String deriveEmailFriendlyName(ProfileEntity profileEntity) {
        String result = null;
        if (profileEntity != null && profileEntity.getRecordNameEntity() != null) {
            RecordNameEntity recordName = profileEntity.getRecordNameEntity();
            if (!PojoUtil.isEmpty(recordName.getCreditName())) {
                result = recordName.getCreditName();
            } else {
                if (!PojoUtil.isEmpty(recordName.getGivenNames()))
                    result = recordName.getGivenNames();
                if (!PojoUtil.isEmpty(recordName.getFamilyName()))
                    result += " " + recordName.getFamilyName();
            }
        }
        if (PojoUtil.isEmpty(result)) {
            result = LAST_RESORT_ORCID_USER_EMAIL_NAME;
        }
        return result;
    }

    @Override
    public String deriveEmailFriendlyName(OrcidProfile orcidProfile) {
        if (orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getPersonalDetails() != null) {
            PersonalDetails personalDetails = orcidProfile.getOrcidBio().getPersonalDetails();
            // all this should never be null as given names are required for
            // all...
            if (personalDetails.getCreditName() != null)
                if (!PojoUtil.isEmpty(personalDetails.getCreditName().getContent()))
                    return personalDetails.getCreditName().getContent();
            if (personalDetails.getGivenNames() != null) {
                String givenName = personalDetails.getGivenNames().getContent();
                String familyName = personalDetails.getFamilyName() != null && !StringUtils.isBlank(personalDetails.getFamilyName().getContent()) ? " "
                        + personalDetails.getFamilyName().getContent() : "";
                return givenName + familyName;
            }
        }

        return LAST_RESORT_ORCID_USER_EMAIL_NAME;
    }

    @Override
    public void sendPasswordResetEmail(String submittedEmail, OrcidProfile orcidProfile) {

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(orcidProfile));
        templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
        templateParams.put("subject", getSubject("email.subject.reset", orcidProfile));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        // Generate body from template
        String resetUrl = createResetEmail(orcidProfile, orcidUrlManager.getBaseUrl());
        templateParams.put("passwordResetUrl", resetUrl);

        addMessageParams(templateParams, orcidProfile);

        // Generate body from template
        String body = templateManager.processTemplate("reset_password_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("reset_password_email_html.ftl", templateParams);
        mailGunManager.sendEmail(RESET_NOTIFY_ORCID_ORG, submittedEmail, getSubject("email.subject.reset", orcidProfile), body, htmlBody);
    }
    
    @Override
    public void sendReactivationEmail(String submittedEmail, OrcidProfile orcidProfile) {

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(orcidProfile));
        templateParams.put("orcid", orcidProfile.getOrcidIdentifier().getPath());
        templateParams.put("subject", getSubject("email.subject.reactivation", orcidProfile));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        // Generate body from template
        String reactivationUrl = createReactivationUrl(submittedEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("reactivationUrl", reactivationUrl);

        addMessageParams(templateParams, orcidProfile);

        // Generate body from template
        String body = templateManager.processTemplate("reactivation_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("reactivation_email_html.ftl", templateParams);
        mailGunManager.sendEmail(RESET_NOTIFY_ORCID_ORG, submittedEmail, getSubject("email.subject.reactivation", orcidProfile), body, htmlBody);
    }

    @Override
    public void sendAmendEmail(OrcidProfile amendedProfile, AmendedSection amendedSection) {
        sendAmendEmail(amendedProfile, amendedSection, null);
    }

    @Override
    public void sendAmendEmail(String orcid, AmendedSection amendedSection, Item item) {
        OrcidProfile amendedProfile = orcidProfileManager.retrieveOrcidProfile(orcid, LoadOptions.BIO_AND_INTERNAL_ONLY);
        Collection<Item> items = new ArrayList<Item>(1);
        items.add(item);
        sendAmendEmail(amendedProfile, amendedSection, items);
    }

    @Override
    public void sendAmendEmail(OrcidProfile amendedProfile, AmendedSection amendedSection, Collection<Item> items) {
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        if (amenderOrcid == null) {
            LOGGER.debug("Not sending amend email, because amender is null: {}", amendedProfile);
            return;
        }
        if (amenderOrcid.equals(amendedProfile.getOrcidIdentifier().getPath())) {
            LOGGER.debug("Not sending amend email, because self edited: {}", amendedProfile);
            return;
        }
        SendChangeNotifications sendChangeNotifications = amendedProfile.getOrcidInternal().getPreferences().getSendChangeNotifications();
        if (sendChangeNotifications == null || !sendChangeNotifications.isValue()) {
            LOGGER.debug("Not sending amend email, because option to send change notifications not set to true: {}", amendedProfile);
            return;
        }
        if (OrcidType.ADMIN.equals(profileDao.retrieveOrcidType(amenderOrcid))) {
            LOGGER.debug("Not sending amend email, because modified by admin ({}): {}", amenderOrcid, amendedProfile);
            return;
        }

        String subject = getSubject("email.subject.amend", amendedProfile);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(amendedProfile));
        templateParams.put("orcid", amendedProfile.getOrcidIdentifier().getPath());
        templateParams.put("amenderName", extractAmenderName(amendedProfile, amenderOrcid));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        addMessageParams(templateParams, amendedProfile);

        // Generate body from template
        String body = templateManager.processTemplate("amend_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("amend_email_html.ftl", templateParams);

        boolean notificationsEnabled = profileDao.find(amendedProfile.getOrcidIdentifier().getPath()).getEnableNotifications();
        if (notificationsEnabled) {
            NotificationAmended notification = new NotificationAmended();
            notification.setNotificationType(NotificationType.AMENDED);
            notification.setAmendedSection(amendedSection);
            if (items != null) {
                notification.setItems(new Items(new ArrayList<>(items)));
            }
            createNotification(amendedProfile.getOrcidIdentifier().getPath(), notification);
        } else {
            String email = amendedProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
            mailGunManager.sendEmail(AMEND_NOTIFY_ORCID_ORG, email, subject, body, html);
        }
    }

    @Override
    @Transactional
    public void sendNotificationToAddedDelegate(OrcidProfile orcidUserGrantingPermission, List<DelegationDetails> delegatesGrantedByUser) {
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String subject = getSubject("email.subject.added_as_delegate", orcidUserGrantingPermission);

        for (DelegationDetails newDelegation : delegatesGrantedByUser) {
            ProfileEntity delegateProfileEntity = profileDao.find(newDelegation.getDelegateSummary().getOrcidIdentifier().getPath());
            Boolean sendAdministrativeChangeNotifications = delegateProfileEntity.getSendAdministrativeChangeNotifications();
            if (sendAdministrativeChangeNotifications == null || !sendAdministrativeChangeNotifications) {
                LOGGER.debug("Not sending added delegate email, because option to send administrative change notifications not set to true for delegate: {}",
                        delegateProfileEntity.getId());
                return;
            }

            String grantingOrcidEmail = orcidUserGrantingPermission.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
            String emailNameForDelegate = deriveEmailFriendlyName(delegateProfileEntity);
            String email = delegateProfileEntity.getPrimaryEmail().getId();

            templateParams.put("emailNameForDelegate", emailNameForDelegate);
            templateParams.put("grantingOrcidValue", orcidUserGrantingPermission.getOrcidIdentifier().getPath());
            templateParams.put("grantingOrcidName", deriveEmailFriendlyName(orcidUserGrantingPermission));
            templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
            templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
            templateParams.put("grantingOrcidEmail", grantingOrcidEmail);
            templateParams.put("subject", subject);

            addMessageParams(templateParams, orcidUserGrantingPermission);

            // Generate body from template
            String body = templateManager.processTemplate("added_as_delegate_email.ftl", templateParams);
            // Generate html from template
            String html = templateManager.processTemplate("added_as_delegate_email_html.ftl", templateParams);

            boolean notificationsEnabled = delegateProfileEntity.getEnableNotifications();
            if (notificationsEnabled) {
                NotificationCustom notification = new NotificationCustom();
                notification.setNotificationType(NotificationType.CUSTOM);
                notification.setSubject(subject);
                notification.setBodyHtml(html);
                createNotification(newDelegation.getDelegateSummary().getOrcidIdentifier().getPath(), notification);
            } else {
                mailGunManager.sendEmail(DELEGATE_NOTIFY_ORCID_ORG, email, subject, body, html);
            }
        }
    }

    @Override
    public void sendEmailAddressChangedNotification(OrcidProfile updatedProfile, Email oldEmail) {

        // build up old template
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = getSubject("email.subject.email_removed", updatedProfile);
        String email = oldEmail.getValue();
        String emailFriendlyName = deriveEmailFriendlyName(updatedProfile);
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = createVerificationUrl(updatedProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("oldEmail", oldEmail.getValue());
        templateParams.put("newEmail", updatedProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        templateParams.put("orcid", updatedProfile.getOrcidIdentifier().getPath());
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        addMessageParams(templateParams, updatedProfile);

        // Generate body from template
        String body = templateManager.processTemplate("email_removed.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("email_removed_html.ftl", templateParams);

        mailGunManager.sendEmail(EMAIL_CHANGED_NOTIFY_ORCID_ORG, email, subject, body, html);
    }

    @Override
    @Transactional
    public void sendApiRecordCreationEmail(String toEmail, OrcidProfile createdProfile) {

        Source source = null;
        CustomEmailEntity customEmail = null;
        if (createdProfile.getOrcidHistory() != null && createdProfile.getOrcidHistory().getSource() != null) {
            if (!PojoUtil.isEmpty(createdProfile.getOrcidHistory().getSource().retrieveSourcePath())) {
                source = createdProfile.getOrcidHistory().getSource();
                customEmail = getCustomizedEmail(createdProfile.getOrcidHistory().getSource().retrieveSourcePath(), EmailType.CLAIM);
            }
        }

        String email = createdProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue().trim();
        String emailName = deriveEmailFriendlyName(createdProfile);
        String orcid = createdProfile.getOrcidIdentifier().getPath();
        String verificationUrl = createClaimVerificationUrl(email, orcidUrlManager.getBaseUrl());

        String creatorName = "";
        if (source != null) {
            if (source.getSourceName() != null && source.getSourceName().getContent() != null) {
                creatorName = source.getSourceName().getContent();
            } else if (!PojoUtil.isEmpty(source.retrieveSourcePath())) {
                creatorName = source.retrieveSourcePath();
            }
        }

        String subject = null;
        String body = null;
        String htmlBody = null;
        String sender = null;

        if (customEmail != null) {
            // Get the customized sender if available
            sender = PojoUtil.isEmpty(customEmail.getSender()) ? CLAIM_NOTIFY_ORCID_ORG : customEmail.getSender();
            // Get the customized subject is available
            subject = PojoUtil.isEmpty(customEmail.getSubject()) ? getSubject("email.subject.api_record_creation", createdProfile) : customEmail.getSubject();
            // Replace the wildcards
            subject = subject.replace(WILDCARD_USER_NAME, emailName);
            subject = subject.replace(WILDCARD_MEMBER_NAME, creatorName);
            if (customEmail.isHtml()) {
                htmlBody = customEmail.getContent();
                htmlBody = htmlBody.replace(WILDCARD_USER_NAME, emailName);
                htmlBody = htmlBody.replace(WILDCARD_MEMBER_NAME, creatorName);
                htmlBody = htmlBody.replace(EmailConstants.WILDCARD_VERIFICATION_URL, verificationUrl);
                if (htmlBody.contains(WILDCARD_WEBSITE) || htmlBody.contains(WILDCARD_DESCRIPTION)) {
                    ClientDetailsEntity clientDetails = customEmail.getClientDetailsEntity();
                    htmlBody = htmlBody.replace(WILDCARD_WEBSITE, clientDetails.getClientWebsite());
                    htmlBody = htmlBody.replace(WILDCARD_DESCRIPTION, clientDetails.getClientDescription());
                }
            } else {
                body = customEmail.getContent();
                body = body.replace(WILDCARD_USER_NAME, emailName);
                body = body.replace(WILDCARD_MEMBER_NAME, creatorName);
                body = body.replace(EmailConstants.WILDCARD_VERIFICATION_URL, verificationUrl);
                if (body.contains(WILDCARD_WEBSITE) || body.contains(WILDCARD_DESCRIPTION)) {
                    ClientDetailsEntity clientDetails = customEmail.getClientDetailsEntity();
                    body = body.replace(WILDCARD_WEBSITE, clientDetails.getClientWebsite());
                    body = body.replace(WILDCARD_DESCRIPTION, clientDetails.getClientDescription());
                }
            }
        } else {
            subject = getSubject("email.subject.api_record_creation", createdProfile);
            // Create map of template params
            Map<String, Object> templateParams = new HashMap<String, Object>();
            templateParams.put("emailName", emailName);
            templateParams.put("orcid", orcid);
            templateParams.put("subject", subject);
            templateParams.put("creatorName", creatorName);
            templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
            templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
            templateParams.put("verificationUrl", verificationUrl);

            addMessageParams(templateParams, createdProfile);
            // Generate body from template
            body = templateManager.processTemplate("api_record_creation_email.ftl", templateParams);
            htmlBody = templateManager.processTemplate("api_record_creation_email_html.ftl", templateParams);
        }

        // Send message
        if (apiRecordCreationEmailEnabled) {
            boolean isCustomEmail = customEmail != null ? true : false;
            // TODO: How to handle sender? we might have to register them on
            // mailgun
            if (isCustomEmail) {
                mailGunManager.sendEmail(sender, email, subject, body, htmlBody, isCustomEmail);
            } else {
                mailGunManager.sendEmail(CLAIM_NOTIFY_ORCID_ORG, email, subject, body, htmlBody);
            }
        } else {
            LOGGER.debug("Not sending API record creation email, because option is disabled. Message would have been: {}", body);
        }
    }

    /**
     * Returns a customized email for the given client and type
     * 
     * @param source
     * @param emailType
     * @return a CustomEmailEntity if exists, null otherwise
     */
    private CustomEmailEntity getCustomizedEmail(String source, EmailType emailType) {
        return customEmailManager.getCustomEmail(source, emailType);
    }

    @Override
    public void sendClaimReminderEmail(OrcidProfile orcidProfile, int daysUntilActivation) {
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(orcidProfile));
        String orcid = orcidProfile.getOrcidIdentifier().getPath();
        templateParams.put("orcid", orcid);
        templateParams.put("subject", getSubject("email.subject.claim_reminder", orcidProfile));
        Source source = orcidProfile.getOrcidHistory().getSource();
        String creatorName = "";
        if (source != null) {
            if (source.getSourceName() != null && source.getSourceName().getContent() != null) {
                creatorName = source.getSourceName().getContent();
            } else {
                creatorName = source.retrieveSourcePath();
            }
        }
        templateParams.put("creatorName", creatorName);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("daysUntilActivation", daysUntilActivation);
        Email primaryEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        if (primaryEmail == null) {
            LOGGER.info("Cant send claim reminder email if primary email is null: {}", orcid);
            return;
        }
        String verificationUrl = createClaimVerificationUrl(primaryEmail.getValue(), orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);

        addMessageParams(templateParams, orcidProfile);

        String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        // Generate body from template
        String body = templateManager.processTemplate("claim_reminder_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("claim_reminder_email_html.ftl", templateParams);

        // Send message
        if (apiRecordCreationEmailEnabled) {
            mailGunManager.sendEmail(CLAIM_NOTIFY_ORCID_ORG, email, getSubject("email.subject.claim_reminder", orcidProfile), body, htmlBody);
            profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.CLAIM_REMINDER_SENT));
        } else {
            LOGGER.debug("Not sending claim reminder email, because API record creation email option is disabled. Message would have been: {}", body);
        }

    }

    private String extractAmenderName(OrcidProfile orcidProfile, String amenderId) {
        Delegation delegation = orcidProfile.getOrcidBio().getDelegation();
        if (delegation != null && delegation.getGivenPermissionTo() != null && !delegation.getGivenPermissionTo().getDelegationDetails().isEmpty()) {
            for (DelegationDetails delegationDetails : delegation.getGivenPermissionTo().getDelegationDetails()) {
                if (amenderId.equals(delegationDetails.getDelegateSummary().getOrcidIdentifier().getPath())) {
                    return delegationDetails.getDelegateSummary().getCreditName().getContent();
                }
            }
        }

        ClientDetailsEntity clientDetailsEntity = clientDetailsEntityCacheManager.retrieve(amenderId);
        if (clientDetailsEntity != null) {
            return clientDetailsEntity.getClientName();
        }
        return "";
    }

    @Override
    public String createClaimVerificationUrl(String email, String baseUri) {
        return createEmailBaseUrl(email, baseUri, "claim");
    }

    public String createVerificationUrl(String email, String baseUri) {
        return createEmailBaseUrl(email, baseUri, "verify-email");
    }

    private String createResetEmail(OrcidProfile orcidProfile, String baseUri) {
        String resetParams = createResetParams(orcidProfile);
        return createEmailBaseUrl(resetParams, baseUri, "reset-password-email");
    }

    private String createResetParams(OrcidProfile orcidProfile) {
        String userEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        return createResetParams(userEmail);
    }
    
    private String createResetParams(String userEmail) {
        XMLGregorianCalendar date = DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(new Date());
        String resetParams = MessageFormat.format("email={0}&issueDate={1}", new Object[] { userEmail, date.toXMLFormat() });
        return resetParams;
    }
    
    private String createReactivationUrl(String userEmail, String baseUri) {
        String resetParams = createResetParams(userEmail);
        return createEmailBaseUrl(resetParams, baseUri, "reactivation");
    }

    public String createEmailBaseUrl(String unencryptedParams, String baseUri, String path) {
        // Encrypt and encode params
        String encryptedUrlParams = encryptionManager.encryptForExternalUse(unencryptedParams);
        String base64EncodedParams = null;
        try {
            base64EncodedParams = Base64.encodeBase64URLSafeString(encryptedUrlParams.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return String.format("%s/%s/%s", baseUri, path, base64EncodedParams);
    }

    @Override
    public void sendDelegationRequestEmail(OrcidProfile managed, OrcidProfile trusted, String link) {
        // Create map of template params
        String orcid = managed.getOrcidIdentifier().getPath();
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("link", link);

        String trustedOrcidValue = trusted.retrieveOrcidPath();
        String managedOrcidValue = managed.retrieveOrcidPath();
        String emailNameForDelegate = deriveEmailFriendlyName(managed);
        String trustedOrcidName = deriveEmailFriendlyName(trusted);
        templateParams.put("emailNameForDelegate", emailNameForDelegate);
        templateParams.put("trustedOrcidName", trustedOrcidName);
        templateParams.put("trustedOrcidValue", trustedOrcidValue);
        templateParams.put("managedOrcidValue", managedOrcidValue);

        Email primaryEmail = managed.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        if (primaryEmail == null) {
            LOGGER.info("Cant send admin delegate email if primary email is null: {}", orcid);
            return;
        }

        addMessageParams(templateParams, managed);

        String htmlBody = templateManager.processTemplate("admin_delegate_request_html.ftl", templateParams);

        // Send message
        if (apiRecordCreationEmailEnabled) {
            String subject = getSubject("email.subject.admin_as_delegate", managed, trustedOrcidName);
            ProfileEntity trustedProfileEntity = profileDao.find(trusted.getOrcidIdentifier().getPath());
            boolean notificationsEnabled = trustedProfileEntity != null ? trustedProfileEntity.getEnableNotifications() : false;
            if (notificationsEnabled) {
                NotificationCustom notification = new NotificationCustom();
                notification.setNotificationType(NotificationType.CUSTOM);
                notification.setSubject(subject);
                notification.setBodyHtml(htmlBody);
                createNotification(managed.getOrcidIdentifier().getPath(), notification);
            } else {
                mailGunManager.sendEmail(DELEGATE_NOTIFY_ORCID_ORG, primaryEmail.getValue(), subject, null, htmlBody);
            }
            profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.ADMIN_PROFILE_DELEGATION_REQUEST));
        } else {
            LOGGER.debug("Not sending admin delegate email, because API record creation email option is disabled. Message would have been: {}", htmlBody);
        }
    }

    @Override
    public Notification createNotification(String orcid, Notification notification) {
        if (notification.getPutCode() != null) {
            throw new IllegalArgumentException("Put code must be null when creating a new notification");
        }
        NotificationEntity notificationEntity = notificationAdapter.toNotificationEntity(notification);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile == null) {
            throw OrcidNotFoundException.newInstance(orcid);
        }
        notificationEntity.setProfile(profile);

        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        if(sourceEntity != null) {
            // Set source id
            if (sourceEntity.getSourceProfile() != null) {
                notificationEntity.setSourceId(sourceEntity.getSourceProfile().getId());
            }
    
            if (sourceEntity.getSourceClient() != null) {
                notificationEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
            }
        } else {
            //If we can't find source id, set the user as the source
            notificationEntity.setSourceId(orcid);
        }

        notificationDao.persist(notificationEntity);
        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    public List<Notification> findUnsentByOrcid(String orcid) {
        return notificationAdapter.toNotification(notificationDao.findUnsentByOrcid(orcid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults) {
        return notificationAdapter.toNotification(notificationDao.findByOrcid(orcid, includeArchived, firstResult, maxResults));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPermissions findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults) {
        NotificationPermissions notifications = new NotificationPermissions();
        List<Notification> notificationsForOrcidAndClient = notificationAdapter.toNotification(notificationDao.findPermissionsByOrcidAndClient(orcid, client, firstResult, maxResults));
        List<NotificationPermission> notificationPermissions = new ArrayList<>();
        notificationsForOrcidAndClient.forEach(n -> notificationPermissions.add((NotificationPermission) n));
        notifications.setNotifications(notificationPermissions);
        return notifications;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findNotificationAlertsByOrcid(String orcid) {
        return notificationAdapter.toNotification(notificationDao.findNotificationAlertsByOrcid(orcid));
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findById(Long id) {
        return notificationAdapter.toNotification(notificationDao.find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findByOrcidAndId(String orcid, Long id) {
        return notificationAdapter.toNotification(notificationDao.findByOricdAndId(orcid, id));
    }

    @Override
    @Transactional
    public Notification flagAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        NotificationEntity notificationEntity = notificationDao.findByOricdAndId(orcid, id);
        if (notificationEntity == null) {
            return null;
        }
        String sourceId = sourceManager.retrieveSourceOrcid();
        if (sourceId != null && !sourceId.equals(notificationEntity.getElementSourceId())) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("activity", "notification");
            throw new WrongSourceException(params);
        }
        if (notificationEntity.getReadDate() != null) {
            throw new OrcidNotificationAlreadyReadException();
        }
        if (notificationEntity.getArchivedDate() == null) {
            notificationEntity.setArchivedDate(new Date());
            notificationDao.merge(notificationEntity);
        }
        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    @Transactional
    public Notification setActionedAndReadDate(String orcid, Long id) {
        NotificationEntity notificationEntity = notificationDao.findByOricdAndId(orcid, id);
        if (notificationEntity == null) {
            return null;
        }

        Date now = new Date();

        if (notificationEntity.getActionedDate() == null) {
            notificationEntity.setActionedDate(now);
            notificationDao.merge(notificationEntity);
        }

        if (notificationEntity.getReadDate() == null) {
            notificationEntity.setReadDate(now);
            notificationDao.merge(notificationEntity);
        }

        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    public void sendAcknowledgeMessage(String userOrcid, String clientId) throws UnsupportedEncodingException {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(userOrcid);
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        Locale userLocale = (profileEntity.getLocale() == null || profileEntity.getLocale().value() == null) ? Locale.ENGLISH : LocaleUtils.toLocale(profileEntity
                .getLocale().value());
        String subject = getSubject("email.subject.institutional_sign_in", userLocale);
        String authorizationUrl = buildAuthorizationUrlForInstitutionalSignIn(clientDetails);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(profileEntity));
        templateParams.put("orcid", userOrcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("subject", subject);
        templateParams.put("clientName", clientDetails.getClientName());
        templateParams.put("authorization_url", authorizationUrl);

        addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("authenticate_request_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("authenticate_request_email_html.ftl", templateParams);

        boolean notificationsEnabled = profileEntity.getEnableNotifications();
        if (notificationsEnabled) {
            NotificationInstitutionalConnection notification = new NotificationInstitutionalConnection();
            notification.setNotificationType(NotificationType.INSTITUTIONAL_CONNECTION);
            notification.setAuthorizationUrl(new AuthorizationUrl(authorizationUrl));
            NotificationInstitutionalConnectionEntity notificationEntity = (NotificationInstitutionalConnectionEntity) notificationAdapter
                    .toNotificationEntity(notification);
            notificationEntity.setProfile(new ProfileEntity(userOrcid));
            notificationEntity.setClientSourceId(clientId);
            notificationEntity.setAuthenticationProviderId(clientDetails.getAuthenticationProviderId());
            notificationDao.persist(notificationEntity);
        } else {
            Emails emails = emailManager.getEmails(userOrcid, (profileEntity.getLastModified() == null ? System.currentTimeMillis() : profileEntity.getLastModified()
                    .getTime()));
            String primaryEmail = null;
            if (emails == null || emails.getEmails() == null) {
                throw new IllegalArgumentException("Unable to find primary email for: " + userOrcid);
            }
            for (org.orcid.jaxb.model.record_rc3.Email email : emails.getEmails()) {
                if (email.isPrimary()) {
                    primaryEmail = email.getEmail();
                }
            }
            mailGunManager.sendEmail(UPDATE_NOTIFY_ORCID_ORG, primaryEmail, subject, body, html);
        }
    }

    public String buildAuthorizationUrlForInstitutionalSignIn(ClientDetailsEntity clientDetails) throws UnsupportedEncodingException {
        ClientRedirectUriEntity rUri = getRedirectUriForInstitutionalSignIn(clientDetails);
        if (rUri == null) {
            return null;
        }
        String urlEncodedScopes = URLEncoder.encode(rUri.getPredefinedClientScope(), "UTF-8");
        String urlEncodedRedirectUri = URLEncoder.encode(rUri.getRedirectUri(), "UTF-8");
        return MessageFormat.format(AUTHORIZATION_END_POINT, orcidUrlManager.getBaseUrl(), clientDetails.getClientId(), urlEncodedScopes, urlEncodedRedirectUri);
    }

    private ClientRedirectUriEntity getRedirectUriForInstitutionalSignIn(ClientDetailsEntity clientDetails) {
        if (clientDetails == null) {
            throw new IllegalArgumentException("Unable to find valid redirect uris for null client details");
        }

        if (clientDetails.getClientRegisteredRedirectUris() == null) {
            throw new IllegalArgumentException("Unable to find valid redirect uris for client: " + clientDetails.getId());
        }

        ClientRedirectUriEntity result = null;

        // Look for the redirect uri of INSTITUTIONAL_SIGN_IN type or if none if
        // found, return the first DEFAULT one
        for (ClientRedirectUriEntity redirectUri : clientDetails.getClientRegisteredRedirectUris()) {
            if (RedirectUriType.INSTITUTIONAL_SIGN_IN.value().equals(redirectUri.getRedirectUriType())) {
                result = redirectUri;
                break;
            }
        }

        return result;
    }

    @Override
    public void sendAutoDeprecateNotification(OrcidProfile orcidProfile, String deprecatedOrcid) {
        String orcidId = orcidProfile.getOrcidIdentifier().getPath();
        ProfileEntity deprecatedProfileEntity = profileEntityCacheManager.retrieve(deprecatedOrcid);
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(deprecatedProfileEntity.getSource().getSourceId());        
        Locale userLocale = localeManager.getLocaleFromOrcidProfile(orcidProfile);
        
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String subject = getSubject("email.subject.auto_deprecate", orcidProfile);
        String baseUri = orcidUrlManager.getBaseUrl();
        Date deprecatedAccountCreationDate = deprecatedProfileEntity.getDateCreated();
        
        // Create map of template params
        templateParams.put("primaryId", orcidId);
        templateParams.put("name", deriveEmailFriendlyName(orcidProfile));        
        templateParams.put("baseUri", baseUri);        
        templateParams.put("subject", subject);
        templateParams.put("clientName", clientDetails.getClientName());
        templateParams.put("deprecatedAccountCreationDate", deprecatedAccountCreationDate);
        templateParams.put("deprecatedId", deprecatedOrcid);
                
        addMessageParams(templateParams, userLocale);
        
        // Generate html from template
        String html = templateManager.processTemplate("auto_deprecated_account_html.ftl", templateParams);
        
        NotificationCustom notification = new NotificationCustom();
        notification.setNotificationType(NotificationType.CUSTOM);
        notification.setSubject(subject);
        notification.setBodyHtml(html);
        createNotification(orcidId, notification);
    }
}
