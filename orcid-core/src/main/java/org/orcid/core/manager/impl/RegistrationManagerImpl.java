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

import javax.annotation.Resource;

import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


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
        OrcidProfile minimalProfile = orcidProfileManager.createOrcidProfile(orcidProfile, false, usedCaptcha);
        //Set source to the new email
        String sourceId = minimalProfile.getOrcidIdentifier().getPath();
        List<Email> emails = minimalProfile.getOrcidBio().getContactDetails().getEmail();
        for(Email email : emails)
            emailManager.addSourceToEmail(email.getValue(), sourceId);
        LOGGER.debug("Created minimal orcid and assigned id of {}", orcidProfile.getOrcidIdentifier().getPath());
        return minimalProfile;
    }

}
