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

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RegistrationManagerImpl implements RegistrationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationManagerImpl.class);

    @Resource
    private ProfileDao profileDao;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    private EncryptionManager encryptionManager;

    private NotificationManager notificationManager;
    
    @Resource
    private EmailManager emailManager;

    @Resource
    private PasswordGenerationManager passwordResetManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private AdminManager adminManager;
    
    @Resource
    private TransactionTemplate transactionTemplate;

    public void setOrcidProfileManager(OrcidProfileManager orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

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
    public OrcidProfile createMinimalRegistration(OrcidProfile orcidProfile, boolean usedCaptcha) {
        String emailAddress = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        try {
            OrcidProfile profile = transactionTemplate.execute(new TransactionCallback<OrcidProfile>() {
                public OrcidProfile doInTransaction(TransactionStatus status) {
                    if (emailManager.emailExists(emailAddress)) {
                        checkAutoDeprecateIsEnabledForEmail(emailAddress);
                        String unclaimedOrcid = getOrcidIdFromEmail(emailAddress);
                        emailManager.removeEmail(unclaimedOrcid, emailAddress, true);
                        OrcidProfile minimalProfile = createMinimalProfile(orcidProfile, usedCaptcha);                    
                        String newUserOrcid = minimalProfile.getOrcidIdentifier().getPath();
                        ProfileDeprecationRequest result = new ProfileDeprecationRequest();  
                        adminManager.deprecateProfile(result, unclaimedOrcid, newUserOrcid);                    
                        notificationManager.sendAutoDeprecateNotification(minimalProfile, unclaimedOrcid);
                        profileEntityCacheManager.remove(unclaimedOrcid);
                        return minimalProfile;
                    } else {
                        return createMinimalProfile(orcidProfile, usedCaptcha);
                    }
                } 
            });
            return profile;
        } catch(Exception e) {
            throw new InvalidRequestException("Unable to register user due: " + e.getMessage(), e.getCause());
        }        
    }

    /**
     * Creates a minimal record
     * 
     * @param orcidProfile
     *          The record to create
     * @return the new record         
     */
    private OrcidProfile createMinimalProfile(OrcidProfile orcidProfile, boolean usedCaptcha) {
        OrcidProfile minimalProfile = orcidProfileManager.createOrcidProfile(orcidProfile, false, usedCaptcha);
        // Set source to the new email
        String sourceId = minimalProfile.getOrcidIdentifier().getPath();
        List<Email> emails = minimalProfile.getOrcidBio().getContactDetails().getEmail();
        for (Email email : emails)
            emailManager.addSourceToEmail(email.getValue(), sourceId);
        LOGGER.debug("Created minimal orcid and assigned id of {}", orcidProfile.getOrcidIdentifier().getPath());
        return minimalProfile;
    }

    /**
     * Validates if the given email address could be auto deprecated
     * 
     * @param emailAddress
     *          The email we want to check
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
}
