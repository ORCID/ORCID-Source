package org.orcid.frontend.email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.VerifyEmailUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.frontend.web.util.PasswordResetTokenEntry;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.EmailListChange;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.ExpiringLinkService;
import org.orcid.utils.OrcidStringUtils;
import org.orcid.utils.email.MailGunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class RecordEmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordEmailSender.class);

    /**
     * Literal text in reset_password_email.ftl / _html.ftl. The reset templates are rendered once
     * per request and this token is swapped per recipient, rather than re-rendering N times for a
     * single differing value.
     */
    private static final String RECIPIENT_EMAIL_PLACEHOLDER = "{recipientEmail}";

    @Value("${org.orcid.core.mail.apiRecordCreationEmailEnabled:true}")
    private boolean apiRecordCreationEmailEnabled;

    @Value("${org.orcid.utils.jwtExpirationInMinutes:240}")
    private long jwtExpirationInMinutes;

    @Value("${org.orcid.core.mail.passwordReset.maxRecipients:25}")
    private int passwordResetMaxRecipients;
    
    @Resource
    private ProfileEventDao profileEventDao;
    
    @Resource(name = "messageSource")
    private MessageSource messages;

    @Resource(name = "messageSourceNoFallback")
    private MessageSource messageSourceNoFallback;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private TemplateManager templateManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private SourceEntityUtils sourceEntityUtils;

    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManager;

    @Resource
    private MailGunManager mailgunManager;
    
    @Resource
    private VerifyEmailUtils verifyEmailUtils;

    @Resource
    private ExpiringLinkService expiringLinkService;

    @Resource
    private RedisClient redisClient;

    public void sendWelcomeEmail(String userOrcid, String email) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(userOrcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profileEntity);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = messages.getMessage("email.subject.register.welcome", null, userLocale);

        String userName = recordNameManager.deriveEmailFriendlyName(userOrcid);
        String verificationUrl = verifyEmailUtils.createVerificationUrl(email, orcidUrlManager.getBaseUrl());
        String orcidId = userOrcid;
        String baseUri = orcidUrlManager.getBaseUrl();
        
        templateParams.put("subject", subject);
        templateParams.put("userName", OrcidStringUtils.isValidEmailFriendlyName(userName)?userName:orcidId);
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcidId", orcidId);
        templateParams.put("baseUri", baseUri);                

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("welcome_email_v2.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("welcome_email_html_v2.ftl", templateParams);
        
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_VERIFY_ORCID_ORG, email, subject, body, html);
    }

    public void sendEmailListChangeEmail(String orcid, EmailListChange emailListChange) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profileEntity);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = messages.getMessage("email.subject.email_list_change_detected", null, userLocale);

        String userName = recordNameManager.deriveEmailFriendlyName(orcid);
        String baseUri = orcidUrlManager.getBaseUrl();

        templateParams.put("subject", subject);
        templateParams.put("userName", OrcidStringUtils.isValidEmailFriendlyName(userName)?userName:orcid);
        templateParams.put("emailListChange", emailListChange);
        templateParams.put("orcid", orcid);
        templateParams.put("baseUri", baseUri);

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("email_list_changes_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("email_list_changes_email_html.ftl", templateParams);

        Emails emails = emailManager.getEmails(orcid);

        // send email to all verified emails on the provided orcid account
        for (Email email : emails.getEmails()) {
            if (email.isVerified()) {
                mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email.getEmail(), subject, body, html);
            }
        }
        // send email to all removed verified emails
        if (emailListChange.getRemovedEmails() != null && !emailListChange.getRemovedEmails().isEmpty()) {
            for (org.orcid.jaxb.model.v3.release.record.Email email : emailListChange.getRemovedEmails()) {
                if (email.isVerified()) {
                    mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email.getEmail(), subject, body, html);
                }
            }
        }
    }

    public void sendOrcidDeactivateEmail(String userOrcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);
        Email primaryEmail = emailManager.findPrimaryEmail(userOrcid);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = verifyEmailUtils.getSubject("email.subject.deactivate", userLocale);
        String email = primaryEmail.getEmail();
        String deactivateUrlEndpointPath = "/account/deactivate";

        String token;
        try {
            token = expiringLinkService.generateExpiringToken(
                    userOrcid,
                    jwtExpirationInMinutes,
                    ExpiringLinkService.ExpiringLinkType.ACCOUNT_DEACTIVATION
            );
        } catch (com.nimbusds.jose.JOSEException e) {
            LOGGER.error("Failed to generate account deactivation token", e);
            throw new RuntimeException("Token generation failed", e);
        }

        String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(userOrcid);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", userOrcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("deactivateUrlEndpoint", deactivateUrlEndpointPath + "?token=" + token);
        templateParams.put("deactivateUrlEndpointUrl", deactivateUrlEndpointPath);
        templateParams.put("subject", subject);

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("deactivate_orcid_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("deactivate_orcid_email_html.ftl", templateParams);

        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, subject, body, html);
    }

    public void sendOrcidDeactivatedEmail(String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);
        Email primaryEmail = emailManager.findPrimaryEmail(orcid);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = verifyEmailUtils.getSubject("email.subject.deactivated", userLocale);
        String email = primaryEmail.getEmail();

        String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(orcid);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", orcid);
        templateParams.put("subject", subject);

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("account_deactivated_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("account_deactivated_email_html.ftl", templateParams);

        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, subject, body, html);
    }

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

    public void sendPasswordResetEmail(String submittedEmail, String userOrcid) {
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);
        Locale locale = getUserLocaleFromProfileEntity(record);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("orcid", userOrcid);
        templateParams.put("subject", verifyEmailUtils.getSubject("email.subject.reset", getUserLocaleFromProfileEntity(record)));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        // Mint the token and record it as the only redeemable one for this record before sending
        // anything. The fan out below is one network round trip per recipient, so the link has to
        // be redeemable by the time the first message lands; overwriting the entry is also what
        // invalidates every previously issued link.
        String token;
        try {
            token = expiringLinkService.generateExpiringToken(userOrcid, jwtExpirationInMinutes, ExpiringLinkService.ExpiringLinkType.PASSWORD_RESET);
            redisClient.set(PasswordResetTokenEntry.redisKey(userOrcid), new PasswordResetTokenEntry(token, false).serialize(), (int) (jwtExpirationInMinutes * 60));
        } catch (com.nimbusds.jose.JOSEException e) {
            LOGGER.error("Failed to generate password reset token", e);
            throw new RuntimeException("Password reset token generation failed", e);
        }
        String resetUrl = orcidUrlManager.getBaseUrl() + "/reset-password-email/" + token;
        templateParams.put("passwordResetUrl", resetUrl);
        verifyEmailUtils.addMessageParams(templateParams, locale);

        String subject = verifyEmailUtils.getSubject("email.subject.reset", locale);
        Collection<String> recipients = buildPasswordResetRecipients(submittedEmail, userOrcid);
        // Rendered once for the whole request: only the recipient's own address differs between
        // copies, and it is substituted below. Rendering also stays outside the per-recipient
        // catch, because a broken template fails for every recipient alike and swallowing that
        // would turn "nobody got a link" into a warning nobody reads.
        String bodyTemplate = templateManager.processTemplate("reset_password_email.ftl", templateParams);
        String htmlBodyTemplate = templateManager.processTemplate("reset_password_email_html.ftl", templateParams);
        int accepted = 0;
        int failed = 0;
        for (String recipient : recipients) {
            String body = bodyTemplate.replace(RECIPIENT_EMAIL_PLACEHOLDER, recipient);
            // The HTML template renders inside <#escape x as x?html>, but this substitution runs
            // after FreeMarker has finished, so the address has to be escaped here or it would be
            // the one unescaped value in the document.
            String htmlBody = htmlBodyTemplate.replace(RECIPIENT_EMAIL_PLACEHOLDER, HtmlUtils.htmlEscape(recipient));
            // One send per recipient on purpose. Putting every address in a single "to" would
            // disclose the record's whole verified email list to each of them.
            try {
                if (mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, recipient, subject, body, htmlBody)) {
                    accepted++;
                }
            } catch (Exception e) {
                // The token is already redeemable, so one bad recipient must not deny the rest
                // their copy of the link. Log the orcid only, never the address list.
                failed++;
                LOGGER.warn("Password reset: failed to send reset link for '" + userOrcid + "' to one of its recipients", e);
            }
        }
        if (!recipients.isEmpty() && failed == recipients.size()) {
            LOGGER.error("Password reset: could not send the reset link for '{}' to any of its {} recipients", userOrcid, recipients.size());
        } else {
            LOGGER.info("Password reset: reset link for '{}' accepted for {} of {} recipients", new Object[] { userOrcid, accepted, recipients.size() });
        }
    }

    /**
     * The reset link goes to the submitted address plus every verified address on the record, so a
     * user who has lost access to the mailbox they typed can still recover through one they still
     * hold. Addresses resolve case insensitively, so entries are de-duplicated on a normalized key
     * while the stored form is what actually gets mailed.
     */
    private Collection<String> buildPasswordResetRecipients(String submittedEmail, String userOrcid) {
        Map<String, String> recipients = new LinkedHashMap<String, String>();
        // The submitted address goes first: it is the one named on the confirmation screen the
        // user is watching. Keeping it also means an account with no verified address at all can
        // still reset, exactly as it could before.
        if (!PojoUtil.isEmpty(submittedEmail)) {
            recipients.put(normalizePasswordResetRecipient(submittedEmail), submittedEmail);
        }
        Emails verifiedEmails = emailManager.getVerifiedEmails(userOrcid);
        if (verifiedEmails != null && verifiedEmails.getEmails() != null) {
            for (Email email : verifiedEmails.getEmails()) {
                if (!PojoUtil.isEmpty(email.getEmail())) {
                    // Overwrites the value but keeps the insertion order, so the submitted address
                    // stays first and is mailed in its stored casing rather than as typed.
                    recipients.put(normalizePasswordResetRecipient(email.getEmail()), email.getEmail());
                }
            }
        }
        if (recipients.size() <= passwordResetMaxRecipients) {
            return recipients.values();
        }
        LOGGER.warn("Password reset: record '{}' has {} recipients, capping at {}", new Object[] { userOrcid, recipients.size(), passwordResetMaxRecipients });
        List<String> capped = new ArrayList<String>(recipients.values());
        return capped.subList(0, passwordResetMaxRecipients);
    }

    private String normalizePasswordResetRecipient(String email) {
        return OrcidStringUtils.filterEmailAddress(email).toLowerCase(Locale.ROOT);
    }

    public void sendPasswordResetNotFoundEmail(String submittedEmail, Locale locale) {
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("submittedEmail", submittedEmail);
        templateParams.put("subject", verifyEmailUtils.getSubject("email.subject.reset_not_found", locale));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        verifyEmailUtils.addMessageParams(templateParams, locale);
        // Generate body from template
        String body = templateManager.processTemplate("reset_password_not_found_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("reset_password_not_found_email_html.ftl", templateParams);
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, submittedEmail, verifyEmailUtils.getSubject("email.subject.reset_not_found", locale), body, htmlBody);
    }

    public void sendReactivationEmail(String submittedEmail, String userOrcid) {
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);
        Locale locale = getUserLocaleFromProfileEntity(record);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", recordNameManager.deriveEmailFriendlyName(userOrcid));
        templateParams.put("orcid", userOrcid);
        templateParams.put("subject", verifyEmailUtils.getSubject("email.subject.reactivatingAccount", locale));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        // Generate body from template
        String reactivationUrl = verifyEmailUtils.createReactivationUrl(submittedEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("reactivationUrl", reactivationUrl);

        verifyEmailUtils.addMessageParams(templateParams, locale);

        // Generate body from template
        String body = templateManager.processTemplate("reactivation_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("reactivation_email_html.ftl", templateParams);
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, submittedEmail, verifyEmailUtils.getSubject("email.subject.reactivatingAccount", locale), body, htmlBody);
    }

    public void sendClaimReminderEmail(String userOrcid, int daysUntilActivation, String email) {
        ProfileEntity record = profileEntityCacheManager.retrieve(userOrcid);
        String primaryEmail = emailManager.findPrimaryEmail(userOrcid).getEmail();

        if (primaryEmail == null) {
            LOGGER.info("Cant send claim reminder email if primary email is null: {}", userOrcid);
            return;
        }

        Locale locale = getUserLocaleFromProfileEntity(record);

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", recordNameManager.deriveEmailFriendlyName(userOrcid));
        templateParams.put("orcid", userOrcid);
        templateParams.put("subject", verifyEmailUtils.getSubject("email.subject.claim_reminder", locale));
        SourceEntity source = record.getSource();
        String creatorName = "";
        if (source != null) {
            String sourceName = sourceEntityUtils.getSourceName(source);
            if (!PojoUtil.isEmpty(sourceName)) {
                creatorName = sourceName;
            } else {
                creatorName = SourceEntityUtils.getSourceId(source);
            }
        }
        templateParams.put("creatorName", creatorName);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("daysUntilActivation", daysUntilActivation);

        String verificationUrl = verifyEmailUtils.createClaimVerificationUrl(primaryEmail, orcidUrlManager.getBaseUrl());
        templateParams.put("verificationUrl", verificationUrl);

        verifyEmailUtils.addMessageParams(templateParams, locale);

        // Generate body from template
        String body;
        String htmlBody;
        boolean useV2Template = false;
        try {
            messageSourceNoFallback.getMessage("email.new_claim_reminder.this_is_a_reminder.1", null, locale);
            useV2Template = true;
        } catch (NoSuchMessageException e) {

        }

        if (useV2Template) {
            body = templateManager.processTemplate("new_claim_reminder_email.ftl", templateParams);
            htmlBody = templateManager.processTemplate("new_claim_reminder_email_html.ftl", templateParams);
        } else {
            body = templateManager.processTemplate("claim_reminder_email.ftl", templateParams);
            htmlBody = templateManager.processTemplate("claim_reminder_email_html.ftl", templateParams);
        }

        // Send message
        if (apiRecordCreationEmailEnabled) {
            mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, verifyEmailUtils.getSubject("email.subject.claim_reminder", locale), body, htmlBody);
            profileEventDao.persist(new ProfileEventEntity(userOrcid, ProfileEventType.CLAIM_REMINDER_SENT));
        } else {
            LOGGER.debug("Not sending claim reminder email, because API record creation email option is disabled. Message would have been: {}", body);
        }
    }

    public void send2FADisabledEmail(String userOrcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);
        Emails emails = emailManager.getEmails(userOrcid);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = verifyEmailUtils.getSubject("email.2fa_disabled.subject", userLocale);

        String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(userOrcid);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", userOrcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("email_2fa_disabled.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("email_2fa_disabled_html.ftl", templateParams);

        for (Email email : emails.getEmails()) {
            mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email.getEmail(), subject, body, html);
        }
    }
    
    public void send2FAEnabledEmail(String userOrcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale userLocale = getUserLocaleFromProfileEntity(profile);
        Emails emails = emailManager.getEmails(userOrcid);
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String subject = verifyEmailUtils.getSubject("email.2fa_enabled.subject", userLocale);

        String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(userOrcid);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", userOrcid);
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", subject);

        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("email_2fa_enabled.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("email_2fa_enabled_html.ftl", templateParams);

        for (Email email : emails.getEmails()) {
            if (email.isPrimary()) {
                mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email.getEmail(), subject, body, html);
                return;
            }
        }
    }

    public void sendForgottenIdEmail(String email, String orcid) {
        ProfileEntity record = profileEntityCacheManager.retrieve(orcid);
        Locale locale = getUserLocaleFromProfileEntity(record);

        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("submittedEmail", email);
        templateParams.put("orcid", orcid);
        templateParams.put("subject", verifyEmailUtils.getSubject("email.subject.forgotten_id", getUserLocaleFromProfileEntity(record)));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        verifyEmailUtils.addMessageParams(templateParams, locale);

        String body = templateManager.processTemplate("forgot_id_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("forgot_id_email_html.ftl", templateParams);
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, verifyEmailUtils.getSubject("email.subject.forgotten_id", locale), body, htmlBody);
    }

    public void sendForgottenIdEmailNotFoundEmail(String email, Locale locale) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("submittedEmail", email);
        templateParams.put("subject", verifyEmailUtils.getSubject("email.subject.forgotten_id", locale));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        verifyEmailUtils.addMessageParams(templateParams, locale);

        String body = templateManager.processTemplate("forgot_id_email_not_found_email.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("forgot_id_email_not_found_email_html.ftl", templateParams);
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, verifyEmailUtils.getSubject("email.subject.forgotten_id", locale), body, htmlBody);
    }

    public void sendVerificationEmailToNonPrimaryEmails(String userOrcid) {
        emailManager.getEmails(userOrcid).getEmails().stream().filter(e -> !e.isPrimary()).map(e -> e.getEmail()).forEach(e -> {
            sendVerificationEmail(userOrcid, e, false);
        });
    }

    public void sendVerificationEmail(String userOrcid, String email, Boolean isPrimaryEmail) {
        processVerificationEmail(userOrcid, email, isPrimaryEmail);
    }

    private void processVerificationEmail(String userOrcid, String email, boolean isPrimaryEmail) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale locale = getUserLocaleFromProfileEntity(profile);
        
        String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(userOrcid);
        Map<String, Object> templateParams = verifyEmailUtils.createParamsForVerificationEmail(emailFriendlyName, userOrcid, email, isPrimaryEmail, locale);
        String subject = (String) templateParams.get("subject");
        // Generate body from template
        String body = templateManager.processTemplate("verification_email_v2.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("verification_email_html_v2.ftl", templateParams);
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_VERIFY_ORCID_ORG, email, subject, body, htmlBody);
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

	public void sendOrcidSecurityResetPasswordEmail(String userOrcid) {
		ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
		Locale userLocale = getUserLocaleFromProfileEntity(profile);
		Email primaryEmail = emailManager.findPrimaryEmail(userOrcid);
		Map<String, Object> templateParams = new HashMap<String, Object>();

		String subject = verifyEmailUtils.getSubject("email.subject.security.reset_pwd", userLocale);
		String email = primaryEmail.getEmail();

		String emailFriendlyName = recordNameManager.deriveEmailFriendlyName(userOrcid);
		templateParams.put("emailName", emailFriendlyName);
		templateParams.put("orcid", userOrcid);
		templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
		templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
		templateParams.put("subject", subject);

		verifyEmailUtils.addMessageParams(templateParams, userLocale);

		// Generate body from template
		String body = templateManager.processTemplate("email_security_reset_pwd.ftl", templateParams);
		// Generate html from template
		String html = templateManager.processTemplate("email_security_reset_pwd_html.ftl", templateParams);
		mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, subject, body, html);
	}

	public void sendOrcidSecurityDeprecatedEmail(String userOrcid, String orcidToDeprecate, Emails emails) {
		ProfileEntity profile = profileEntityCacheManager.retrieve(orcidToDeprecate);
		Locale userLocale = getUserLocaleFromProfileEntity(profile);
		Map<String, Object> templateParams = new HashMap<String, Object>();
		String subject = verifyEmailUtils.getSubject("email.subject.security.record_deprecated", userLocale);
        String deprecatedPrimaryEmail = "";
        String mainPrimaryEmail = emailManager.findPrimaryEmail(userOrcid).getEmail();

		templateParams.put("orcid", userOrcid);
		templateParams.put("deprecated_orcid", orcidToDeprecate);
		if (emails != null && emails.getEmails() != null) {
            for (org.orcid.jaxb.model.v3.release.record.Email email : emails.getEmails()) {
                if (email.isPrimary()) {
                    deprecatedPrimaryEmail = email.getEmail();
                    break;
                }
            }
			templateParams.put("emailList", emails.getEmails());
		} else {
			templateParams.put("emailList", new java.util.ArrayList<Email>());
		}
		templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
		templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
		templateParams.put("subject", subject);

		verifyEmailUtils.addMessageParams(templateParams, userLocale);

		// Generate body from template
		String body = templateManager.processTemplate("email_security_deprecate_record.ftl", templateParams);
		// Generate html from template
		String html = templateManager.processTemplate("email_security_deprecate_record_html.ftl", templateParams);

        // Send email to main primary email address
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, mainPrimaryEmail, subject, body, html);

        // Send email to deprecated primary email address
        if (!deprecatedPrimaryEmail.isEmpty()) {
            mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, deprecatedPrimaryEmail, subject, body, html);
        } else {
            LOGGER.error("Primary email not found in user {}", orcidToDeprecate);
        }
	}

    public void alternateSignInAccountAdded(String orcid, String alternateAccountName) {
        alternatesingInAccountModified(orcid, alternateAccountName, true);
    }

    public void alternateSignInAccountRemoved(String orcid, String alternateAccountName) {
        alternatesingInAccountModified(orcid, alternateAccountName, false);
    }

    private void alternatesingInAccountModified(String orcid, String alternateAccountName, boolean added) {
        ProfileEntity entity = profileEntityCacheManager.retrieve(orcid);
        Locale userLocale = getUserLocaleFromProfileEntity(entity);
        String email = emailManager.findPrimaryEmail(orcid).getEmail();
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String subject = verifyEmailUtils.getSubject("email.subject.alternate_sign_in.changed", userLocale);
        templateParams.put("subject", subject);
        templateParams.put("orcid", orcid);
        templateParams.put("alternateAccount", StringUtils.isEmpty(alternateAccountName) ? "UNDEFINED" : alternateAccountName);
        templateParams.put("accountWasAdded", added);
        verifyEmailUtils.addMessageParams(templateParams, userLocale);

        // Generate body from template
        String body = templateManager.processTemplate("email_alternate_sign_in_account.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("email_alternate_sign_in_account_html.ftl", templateParams);

        // Send email to primary email address
        mailgunManager.sendEmail(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG, email, subject, body, html);

    }
}
