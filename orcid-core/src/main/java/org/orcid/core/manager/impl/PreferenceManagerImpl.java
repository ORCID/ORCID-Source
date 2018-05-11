package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferenceManagerImpl implements PreferenceManager {

    private static final Logger LOG = LoggerFactory.getLogger(PreferenceManagerImpl.class);
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

    @Resource
    private EmailFrequencyDao emailFrequencyDao;
    
    @Override
    public boolean updateEmailFrequencyDays(String orcid, SendEmailFrequency newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException();
        }

        return profileDao.updateSendEmailFrequencyDays(orcid, Float.valueOf(newValue.value()));
    }

    @Override
    public boolean updateNotifications(String orcid, Boolean sendChangeNotifications, Boolean sendAdministrativeChangeNotifications, Boolean sendOrcidNews,
            Boolean sendMemberUpdateRequests) {
        if (sendChangeNotifications == null || sendAdministrativeChangeNotifications == null || sendOrcidNews == null || sendMemberUpdateRequests == null) {
            throw new IllegalArgumentException();
        }

        boolean updated = profileDao.updateNotificationsPreferences(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendOrcidNews,
                sendMemberUpdateRequests);

        // Update the email_frequency table
        if (updated) {
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
            Float emailFrequency = profileEntity.getSendEmailFrequencyDays();
            SendEmailFrequency sendEmailFrequency = SendEmailFrequency.fromValue(emailFrequency);
            Date now = new Date();
            EmailFrequencyEntity entity = null;
            try {
                entity = emailFrequencyDao.findByOrcid(orcid);
            } catch (Exception e) {
                LOG.warn("Couldn't find email_frequency for " + orcid);
            }
            if (entity != null) {
                if (sendAdministrativeChangeNotifications) {
                    emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, sendEmailFrequency);
                } else {
                    emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, SendEmailFrequency.NEVER);
                }

                if (sendChangeNotifications) {
                    emailFrequencyDao.updateSendChangeNotifications(orcid, sendEmailFrequency);
                } else {
                    emailFrequencyDao.updateSendChangeNotifications(orcid, SendEmailFrequency.NEVER);
                }

                if (sendMemberUpdateRequests) {
                    emailFrequencyDao.updateSendMemberUpdateRequests(orcid, sendEmailFrequency);
                } else {
                    emailFrequencyDao.updateSendMemberUpdateRequests(orcid, SendEmailFrequency.NEVER);
                }

                if (profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews() == true && !SendEmailFrequency.NEVER.equals(sendEmailFrequency)) {
                    emailFrequencyDao.updateSendQuarterlyTips(orcid, true);
                } else {
                    emailFrequencyDao.updateSendQuarterlyTips(orcid, false);
                }

            } else {
                entity = new EmailFrequencyEntity();
                entity.setId(UUID.randomUUID().toString());
                entity.setDateCreated(now);
                entity.setLastModified(now);
                entity.setOrcid(orcid);
                if (sendAdministrativeChangeNotifications) {
                    entity.setSendAdministrativeChangeNotifications(emailFrequency);
                } else {
                    entity.setSendAdministrativeChangeNotifications(SendEmailFrequency.NEVER.floatValue());
                }

                if (sendChangeNotifications) {
                    entity.setSendChangeNotifications(emailFrequency);
                } else {
                    entity.setSendChangeNotifications(SendEmailFrequency.NEVER.floatValue());
                }

                if (sendMemberUpdateRequests) {
                    entity.setSendMemberUpdateRequests(emailFrequency);
                } else {
                    entity.setSendMemberUpdateRequests(SendEmailFrequency.NEVER.floatValue());
                }

                if (profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews() == true && !SendEmailFrequency.NEVER.equals(sendEmailFrequency)) {
                    entity.setSendQuarterlyTips(true);
                } else {
                    entity.setSendQuarterlyTips(false);
                }
                emailFrequencyDao.persist(entity);
            }
        }

        return updated;
    }

    @Override
    public boolean updateDefaultVisibility(String orcid, Visibility newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException();
        }

        boolean updated = profileDao.updateDefaultVisibility(orcid, newValue.name());
        if (updated) {
            if (Visibility.LIMITED.equals(newValue)) {
                profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_LIMITED, orcid);
            }
            if (Visibility.PRIVATE.equals(newValue)) {
                profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PRIVATE, orcid);
            }
            if (Visibility.PUBLIC.equals(newValue)) {
                profileHistoryEventManager.recordEvent(ProfileHistoryEventType.SET_DEFAULT_VIS_TO_PUBLIC, orcid);
            }
        }
        return updated;
    }

}
