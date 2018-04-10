package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.SendEmailFrequency;
import org.orcid.persistence.dao.ProfileDao;

public class PreferenceManagerImpl implements PreferenceManager {

    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

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

        return profileDao.updateNotificationsPreferences(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendOrcidNews, sendMemberUpdateRequests);
    }

    @Override
    public boolean updateDefaultVisibility(String orcid, Visibility newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException();
        }

        boolean updated = profileDao.updateDefaultVisibility(orcid, newValue);
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
