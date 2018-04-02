package org.orcid.core.manager.v3.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDateTime;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.CustomEmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.togglz.Features;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.v3.dev1.common.OrcidType;
import org.orcid.jaxb.model.v3.dev1.notification.Notification;
import org.orcid.jaxb.model.v3.dev1.notification.NotificationType;
import org.orcid.jaxb.model.v3.dev1.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.dev1.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.dev1.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.dev1.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.dev1.notification.permission.Item;
import org.orcid.jaxb.model.v3.dev1.notification.permission.Items;
import org.orcid.jaxb.model.v3.dev1.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.dev1.notification.permission.NotificationPermissions;
import org.orcid.jaxb.model.v3.dev1.record.Emails;
import org.orcid.model.v3.dev1.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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
    
    @Value("${org.orcid.core.email.verify.tooOld:15}")
    private int emailTooOld;
    
    private int verifyReminderAfterDays = 7;

    @Resource(name = "messageSource")
    private MessageSource messages;
    
    @Resource(name = "messageSourceNoFallback")
    private MessageSource messageSourceNoFallback;

    @Resource
    private MailGunManager mailGunManager;

    private String LAST_RESORT_ORCID_USER_EMAIL_NAME = "ORCID Registry User";    

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
    private ProfileDao profileDaoReadOnly;

    @Resource
    private CustomEmailManager customEmailManager;

    @Resource(name = "jpaJaxbNotificationAdapterV3")
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Resource
    private NotificationDao notificationDao;    
    
    @Resource
    private NotificationDao notificationDaoReadOnly;    
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManager;
    
    @Resource
    private GenericDao<EmailEventEntity, Long> emailEventDao;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;

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
    public void sendWelcomeEmail(String userOrcid, String email) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(userOrcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profileEntity);
        Map<String, Object> templateParams = new HashMap<String, Object>();
        
        boolean useV2Template = false;
        String subject;
        try {
            subject = messageSourceNoFallback.getMessage("email.subject.register.welcome", null, userLocale);
            useV2Template = true;
        } catch(NoSuchMessageException e) {
            subject = messages.getMessage("email.subject.register.thanks", null, userLocale);
        }
        
        String emailName = deriveEmailFriendlyName(profileEntity);
        String verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        String orcidId = userOrcid;
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
                    String paramValue = " " + messages.getMessage("common.through", null, userLocale) + " " + sourceName + ".";
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

        addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = (useV2Template) ? templateManager.processTemplate("welcome_email_v2.ftl", templateParams) : templateManager.processTemplate("welcome_email.ftl", templateParams);
        // Generate html from template
        String html = (useV2Template) ? templateManager.processTemplate("welcome_email_html_v2.ftl", templateParams): templateManager.processTemplate("welcome_email_html.ftl", templateParams);

        mailGunManager.sendEmail(SUPPORT_VERIFY_ORCID_ORG, email, subject, body, html);
    }

    @Override
    public void sendOrcidDeactivateEmail(String userOrcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);
        org.orcid.jaxb.model.v3.dev1.record.Email primaryEmail = emailManager.findPrimaryEmail(userOrcid);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = getSubject("email.subject.deactivate", userLocale);
        String email = primaryEmail.getEmail();
        String encryptedEmail = encryptionManager.encryptForExternalUse(email);
        String base64EncodedEmail = Base64.encodeBase64URLSafeString(encryptedEmail.getBytes());
        String deactivateUrlEndpointPath = "/account/confirm-deactivate-orcid";

        String emailFriendlyName = deriveEmailFriendlyName(profile);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", userOrcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("deactivateUrlEndpoint", deactivateUrlEndpointPath + "/" + base64EncodedEmail);
        templateParams.put("deactivateUrlEndpointUrl", deactivateUrlEndpointPath);
        templateParams.put("subject", subject);

        addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("deactivate_orcid_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("deactivate_orcid_email_html.ftl", templateParams);

        mailGunManager.sendEmail(DEACTIVATE_NOTIFY_ORCID_ORG, email, subject, body, html);
    }

    @Override
    public void sendOrcidLockedEmail(String orcidToLock) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcidToLock);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);   
        
        String subject = getSubject("email.subject.locked", userLocale);
        String email = emailManager.findPrimaryEmail(orcidToLock).getEmail();
        String emailFriendlyName = deriveEmailFriendlyName(profile);
        
        Map<String, Object> templateParams = new HashMap<String, Object>();                
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", orcidToLock);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("locked_orcid_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("locked_orcid_email_html.ftl", templateParams);

        mailGunManager.sendEmail(LOCKED_NOTIFY_ORCID_ORG, email, subject, body, html);
    }
    
    @Override
    public void sendVerificationEmailToNonPrimaryEmails(String userOrcid) {
        emailManager.getEmails(userOrcid).getEmails().stream().filter(e -> !e.isPrimary()).map(e -> e.getEmail()).forEach(e -> {
            sendVerificationEmail(userOrcid, e);
        });
    }
    
    // look like the following is our best best for i18n emails
    // http://stackoverflow.com/questions/9605828/email-internationalization-using-velocity-freemarker-templates
    @Override
    public void sendVerificationEmail(String userOrcid, String email) {
        processVerificationEmail(userOrcid, email, false);
    }

    @Override
    public void sendVerificationReminderEmail(String userOrcid, String email) {
        processVerificationEmail(userOrcid, email, true);
    }
    
    private void processVerificationEmail(String userOrcid, String email, boolean isVerificationReminder) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale locale = getUserLocaleFromProfileEntity(profile);
        
        boolean useV2Template = false;
        try {
            messageSourceNoFallback.getMessage("email.verify.primary_reminder_v2", null, locale);
            useV2Template = true;
        } catch(NoSuchMessageException e) {
            
        }
        
        String primaryEmail = emailManager.findPrimaryEmail(userOrcid).getEmail();
        String emailFriendlyName = deriveEmailFriendlyName(profile);
        String subject = createSubjectForVerificationEmail(email, primaryEmail, locale);
        Map<String, Object> templateParams = createParamsForVerificationEmail(subject, emailFriendlyName, userOrcid, email, primaryEmail, locale);
        templateParams.put("isReminder", isVerificationReminder);
        // Generate body from template
        String body = (useV2Template) ? templateManager.processTemplate("verification_email_v2.ftl", templateParams) : templateManager.processTemplate("verification_email.ftl", templateParams);
        String htmlBody = (useV2Template) ? templateManager.processTemplate("verification_email_html_v2.ftl", templateParams) : templateManager.processTemplate("verification_email_html.ftl", templateParams);
        mailGunManager.sendEmail(SUPPORT_VERIFY_ORCID_ORG, email, subject, body, htmlBody);    
    }

    private String createSubjectForVerificationEmail(String email, String primaryEmail, Locale userLocale) {
        return getSubject(primaryEmail.equalsIgnoreCase(email) ? "email.subject.verify_reminder_primary" : "email.subject.verify_reminder", userLocale);
    }
    
    private Map<String, Object> createParamsForVerificationEmail(String subject, String emailFriendlyName, String orcid, String email, String primaryEmail,
            Locale locale) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("primaryEmail", primaryEmail);
        templateParams.put("isPrimary", primaryEmail.equalsIgnoreCase(email));
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcid", orcid);
        templateParams.put("email", email);
        templateParams.put("subject", subject);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        addMessageParams(templateParams, locale);
        return templateParams;
    }

    public String createUpdateEmailFrequencyUrl(String email) {
        return createEmailBaseUrl(email, orcidUrlManager.getBaseUrl(), "notifications/frequencies");
    }

    public void addMessageParams(Map<String, Object> templateParams, Locale locale) {
        Map<String, Boolean> features = getFeatures();
        templateParams.put("messages", this.messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", locale);
        templateParams.put("features", features);
    }
    
    private Map<String, Boolean> getFeatures() {
        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        return features;
    }        

    private String getSubject(String code, Locale locale) {
        return messages.getMessage(code, null, locale);
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
    public void sendPasswordResetEmail(String submittedEmail, String userOrcid) {
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);        
        String primaryEmail = emailManager.findPrimaryEmail(userOrcid).getEmail();
        Locale locale = getUserLocaleFromProfileEntity(record);
        
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(record));
        templateParams.put("orcid", userOrcid);
        templateParams.put("subject", getSubject("email.subject.reset", getUserLocaleFromProfileEntity(record)));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        // Generate body from template
        String resetUrl = createResetEmail(primaryEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("passwordResetUrl", resetUrl);

        addMessageParams(templateParams, locale);

        // Generate body from template
        String body = templateManager.processTemplate("reset_password_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("reset_password_email_html.ftl", templateParams);
        mailGunManager.sendEmail(RESET_NOTIFY_ORCID_ORG, submittedEmail, getSubject("email.subject.reset", locale), body, htmlBody);
    }

    @Override
    public void sendReactivationEmail(String submittedEmail, String userOrcid) {
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);        
        Locale locale = getUserLocaleFromProfileEntity(record);
        
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(record));
        templateParams.put("orcid", userOrcid);
        templateParams.put("subject", getSubject("email.subject.reactivation", locale));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        // Generate body from template
        String reactivationUrl = createReactivationUrl(submittedEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("reactivationUrl", reactivationUrl);

        addMessageParams(templateParams, locale);

        // Generate body from template
        String body = templateManager.processTemplate("reactivation_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("reactivation_email_html.ftl", templateParams);
        mailGunManager.sendEmail(RESET_NOTIFY_ORCID_ORG, submittedEmail, getSubject("email.subject.reactivation", locale), body, htmlBody);
    }

    @Override
    public void sendAmendEmail(String userOrcid, AmendedSection amendedSection, Collection<Item> items) {
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);        
        Locale locale = getUserLocaleFromProfileEntity(record);
        
        if (amenderOrcid == null) {
            LOGGER.info("Not sending amend email to {} because amender is null", userOrcid);
            return;
        }
        if (amenderOrcid.equals(userOrcid)) {
            LOGGER.debug("Not sending amend email, because self edited: {}", userOrcid);
            return;
        }
        
        Boolean sendChangeNotifications = record.getSendChangeNotifications();
        
        if (sendChangeNotifications == null || !sendChangeNotifications) {
            LOGGER.debug("Not sending amend email, because option to send change notifications is disabled: {}", userOrcid);
            return;
        }
        org.orcid.jaxb.model.common_v2.OrcidType amenderType = profileDao.retrieveOrcidType(amenderOrcid);
        if (amenderType != null && OrcidType.ADMIN.equals(OrcidType.fromValue(amenderType.value()))) {
            LOGGER.debug("Not sending amend email, because modified by admin ({}): {}", amenderOrcid, userOrcid);
            return;
        }

        String subject = getSubject("email.subject.amend", locale);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(record));
        templateParams.put("orcid", userOrcid);
        templateParams.put("amenderName", extractAmenderName(userOrcid, amenderOrcid));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        addMessageParams(templateParams, locale);

        // Generate body from template
        String body = templateManager.processTemplate("amend_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("amend_email_html.ftl", templateParams);

        boolean notificationsEnabled = record.getEnableNotifications();
        if (notificationsEnabled) {
            NotificationAmended notification = new NotificationAmended();
            notification.setNotificationType(NotificationType.AMENDED);
            notification.setAmendedSection(amendedSection);
            if (items != null) {
                notification.setItems(new Items(new ArrayList<>(items)));
            }
            createNotification(userOrcid, notification);
        } else {
            String primaryEmail = emailManager.findPrimaryEmail(userOrcid).getEmail();
            mailGunManager.sendEmail(AMEND_NOTIFY_ORCID_ORG, primaryEmail, subject, body, html);
        }
    }

    @Override
    @Transactional
    public void sendNotificationToAddedDelegate(String userGrantingPermission, String userReceivingPermission) {
        ProfileEntity delegateProfileEntity = profileEntityCacheManager.retrieve(userReceivingPermission);
        Boolean sendAdministrativeChangeNotifications = delegateProfileEntity.getSendAdministrativeChangeNotifications();
        if (sendAdministrativeChangeNotifications == null || !sendAdministrativeChangeNotifications) {
            LOGGER.debug("Not sending added delegate email, because option to send administrative change notifications not set to true for delegate: {}",
                    delegateProfileEntity.getId());
            return;
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(userGrantingPermission);
        Locale userLocale = getUserLocaleFromProfileEntity(delegateProfileEntity);
        String subject = getSubject("email.subject.added_as_delegate", userLocale);
        
        org.orcid.jaxb.model.v3.dev1.record.Email primaryEmail = emailManager.findPrimaryEmail(userGrantingPermission);
        String grantingOrcidEmail = primaryEmail.getEmail();
        String emailNameForDelegate = deriveEmailFriendlyName(delegateProfileEntity);
        String email = emailManager.findPrimaryEmail(userReceivingPermission).getEmail();
        String assetsUrl = getAssetsUrl();
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailNameForDelegate", emailNameForDelegate);
        templateParams.put("grantingOrcidValue", userGrantingPermission);
        templateParams.put("grantingOrcidName", deriveEmailFriendlyName(profile));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("grantingOrcidEmail", grantingOrcidEmail);
        templateParams.put("subject", subject);
        templateParams.put("assetsUrl", assetsUrl);
        
        addMessageParams(templateParams, userLocale);

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
            createNotification(userReceivingPermission, notification);
        } else {
            mailGunManager.sendEmail(DELEGATE_NOTIFY_ORCID_ORG, email, subject, body, html);
        }

    }

    @Override
    public void sendEmailAddressChangedNotification(String currentUserOrcid, String newEmail, String oldEmail) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(currentUserOrcid);        
        Locale userLocale = getUserLocaleFromProfileEntity(profile);
        
        // build up old template
        Map<String, Object> templateParams = new HashMap<String, Object>();        
        
        String subject = getSubject("email.subject.email_removed", userLocale);
        String emailFriendlyName = deriveEmailFriendlyName(profile);
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = createVerificationUrl(newEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("oldEmail", oldEmail);
        templateParams.put("newEmail", newEmail);
        templateParams.put("orcid", currentUserOrcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("email_removed.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("email_removed_html.ftl", templateParams);

        mailGunManager.sendEmail(EMAIL_CHANGED_NOTIFY_ORCID_ORG, oldEmail, subject, body, html);
    }

    @Override
    @Transactional
    public void sendApiRecordCreationEmail(String toEmail, String orcid) {
        ProfileEntity record = profileEntityCacheManager.retrieve(orcid);
        String sourceId = record.getSource() == null ? null : record.getSource().getSourceId();
        String creatorName = record.getSource() == null ? null : record.getSource().getSourceName();
        Locale userLocale = getUserLocaleFromProfileEntity(record);
        CustomEmailEntity customEmail = null;
        if (!PojoUtil.isEmpty(sourceId)) {
            customEmail = getCustomizedEmail(sourceId, EmailType.CLAIM);
        }
        String email = emailManager.findPrimaryEmail(orcid).getEmail();
        String emailName = deriveEmailFriendlyName(record);
        String verificationUrl = createClaimVerificationUrl(email, orcidUrlManager.getBaseUrl());        

        String subject = null;
        String body = null;
        String htmlBody = null;
        String sender = null;

        if (customEmail != null) {
            // Get the customized sender if available
            sender = PojoUtil.isEmpty(customEmail.getSender()) ? CLAIM_NOTIFY_ORCID_ORG : customEmail.getSender();
            // Get the customized subject is available
            subject = PojoUtil.isEmpty(customEmail.getSubject()) ? getSubject("email.subject.api_record_creation", userLocale) : customEmail.getSubject();
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
            subject = getSubject("email.subject.api_record_creation", userLocale);
            // Create map of template params
            Map<String, Object> templateParams = new HashMap<String, Object>();
            templateParams.put("emailName", emailName);
            templateParams.put("orcid", orcid);
            templateParams.put("subject", subject);
            templateParams.put("creatorName", creatorName);
            templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
            templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
            templateParams.put("verificationUrl", verificationUrl);

            addMessageParams(templateParams, userLocale);
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
    public void sendClaimReminderEmail(String userOrcid, int daysUntilActivation) {
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);        
        String primaryEmail = emailManager.findPrimaryEmail(userOrcid).getEmail();
        Locale locale = getUserLocaleFromProfileEntity(record);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(record));        
        templateParams.put("orcid", userOrcid);
        templateParams.put("subject", getSubject("email.subject.claim_reminder", locale));
        SourceEntity source = record.getSource();
        String creatorName = "";
        if (source != null) {
            if (!PojoUtil.isEmpty(source.getSourceName())) {
                creatorName = source.getSourceName();
            } else {
                creatorName = source.getSourceId();
            }
        }
        templateParams.put("creatorName", creatorName);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("daysUntilActivation", daysUntilActivation);
        if (primaryEmail == null) {
            LOGGER.info("Cant send claim reminder email if primary email is null: {}", userOrcid);
            return;
        }
        String verificationUrl = createClaimVerificationUrl(primaryEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);

        addMessageParams(templateParams, locale);

        // Generate body from template
        String body;
        String htmlBody;
        //https://trello.com/c/q2MpR3Ka/4727-update-claim-reminder-email-to-no-longer-text-saying-it-will-be-public-in-2-days
        //TODO: cleanup after translations are done
        if(Locale.ENGLISH.equals(locale)) {
            body = templateManager.processTemplate("new_claim_reminder_email.ftl", templateParams);
            htmlBody = templateManager.processTemplate("new_claim_reminder_email_html.ftl", templateParams);
        } else {
            body = templateManager.processTemplate("claim_reminder_email.ftl", templateParams);
            htmlBody = templateManager.processTemplate("claim_reminder_email_html.ftl", templateParams);
        }
        
        // Send message
        if (apiRecordCreationEmailEnabled) {
            mailGunManager.sendEmail(CLAIM_NOTIFY_ORCID_ORG, primaryEmail, getSubject("email.subject.claim_reminder", locale), body, htmlBody);
            profileEventDao.persist(new ProfileEventEntity(userOrcid, ProfileEventType.CLAIM_REMINDER_SENT));
        } else {
            LOGGER.debug("Not sending claim reminder email, because API record creation email option is disabled. Message would have been: {}", body);
        }
    }

    private String extractAmenderName(String userOrcid, String amenderId) {
        DelegateForm delegateForm = givenPermissionToManagerReadOnly.findByGiverAndReceiverOrcid(userOrcid, amenderId);
        if(delegateForm != null && !PojoUtil.isEmpty(delegateForm.getReceiverName())) {
            return delegateForm.getReceiverName().getValue();
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

    private String createResetEmail(String userEmail, String baseUri) {
        XMLGregorianCalendar date = DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(new Date());
        String resetParams = MessageFormat.format("email={0}&issueDate={1}", new Object[] { userEmail, date.toXMLFormat() });
        return createEmailBaseUrl(resetParams, baseUri, "reset-password-email");
    }
    
    private String createReactivationUrl(String userEmail, String baseUri) {
        XMLGregorianCalendar date = DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(new Date());
        String resetParams = MessageFormat.format("email={0}&issueDate={1}", new Object[] { userEmail, date.toXMLFormat() });
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
    public void sendDelegationRequestEmail(String managedOrcid, String trustedOrcid, String link) {
        // Create map of template params        
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("link", link);

        ProfileEntity managedEntity = profileEntityCacheManager.retrieve(managedOrcid);
        ProfileEntity trustedEntity = profileEntityCacheManager.retrieve(trustedOrcid);
        
        String emailNameForDelegate = deriveEmailFriendlyName(managedEntity);
        String trustedOrcidName = deriveEmailFriendlyName(trustedEntity);
        templateParams.put("emailNameForDelegate", emailNameForDelegate);
        templateParams.put("trustedOrcidName", trustedOrcidName);
        templateParams.put("trustedOrcidValue", trustedOrcid);
        templateParams.put("managedOrcidValue", managedOrcid);

        String primaryEmail = emailManager.findPrimaryEmail(managedOrcid).getEmail();
        if (primaryEmail == null) {
            LOGGER.info("Cant send admin delegate email if primary email is null: {}", managedOrcid);
            return;
        }
        
        org.orcid.jaxb.model.common_v2.Locale locale = managedEntity.getLocale();
        Locale userLocale = LocaleUtils.toLocale("en");
        
        if(locale != null) {
            userLocale = LocaleUtils.toLocale(locale.value());
        }        

        addMessageParams(templateParams, userLocale);
        
        String htmlBody = templateManager.processTemplate("admin_delegate_request_html.ftl", templateParams);

        // Send message
        if (apiRecordCreationEmailEnabled) {
            String subject = messages.getMessage("email.subject.admin_as_delegate", new Object[]{trustedOrcidName}, userLocale);
            boolean notificationsEnabled = trustedEntity != null ? trustedEntity.getEnableNotifications() : false;
            if (notificationsEnabled) {
                NotificationCustom notification = new NotificationCustom();
                notification.setNotificationType(NotificationType.CUSTOM);
                notification.setSubject(subject);
                notification.setBodyHtml(htmlBody);
                createNotification(managedOrcid, notification);
            } else {
                mailGunManager.sendEmail(DELEGATE_NOTIFY_ORCID_ORG, primaryEmail, subject, null, htmlBody);
            }
            profileEventDao.persist(new ProfileEventEntity(managedOrcid, ProfileEventType.ADMIN_PROFILE_DELEGATION_REQUEST));
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

        if (sourceEntity != null) {
            // Set source id
            if (sourceEntity.getSourceProfile() != null) {
                notificationEntity.setSourceId(sourceEntity.getSourceProfile().getId());
            }

            if (sourceEntity.getSourceClient() != null) {
                notificationEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
            }
        } else {
            // If we can't find source id, set the user as the source
            notificationEntity.setSourceId(orcid);
        }

        notificationDao.persist(notificationEntity);
        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    public List<Notification> findUnsentByOrcid(String orcid) {
        return notificationAdapter.toNotification(notificationDaoReadOnly.findUnsentByOrcid(orcid));
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
        List<Notification> notificationsForOrcidAndClient = notificationAdapter
                .toNotification(notificationDao.findPermissionsByOrcidAndClient(orcid, client, firstResult, maxResults));
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
    public List<Notification> filterActionedNotificationAlerts(Collection<Notification> notifications, String userOrcid) {
        return notifications.stream().filter(n -> {
            //Filter only INSTITUTIONAL_CONNECTION notifications
            if(NotificationType.INSTITUTIONAL_CONNECTION.equals(n.getNotificationType())) {
                boolean alreadyConnected = orcidOauth2TokenDetailService.doesClientKnowUser(n.getSource().retrieveSourcePath(), userOrcid);
                if (alreadyConnected) {
                    flagAsArchived(userOrcid, n.getPutCode(), false);
                }
                return !alreadyConnected;
            }
            return true;
        }).collect(Collectors.toList());
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
        return flagAsArchived(orcid, id, true);
    }

    @Override
    @Transactional
    public Notification flagAsArchived(String orcid, Long id, boolean validateForApi) throws OrcidNotificationAlreadyReadException {
        NotificationEntity notificationEntity = notificationDao.findByOricdAndId(orcid, id);
        if (notificationEntity == null) {
            return null;
        }
        String sourceId = sourceManager.retrieveSourceOrcid();
        if (validateForApi) {
            if (sourceId != null && !sourceId.equals(notificationEntity.getElementSourceId())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("activity", "notification");
                throw new WrongSourceException(params);
            }
            if (notificationEntity.getReadDate() != null) {
                throw new OrcidNotificationAlreadyReadException();
            }
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
        Locale userLocale = (profileEntity.getLocale() == null || profileEntity.getLocale().value() == null) ? Locale.ENGLISH
                : LocaleUtils.toLocale(profileEntity.getLocale().value());
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
            Emails emails = emailManager.getEmails(userOrcid);
            String primaryEmail = null;
            if (emails == null || emails.getEmails() == null) {
                throw new IllegalArgumentException("Unable to find primary email for: " + userOrcid);
            }
            for (org.orcid.jaxb.model.v3.dev1.record.Email email : emails.getEmails()) {
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
    public void sendAutoDeprecateNotification(String primaryOrcid, String deprecatedOrcid) {
        ProfileEntity primaryProfileEntity = profileEntityCacheManager.retrieve(primaryOrcid);
        ProfileEntity deprecatedProfileEntity = profileEntityCacheManager.retrieve(deprecatedOrcid);
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(deprecatedProfileEntity.getSource().getSourceId());
        Locale userLocale = LocaleUtils
                .toLocale(primaryProfileEntity.getLocale() == null ? org.orcid.jaxb.model.message.Locale.EN.value() : primaryProfileEntity.getLocale().value());

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String subject = getSubject("email.subject.auto_deprecate", userLocale);
        String assetsUrl = getAssetsUrl();
        Date deprecatedAccountCreationDate = deprecatedProfileEntity.getDateCreated();

        // Create map of template params
        templateParams.put("primaryId", primaryOrcid);
        templateParams.put("name", deriveEmailFriendlyName(primaryProfileEntity));
        templateParams.put("assetsUrl", assetsUrl);
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
        createNotification(primaryOrcid, notification);
    }

    public int getUnreadCount(String orcid) {
        return notificationDao.getUnreadCount(orcid);
    }

    @Override
    public void flagAsRead(String orcid, Long id) {
        notificationDao.flagAsRead(orcid, id);
    }

    @Override
    public ActionableNotificationEntity findActionableNotificationEntity(Long id) {
        return (ActionableNotificationEntity) notificationDao.find(id);
    }

    private Locale getUserLocaleFromProfileEntity(ProfileEntity profile) {
        org.orcid.jaxb.model.common_v2.Locale locale = profile.getLocale();
        if (locale != null) {
            return LocaleUtils.toLocale(locale.value());
        }

        return LocaleUtils.toLocale("en");
    }

    @Override
    public List<Notification> findNotificationsToSend(String orcid, Float emailFrequencyDays, Date recordActiveDate) {
        return notificationAdapter.toNotification(notificationDaoReadOnly.findNotificationsToSend(new Date(), orcid, emailFrequencyDays, recordActiveDate));
    }
    
    @Override
    synchronized public void processUnverifiedEmails7Days() {
        LOGGER.info("About to process unverIfied emails for reminder");
        List<Pair<String, Date>> elements = Collections.<Pair<String, Date>> emptyList();
        do {
            elements = profileDaoReadOnly.findEmailsUnverfiedDays(verifyReminderAfterDays, 100, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT);
            LOGGER.info("Got batch of {} profiles with unverified emails for reminder", elements.size());
            LocalDateTime now = LocalDateTime.now();
            Date tooOld = now.minusDays(emailTooOld).toDate();
            for (Pair<String, Date> element : elements) {
                if(element.getRight() == null || element.getRight().after(tooOld)) {
                    processUnverifiedEmails7DaysInTransaction(element.getLeft());
                } else {
                    // Mark is as too old to send the verification email
                    markUnverifiedEmailAsTooOld(element.getLeft());
                }
            }
        } while (!elements.isEmpty());
    }

    private void processUnverifiedEmails7DaysInTransaction(final String email) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            @Transactional
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String userOrcid = emailManager.findOrcidIdByEmail(email);
                    sendVerificationReminderEmail(userOrcid, email);
                    emailEventDao.persist(new EmailEventEntity(email, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT));
                    emailEventDao.flush();
                } catch (Exception e) {
                    LOGGER.error("Unable to send unverified email reminder to email: " + email, e);
                    emailEventDao.persist(new EmailEventEntity(email, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT_SKIPPED));
                    emailEventDao.flush();
                }
            }
        });
    }
    
    private void markUnverifiedEmailAsTooOld(final String email) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            @Transactional
            protected void doInTransactionWithoutResult(TransactionStatus status) {                                
                emailEventDao.persist(new EmailEventEntity(email, EmailEventType.VERIFY_EMAIL_TOO_OLD));
                emailEventDao.flush();
            }
        });
    }
    
    private String getAssetsUrl() {
        String baseUrl = orcidUrlManager.getBaseUrl();
        if(!baseUrl.endsWith("/")) {
            baseUrl += '/';
        }
        
        return baseUrl + "static/" + ReleaseNameUtils.getReleaseName();
    }
}
