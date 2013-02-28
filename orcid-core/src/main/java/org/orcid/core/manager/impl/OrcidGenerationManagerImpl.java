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

import java.util.Random;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.crypto.OrcidCheckDigitGenerator;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Will Simpson (will) Date: 15/02/2012
 */
public class OrcidGenerationManagerImpl implements OrcidGenerationManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidGenerationManager.class);

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource(name = "recentOrcidCache")
    private Cache recentOrcidCache;

    @Override
    public String createNewOrcid() {
        String orcid = getNextOrcid();
        while (isInRecentOrcidCache(orcid) || profileEntityManager.orcidExists(orcid)) {
            orcid = getNextOrcid();
        }
        recentOrcidCache.put(new Element(orcid, orcid));
        return orcid;
    }

    private String getNextOrcid() {
        String baseOrcid = StringUtils.leftPad(String.valueOf(getRandomNumber()), 15, '0');
        String checkDigit = OrcidCheckDigitGenerator.generateCheckDigit(baseOrcid);
        return formatOrcid(baseOrcid + checkDigit);
    }

    private boolean isInRecentOrcidCache(String formattedOrcid) {
        LOGGER.debug("Recent ORCID cache size: {}", recentOrcidCache.getSize());
        Element alreadyUsed = recentOrcidCache.get(formattedOrcid);
        if (alreadyUsed != null) {
            LOGGER.debug("Same ORCID randomly generated a few moments ago: {}", formattedOrcid);
            return true;
        }
        return false;
    }

    private long getRandomNumber() {
        Random random = new Random();
        // XXX Need to test edge cases
        return (long) (ORCID_BASE_MIN + (random.nextDouble() * (ORCID_BASE_MAX - ORCID_BASE_MIN + 1)));
    }

    private String formatOrcid(String orcid) {
        return orcid.replaceAll("(.{4})(?=.)", "$1-");
    }

}
