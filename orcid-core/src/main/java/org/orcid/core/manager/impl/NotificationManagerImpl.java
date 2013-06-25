/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.jaxb.model.message.ApplicationSummary;
import org.orcid.jaxb.model.message.Applications;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.Source;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author Will Simpson
 */
public class NotificationManagerImpl implements NotificationManager {

    private MailSender mailSender;

    private String fromAddress;

    private String supportAddress;

    private String LAST_RESORT_ORCID_USER_EMAIL_NAME = "ORCID Registry User";

    private URI baseUri;

    private boolean apiRecordCreationEmailEnabled;

    private TemplateManager templateManager;

    private EncryptionManager encryptionManager;

    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Resource
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Resource
    private ProfileDao profileDao;

    private Properties emailSubjects = new Properties();
    {
        try {
            emailSubjects.load(getClass().getResourceAsStream("/org/orcid/core/template/email_subjects.properties"));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManagerImpl.class);

    @Required
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    @Required
    public void setSupportAddress(String supportAddress) {
        this.supportAddress = supportAddress;
    }

    @Required
    public void setBaseUri(URI baseUri) {
        this.baseUri = baseUri;
    }

    public boolean isApiRecordCreationEmailEnabled() {
        return apiRecordCreationEmailEnabled;
    }

    public void setApiRecordCreationEmailEnabled(boolean apiRecordCreationEmailEnabled) {
        this.apiRecordCreationEmailEnabled = apiRecordCreationEmailEnabled;
    }

    @Override
    @Required
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Required
    public void setTemplateManager(TemplateManager templateManager) {
        this.templateManager = templateManager;
    }

    @Required
    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Required
    @Resource(name = "securityQuestionDao")
    @Override
    public void setSecurityQuestionDao(GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao) {
        this.securityQuestionDao = securityQuestionDao;
    }

    public void setProfileEventDao(GenericDao<ProfileEventEntity, Long> profileEventDao) {
        this.profileEventDao = profileEventDao;
    }

    @Override
    public void sendOrcidDeactivateEmail(OrcidProfile orcidToDeactivate, URI baseUri) {
        // Create verification url

        Map<String, Object> templateParams = new HashMap<String, Object>();

        String emailFriendlyName = deriveEmailFriendlyName(orcidToDeactivate);
        templateParams.put("emailName", emailFriendlyName);
        templateParams.put("orcid", orcidToDeactivate.getOrcid().getValue());
        templateParams.put("baseUri", baseUri);
        templateParams.put("deactivateUrlEndpoint", "/account/confirm-deactivate-orcid");

        // Generate body from template
        String body = templateManager.processTemplate("deactivate_orcid_email.ftl", templateParams);
        // Create email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(orcidToDeactivate.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        message.setSubject(emailSubjects.getProperty("deactivate"));
        message.setText(body);
        // Send message
        sendAndLogMessage(message);
    }

    public void sendVerificationEmail(OrcidProfile orcidProfile, URI baseUri, String email) {
        Map<String, Object> templateParams = new HashMap<String, Object>();

        String emailFriendlyName = deriveEmailFriendlyName(orcidProfile);
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = createVerificationUrl(email, baseUri);
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcid", orcidProfile.getOrcid().getValue());
        templateParams.put("baseUri", baseUri);
        // Generate body from template
        String body = templateManager.processTemplate("verification_email.ftl", templateParams);
        // Create email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(emailSubjects.getProperty("verification"));
        message.setText(body);
        // Send message
        sendAndLogMessage(message);
    }

    public String deriveEmailFriendlyName(OrcidProfile orcidProfile) {
        if (orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getPersonalDetails() != null) {
            PersonalDetails personalDetails = orcidProfile.getOrcidBio().getPersonalDetails();
            // all this should never be null as given names are required for
            // all...
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
    public void sendPasswordResetEmail(OrcidProfile orcidProfile, URI baseUri) {

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(orcidProfile));
        templateParams.put("orcid", orcidProfile.getOrcid().getValue());
        templateParams.put("baseUri", baseUri);
        // Generate body from template
        String resetUrl = createResetEmail(orcidProfile, baseUri);
        templateParams.put("passwordResetUrl", resetUrl);
        String body = templateManager.processTemplate("reset_password_email.ftl", templateParams);

        // Create email message

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        message.setSubject(emailSubjects.getProperty("reset"));
        message.setText(body);
        sendAndLogMessage(message);

    }

    @Override
    public void sendAmendEmail(OrcidProfile amendedProfile, String amenderOrcid) {
        if (amenderOrcid == null) {
            LOGGER.debug("Not sending amend email, because amender is null: {}", amendedProfile);
            return;
        }
        if (amenderOrcid.equals(amendedProfile.getOrcid().getValue())) {
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
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(amendedProfile));
        templateParams.put("orcid", amendedProfile.getOrcid().getValue());
        templateParams.put("amenderName", extractAmenderName(amendedProfile, amenderOrcid));
        templateParams.put("baseUri", baseUri);
        // Generate body from template
        String body = templateManager.processTemplate("amend_email.ftl", templateParams);
        // Create email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(amendedProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        message.setSubject(emailSubjects.getProperty("amend"));
        message.setText(body);
        // Send message
        sendAndLogMessage(message);
    }

    @Override
    public void sendNotificationToAddedDelegate(OrcidProfile orcidUserGrantingPermission, List<DelegationDetails> delegatesGrantedByUser) {

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setSubject(emailSubjects.getProperty("added_as_delegate"));

        for (DelegationDetails newDelegation : delegatesGrantedByUser) {
            String emailNameForDelegate = newDelegation.getDelegateSummary() != null && newDelegation.getDelegateSummary().getCreditName() != null ? newDelegation
                    .getDelegateSummary().getCreditName().getContent() : LAST_RESORT_ORCID_USER_EMAIL_NAME;

            templateParams.put("emailNameForDelegate", emailNameForDelegate);
            templateParams.put("grantingOrcidValue", orcidUserGrantingPermission.getOrcid().getValue());
            templateParams.put("grantingOrcidName", deriveEmailFriendlyName(orcidUserGrantingPermission));
            templateParams.put("baseUri", baseUri);
            String body = templateManager.processTemplate("added_as_delegate_email.ftl", templateParams);
            String toAddress = orcidUserGrantingPermission.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
            message.setTo(toAddress);
            message.setText(body);
            // Send message
            sendAndLogMessage(message);
        }

    }

    @Override
    public void sendEmailAddressChangedNotification(OrcidProfile updatedProfile, Email oldEmail, URI baseUri) {

        // build up old template
        Map<String, Object> templateParams = new HashMap<String, Object>();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(supportAddress);
        message.setTo(oldEmail.getValue());
        message.setSubject(emailSubjects.getProperty("email_removed"));

        String emailFriendlyName = deriveEmailFriendlyName(updatedProfile);
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = createVerificationUrl(updatedProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), baseUri);
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("oldEmail", oldEmail.getValue());
        templateParams.put("newEmail", updatedProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        templateParams.put("orcid", updatedProfile.getOrcid().getValue());
        templateParams.put("baseUri", baseUri);
        // Generate body from template
        String body = templateManager.processTemplate("email_removed.ftl", templateParams);
        // Create email message
        message.setText(body);
        // Send message
        sendAndLogMessage(message);
    }

    @Override
    public void sendApiRecordCreationEmail(OrcidProfile createdProfile) {
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(createdProfile));
        templateParams.put("orcid", createdProfile.getOrcid().getValue());
        Source source = createdProfile.getOrcidHistory().getSource();
        templateParams.put("creatorName", source == null ? "" : source.getSourceName().getContent());
        templateParams.put("baseUri", baseUri);
        templateParams.put("orcid", createdProfile.getOrcid().getValue());
        String verificationUrl = createClaimVerificationUrl(createdProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), baseUri);
        templateParams.put("verificationUrl", verificationUrl);
        // Generate body from template
        String body = templateManager.processTemplate("api_record_creation_email.ftl", templateParams);
        // Create email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(createdProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue());
        message.setSubject(emailSubjects.getProperty("api_record_creation"));
        message.setText(body);
        // Send message
        if (apiRecordCreationEmailEnabled) {
            sendAndLogMessage(message);
        } else {
            LOGGER.debug("Not sending API record creation email, because option is disabled. Message would have been: {}", message);
        }
    }

