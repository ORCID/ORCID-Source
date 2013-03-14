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

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.orcid.core.manager.HearAboutManager;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.HearAboutEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author Will Simpson
 * 
 */
public class HearAboutManagerImpl implements HearAboutManager {

    @Resource(name = "hearAboutDao")
    private GenericDao<HearAboutEntity, Integer> hearAboutDao;

    @Override
    public Map<String, String> retrieveHearAboutsAsMap() {
        Map<String, String> hearAbouts = new TreeMap<String, String>();
        for (HearAboutEntity hearAbout : hearAboutDao.getAll()) {
            hearAbouts.put(String.valueOf(hearAbout.getId()), hearAbout.getName());
        }
        return hearAbouts;
    }

    public void setHearAboutDao(GenericDao<HearAboutEntity, Integer> hearAboutDao) {
        this.hearAboutDao = hearAboutDao;
    }

}
