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

import javax.annotation.Resource;

import org.orcid.core.manager.PreferenceManager;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.SendEmailFrequency;
import org.orcid.persistence.dao.ProfileDao;

public class PreferenceManagerImpl implements PreferenceManager {

    @Resource
    private ProfileDao profileDao;

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

        return profileDao.updateDefaultVisibility(orcid, newValue);
    }

}
