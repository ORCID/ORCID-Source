package org.orcid.core.manager.impl;

import jakarta.annotation.Resource;

import org.orcid.core.manager.PreferenceManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.ProfileDao;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class PreferenceManagerImpl implements PreferenceManager {

    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    
    @Override
    @Transactional
    public boolean updateDefaultVisibility(String orcid, Visibility newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException();
        }

        boolean updated = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                return profileDao.updateDefaultVisibility(orcid, newValue.name());
            }
        });
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
