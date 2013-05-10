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

import java.net.URI;
import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.utils.VerifyRegistrationToken;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.HearAboutEntity;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class RegistrationManagerImpl implements RegistrationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationManagerImpl.class);

    public static final Counter REGISTRATIONS_VERIFIED_COUNTER = Metrics.newCounter(RegistrationManagerImpl.class, "orcid-registrations-completed");

    @Resource(name = "hearAboutDao")
    private GenericDao<HearAboutEntity, Integer> hearAboutDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    private EncryptionManager encryptionManager;

    private NotificationManager notificationManager;

    @Resource
    private PasswordGenerationManager passwordResetManager;

    public void setHearAboutDao(GenericDao<HearAboutEntity, Integer> hearAboutDao) {
        this.hearAboutDao = hearAboutDao;
    }

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
    public void verifyRegistration(OrcidProfile orcidProfile, URI baseUri) {
        LOGGER.debug("Verifying registration: {}", orcidProfile.getOrcid().getValue());
        OrcidHistory orcidHistory = orcidProfile.getOrcidHistory();
        if (orcidHistory == null) {
            orcidHistory = new OrcidHistory();
        }
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCompletionDate(new CompletionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        orcidHistory.setCreationMethod(CreationMethod.WEBSITE);
        orcidProfile.setOrcidHistory(orcidHistory);
        orcidProfileManager.updateOrcidHistory(orcidProfile);
        REGISTRATIONS_VERIFIED_COUNTER.inc();
    }

    @Override
    public void resetUserPassword(OrcidProfile orcidProfile, URI baseUri) {
        LOGGER.debug("Resetting password for Orcid: {}", orcidProfile.getOrcid().getValue());
        if (!orcidProfile.getOrcidHistory().isClaimed()) {
            LOGGER.debug("Profile is not claimed so re-sending claim email instead of password reset: {}", orcidProfile.getOrcid().getValue());
            notificationManager.sendApiRecordCreationEmail(orcidProfile);
        } else {
            notificationManager.sendPasswordResetEmail(orcidProfile, baseUri);
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
    public OrcidProfile createMinimalRegistration(OrcidProfile orcidProfile) {
        OrcidProfile minimalProfile = orcidProfileManager.createOrcidProfile(orcidProfile);
        LOGGER.debug("Created minimal orcid and assigned id of {}", orcidProfile.getOrcid().getValue());
        return minimalProfile;
    }

}
