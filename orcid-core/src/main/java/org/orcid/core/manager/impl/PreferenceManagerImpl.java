package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.ProfileDao;

public class PreferenceManagerImpl implements PreferenceManager {

    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

    
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