    @Override
    public void sendClaimReminderEmail(OrcidProfile orcidProfile, int daysUntilActivation) {
        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", deriveEmailFriendlyName(orcidProfile));
        String orcid = orcidProfile.getOrcid().getValue();
        templateParams.put("orcid", orcid);
        Source source = orcidProfile.getOrcidHistory().getSource();
        templateParams.put("creatorName", source == null ? "" : source.getSourceName().getContent());
        templateParams.put("baseUri", baseUri);
        templateParams.put("orcid", orcid);
        templateParams.put("daysUntilActivation", daysUntilActivation);
        Email primaryEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail();
        if (primaryEmail == null) {
            LOGGER.info("Cant send claim reminder email if primary email is null: {}", orcid);
            return;
        }
        String verificationUrl = createClaimVerificationUrl(primaryEmail.getValue(), baseUri);
        templateParams.put("verificationUrl", verificationUrl);
        // Generate body from template
        String body = templateManager.processTemplate("claim_reminder_email.ftl", templateParams);
        // Create email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(primaryEmail.getValue());
        message.setSubject(emailSubjects.getProperty("claim_reminder"));
        message.setText(body);
        // Send message
        if (apiRecordCreationEmailEnabled) {
            sendAndLogMessage(message);
            profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.CLAIM_REMINDER_SENT));
        } else {
            LOGGER.debug("Not sending claim reminder email, because API record creation email option is disabled. Message would have been: {}", message);
        }

    }

    private String extractAmenderName(OrcidProfile orcidProfile, String amenderOrcid) {
        Delegation delegation = orcidProfile.getOrcidBio().getDelegation();
        if (delegation != null && delegation.getGivenPermissionTo() != null && !delegation.getGivenPermissionTo().getDelegationDetails().isEmpty()) {
            for (DelegationDetails delegationDetails : delegation.getGivenPermissionTo().getDelegationDetails()) {
                if (amenderOrcid.equals(delegationDetails.getDelegateSummary().getOrcid().getValue())) {
                    return delegationDetails.getDelegateSummary().getCreditName().getContent();
                }
            }
        }
        Applications applications = orcidProfile.getOrcidBio().getApplications();
        if (applications != null && applications.getApplicationSummary() != null && !applications.getApplicationSummary().isEmpty()) {
            for (ApplicationSummary applicationSummary : applications.getApplicationSummary()) {
                if (amenderOrcid.equals(applicationSummary.getApplicationOrcid().getValue())) {
                    return applicationSummary.getApplicationName().getContent();
                }
            }
        }
        return "";
    }

    private String createClaimVerificationUrl(String email, URI baseUri) {
        return createEmailBaseUrl(email, baseUri, "claim");
    }

    public String createVerificationUrl(String email, URI baseUri) {
        return createEmailBaseUrl(email, baseUri, "verify-email");
    }

    private String createResetEmail(OrcidProfile orcidProfile, URI baseUri) {
        String userEmail = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        XMLGregorianCalendar date = DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(new Date());
        String resetParams = MessageFormat.format("email={0}&issueDate={1}", new Object[] { userEmail, date.toXMLFormat() });
        return createEmailBaseUrl(resetParams, baseUri, "reset-password-email");
    }

    public String createEmailBaseUrl(String unencryptedParams, URI baseUri, String path) {
        // Encrypt and encode params
        String encryptedUrlParams = encryptionManager.encryptForExternalUse(unencryptedParams);
        String base64EncodedParams = null;
        try {
            base64EncodedParams = Base64.encodeBase64URLSafeString(encryptedUrlParams.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return String.format("%s/%s/%s", baseUri.toString(), path, base64EncodedParams);
    }

    private void sendAndLogMessage(SimpleMailMessage message) {
        LOGGER.info("Sending email message: {}", message);
        try {
            mailSender.send(message);
        } catch (RuntimeException e) {
            LOGGER.error("Error sending email: {}", message, e);
        }
    }

}
