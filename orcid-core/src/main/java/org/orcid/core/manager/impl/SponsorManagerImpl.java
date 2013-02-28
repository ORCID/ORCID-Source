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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.SponsorManager;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SponsorManagerImpl implements SponsorManager {

    private ProfileDao profileDao;

    @Resource
    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    @Override
    public Map<String, String> retrieveSponsorsAsMap() {
        List<ProfileEntity> profiles = profileDao.retrieveSelectableSponsors();
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (ProfileEntity profile : profiles) {
            map.put(profile.getId(), profile.getVocativeName());
        }
        return map;
    }

}
