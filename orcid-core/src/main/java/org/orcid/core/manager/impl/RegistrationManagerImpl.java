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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.constants.DefaultPreferences;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class RegistrationManagerImpl implements RegistrationManager, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationManagerImpl.class);

    private static final String COMMON_PASSWORDS_FILENAME = "common_passwords.txt";

    private EncryptionManager encryptionManager;

    private NotificationManager notificationManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private EmailManager emailManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private AdminManager adminManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private OrcidGenerationManager orcidGenerationManager;

    private List<String> commonPasswords;

    @Required
    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    @Required
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public void resetUserPassword(String toEmail, OrcidProfile orcidProfile) {
        LOGGER.debug("Resetting password for Orcid: {}", orcidProfile.getOrcidIdentifier().getPath());
        if (!orcidProfile.getOrcidHistory().isClaimed()) {
            LOGGER.debug("Profile is not claimed so re-sending claim email instead of password reset: {}", orcidProfile.getOrcidIdentifier().getPath());
            notificationManager.sendApiRecordCreationEmail(toEmail, orcidProfile);
        } else {
            notificationManager.sendPasswordResetEmail(toEmail, orcidProfile);
        }
    }

    @Override
    public Long getCount() {
        return profileDao.getConfirmedProfileCount();
    }

    @Override
    public VerifyRegistrationToken parseEncyrptedParamsForVerification(String encryptedParams) {
        String decryptedParams = encryptionManager.decryptForExternalUse(encryptedParams);
        LOGGER.debug("Got verification params: {}", decryptedParams);
        return new VerifyRegistrationToken(decryptedParams);
    }

    @Override
    public String createMinimalRegistration(Registration registration, boolean usedCaptcha, Locale locale, String ip) {
        String emailAddress = registration.getEmail().getValue();
        try {
            String orcidId = transactionTemplate.execute(new TransactionCallback<String>() {
                public String doInTransaction(TransactionStatus status) {
                    if (emailManager.emailExists(emailAddress)) {
                        checkAutoDeprecateIsEnabledForEmail(emailAddress);
                        String unclaimedOrcid = getOrcidIdFromEmail(emailAddress);
                        emailManager.removeEmail(unclaimedOrcid, emailAddress, true);
                        String newUserOrcid = createMinimalProfile(registration, usedCaptcha, locale, ip);
                        ProfileDeprecationRequest result = new ProfileDeprecationRequest();
                        adminManager.deprecateProfile(result, unclaimedOrcid, newUserOrcid);
                        notificationManager.sendAutoDeprecateNotification(newUserOrcid, unclaimedOrcid);
                        profileEntityCacheManager.remove(unclaimedOrcid);
                        return newUserOrcid;
                    } else {
                        return createMinimalProfile(registration, usedCaptcha, locale, ip);
                    }
                }
            });
            return orcidId;
        } catch (Exception e) {
            throw new InvalidRequestException("Unable to register user due: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * Creates a minimal record
     * 
     * @param orcidProfile
     *            The record to create
     * @return the new record
     */
    private String createMinimalProfile(Registration registration, boolean usedCaptcha, Locale locale, String ip) {
        Date now = new Date();
        String orcid = orcidGenerationManager.createNewOrcid();
        ProfileEntity newRecord = new ProfileEntity();
        newRecord.setId(orcid);
        newRecord.setOrcidType(OrcidType.USER);
        newRecord.setDateCreated(now);
        newRecord.setLastModified(now);
        newRecord.setSubmissionDate(now);
        newRecord.setClaimed(true);
        newRecord.setEnableDeveloperTools(false);
        newRecord.setRecordLocked(false);
        newRecord.setReviewed(false);
        newRecord.setEnableNotifications(DefaultPreferences.NOTIFICATIONS_ENABLED);
        newRecord.setUsedRecaptchaOnRegistration(usedCaptcha);
        newRecord.setUserLastIp(ip);
        if (PojoUtil.isEmpty(registration.getSendEmailFrequencyDays())) {
            newRecord.setSendEmailFrequencyDays(Float.valueOf(DefaultPreferences.SEND_EMAIL_FREQUENCY_DAYS));
        } else {
            newRecord.setSendEmailFrequencyDays(Float.valueOf(registration.getSendEmailFrequencyDays().getValue()));
        }

        if (registration.getSendMemberUpdateRequests() == null) {
            newRecord.setSendMemberUpdateRequests(DefaultPreferences.SEND_MEMBER_UPDATE_REQUESTS);
        } else {
            newRecord.setSendMemberUpdateRequests(registration.getSendMemberUpdateRequests().getValue());
        }
        newRecord.setCreationMethod(PojoUtil.isEmpty(registration.getCreationType()) ? CreationMethod.DIRECT.value() : registration.getCreationType().getValue());
        newRecord.setSendChangeNotifications(registration.getSendChangeNotifications().getValue());
        newRecord.setSendOrcidNews(registration.getSendOrcidNews().getValue());
        newRecord.setLocale(locale == null ? org.orcid.jaxb.model.common_v2.Locale.EN : org.orcid.jaxb.model.common_v2.Locale.fromValue(locale.toString()));
        // Visibility defaults
        newRecord.setActivitiesVisibilityDefault(Visibility.fromValue(registration.getActivitiesVisibilityDefault().getVisibility().value()));

        // Encrypt the password
        newRecord.setEncryptedPassword(encryptionManager.hashForInternalUse(registration.getPassword().getValue()));

        // Set the email
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setId(registration.getEmail().getValue().trim());
        emailEntity.setProfile(newRecord);
        emailEntity.setPrimary(true);
        emailEntity.setCurrent(true);
        emailEntity.setVerified(false);
        // Email is private by default
        emailEntity.setVisibility(Visibility.PRIVATE);
        emailEntity.setSourceId(orcid);
        Set<EmailEntity> emails = new HashSet<>();
        emails.add(emailEntity);
        newRecord.setEmails(emails);

        // Set the name
        RecordNameEntity recordNameEntity = new RecordNameEntity();
        recordNameEntity.setDateCreated(now);
        recordNameEntity.setLastModified(now);
        recordNameEntity.setProfile(newRecord);
        // Name is public by default
        recordNameEntity.setVisibility(Visibility.PUBLIC);
        if (!PojoUtil.isEmpty(registration.getFamilyNames())) {
            recordNameEntity.setFamilyName(registration.getFamilyNames().getValue().trim());
        }
        if (!PojoUtil.isEmpty(registration.getGivenNames())) {
            recordNameEntity.setGivenNames(registration.getGivenNames().getValue().trim());
        }        
        newRecord.setRecordNameEntity(recordNameEntity);

        // Set authority
        OrcidGrantedAuthority authority = new OrcidGrantedAuthority();
        authority.setProfileEntity(newRecord);
        authority.setAuthority(OrcidWebRole.ROLE_USER.getAuthority());
        Set<OrcidGrantedAuthority> authorities = new HashSet<OrcidGrantedAuthority>(1);
        authorities.add(authority);
        newRecord.setAuthorities(authorities);

        profileDao.persist(newRecord);
        profileDao.flush();
        return newRecord.getId();
    }

    /**
     * Validates if the given email address could be auto deprecated
     * 
     * @param emailAddress
     *            The email we want to check
     */
    private void checkAutoDeprecateIsEnabledForEmail(String emailAddress) throws InvalidRequestException {
        // If the email doesn't exists, just return
        if (!emailManager.emailExists(emailAddress)) {
            return;
        }

        // Check the record is not claimed
        if (profileEntityManager.isProfileClaimedByEmail(emailAddress)) {
            throw new InvalidRequestException("Email " + emailAddress + " already exists and is claimed, so, it can't be used again");
        }

        // Check the auto deprecate is enabled for this email address
        if (!emailManager.isAutoDeprecateEnableForEmail(emailAddress)) {
            throw new InvalidRequestException("Autodeprecate is not enabled for " + emailAddress);
        }
    }

    /**
     * Returns the orcid id associated with an email address
     * 
     * @param emailAddress
     * @return the orcid id associated with the given email address
     */
    private String getOrcidIdFromEmail(String emailAddress) {
        Map<String, String> emailMap = emailManager.findOricdIdsByCommaSeparatedEmails(emailAddress);
        String unclaimedOrcid = emailMap == null ? null : emailMap.get(emailAddress);
        if (PojoUtil.isEmpty(unclaimedOrcid)) {
            throw new InvalidRequestException("Unable to find orcid id for " + emailAddress);
        }
        return unclaimedOrcid;
    }

    @Override
    public boolean passwordIsCommon(String password) {
        return commonPasswords.contains(password);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Building common passwords list...");
        commonPasswords = new ArrayList<>();
        InputStream inputStream = getClass().getResourceAsStream(COMMON_PASSWORDS_FILENAME);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line = reader.readLine();
            while (line != null) {
                commonPasswords.add(line.trim());
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        LOGGER.info("Built list of " + commonPasswords.size() + " common passwords");
    }
}
