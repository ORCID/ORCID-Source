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

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrcidProfileCacheManagerImpl implements OrcidProfileCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(OrcidProfileCacheManagerImpl.class);

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ProfileDao profileDao;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private ConcurrentMap<String, Object> readLocks = new ConcurrentHashMap<>();

    @Resource(name = "publicProfileCache")
    private Cache publicProfileCache;

    @Override
    public OrcidProfile retrievePublicOrcidProfile(String orcid) {

        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = orcidProfileManager.retrieveLastModifiedDate(orcid);
        OrcidProfile op = toOrcidProfile(publicProfileCache.get(key));
        if (needsFresh(dbDate, op))
            try {
                synchronized (obtainPublicReadLock(orcid)) {
                    op = toOrcidProfile(publicProfileCache.get(orcid));
                    if (needsFresh(dbDate, op)) {
                        op = orcidProfileManager.retrieveClaimedOrcidProfile(orcid);
                        publicProfileCache.put(new Element(key, op));
                    }
                }
            } finally {
                releasePublicReadLock(orcid);
            }
        return op;
    }

    private Object obtainPublicReadLock(String orcid) {
        LOG.debug("About to obtain read lock: " + orcid);
        Object newLock = new Object();
        Object existingLock = readLocks.putIfAbsent(orcid, newLock);
        return existingLock == null ? newLock : existingLock;
    }

    static public OrcidProfile toOrcidProfile(Element element) {
        return (OrcidProfile) (element != null ? element.getObjectValue() : null);
    }

    private void releasePublicReadLock(String orcid) {
        LOG.debug("About to release read lock: " + orcid);
        readLocks.remove(orcid);
    }

    static public boolean needsFresh(Date dbDate, OrcidProfile orcidProfile) {
        return orcidProfile == null || !extractLastModifiedDateFromObject(orcidProfile).equals(dbDate);
    }

    static public Date extractLastModifiedDateFromObject(OrcidProfile orcidProfile) {
        if (orcidProfile == null)
            return null;
        OrcidHistory orcidHistory = orcidProfile.getOrcidHistory();
        if (orcidHistory == null)
            return null;
        LastModifiedDate lastModifiedDate = orcidHistory.getLastModifiedDate();
        if (lastModifiedDate == null)
            return null;
        return lastModifiedDate.getValue().toGregorianCalendar().getTime();
    }

}
