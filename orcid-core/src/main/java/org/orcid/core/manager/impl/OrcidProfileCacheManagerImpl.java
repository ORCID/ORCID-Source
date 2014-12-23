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

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrcidProfileCacheManagerImpl implements OrcidProfileCacheManager {

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource(name = "publicProfileCache")
    private Cache publicProfileCache;

    LockerObjectsManager pubLocks = new LockerObjectsManager();

    @Resource(name = "profileCache")
    private Cache profileCache;
    LockerObjectsManager lockers = new LockerObjectsManager();

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private static final Logger LOG = LoggerFactory.getLogger(OrcidProfileCacheManagerImpl.class);

    @Override
    public OrcidProfile retrievePublic(String orcid) {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = toOrcidProfile(publicProfileCache.get(key));
        if (needsFresh(dbDate, op))
            try {
                synchronized (pubLocks.obtainLock(orcid)) {
                    op = toOrcidProfile(publicProfileCache.get(orcid));
                    if (needsFresh(dbDate, op)) {
                        op = orcidProfileManager.retrieveClaimedOrcidProfile(orcid);
                        publicProfileCache.put(new Element(key, op));
                    }
                }
            } finally {
                pubLocks.releaseLock(orcid);
            }
        return op;
    }

    @Override
    public OrcidProfile retrieve(String orcid) {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = toOrcidProfile(profileCache.get(key));
        if (needsFresh(dbDate, op))
            try {
                synchronized (lockers.obtainLock(orcid)) {
                    op = toOrcidProfile(profileCache.get(orcid));
                    if (needsFresh(dbDate, op)) {
                        op = orcidProfileManager.retrieveFreshOrcidProfile(orcid, LoadOptions.ALL);
                        profileCache.put(new Element(key, op));
                    }
                }
            } finally {
                lockers.releaseLock(orcid);
            }
        return op;
    }
    
    private Date retrieveLastModifiedDate(String orcid) {
        Date date = null;
        try {
            date = orcidProfileManager.retrieveLastModifiedDate(orcid);
        } catch (javax.persistence.NoResultException e) {
             LOG.debug("Missing retrieveLastModifiedDate orcid:" + orcid);   
        }
        return date;
    }

    public void put(OrcidProfile orcidProfile) {
        put(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile);
    }

    public void put(String orcid, OrcidProfile orcidProfile) {
        try {
            synchronized (lockers.obtainLock(orcid)) {
                profileCache.put(new Element(new OrcidCacheKey(orcid, releaseName), orcidProfile));
            }
        } finally {
            lockers.releaseLock(orcid);
        }
    }

    public void removeAll() {
        profileCache.removeAll();
    }

    public void remove(String orcid) {
        profileCache.remove(new OrcidCacheKey(orcid, releaseName));
    }

    static public OrcidProfile toOrcidProfile(Element element) {
        return (OrcidProfile) (element != null ? element.getObjectValue() : null);
    }

    static public boolean needsFresh(Date dbDate, OrcidProfile orcidProfile) {
        if (orcidProfile == null)
            return true;
        if (!orcidProfile.extractLastModifiedDate().equals(dbDate))
            return true;
        if (dbDate == null) // not sure when this happens?
            return true;
        return false;
    }

}
