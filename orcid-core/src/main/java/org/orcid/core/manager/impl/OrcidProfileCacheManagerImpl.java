package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileCacheManager;
import org.orcid.core.manager.OrcidProfileManagerReadOnly;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrcidProfileCacheManagerImpl implements OrcidProfileCacheManager {
    // Default time to live will be one hour
    private static final int DEFAULT_TTL = 3600;

    // Default time to idle will be one hour
    private static final int DEFAULT_TTI = 3600;
    
    private OrcidProfileManagerReadOnly orcidProfileManager;

    @Resource(name = "publicProfileCache")
    private Cache<OrcidCacheKey, OrcidProfile> publicProfileCache;
    
    @Resource(name = "publicBioCache")
    private Cache<OrcidCacheKey, OrcidProfile> publicBioCache;

    @Resource(name = "profileCache")
    private Cache<OrcidCacheKey, OrcidProfile> profileCache;
    
    @Resource(name = "profileBioAndInternalCache")
    private Cache<OrcidCacheKey, OrcidProfile> profileBioAndInternalCache;
    
    private String releaseName = ReleaseNameUtils.getReleaseName();

    private static final Logger LOG = LoggerFactory.getLogger(OrcidProfileCacheManagerImpl.class);
    
    public void setOrcidProfileManager(OrcidProfileManagerReadOnly orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    @Override
    public OrcidProfile retrievePublic(String orcid) {
        OrcidCacheKey key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = publicProfileCache.get(key);
        if (needsFresh(dbDate, op)) {
            op = orcidProfileManager.retrievePublicOrcidProfile(orcid);
            publicProfileCache.put(key, op);
        }
        return op;
    }
    
    @Override
    public OrcidProfile retrievePublicBio(String orcid) {
        OrcidCacheKey key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = publicBioCache.get(key);
        if (needsFresh(dbDate, op)) {
            op = orcidProfileManager.retrievePublicOrcidProfile(orcid, LoadOptions.BIO_ONLY);
            publicBioCache.put(key, op);
        }
        return op;
    }

    @Override
    public OrcidProfile retrieveProfileBioAndInternal(String orcid) {
        OrcidCacheKey key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = profileBioAndInternalCache.get(key);
        if (needsFresh(dbDate, op)) {
            op = orcidProfileManager.retrieveFreshOrcidProfile(orcid, LoadOptions.BIO_AND_INTERNAL_ONLY);
            profileBioAndInternalCache.put(key, op);
        }
        return op;
    }
    
    @Override
    public OrcidProfile retrieve(String orcid) {
        OrcidCacheKey key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = profileCache.get(key);
        if (needsFresh(dbDate, op)) {
            op = orcidProfileManager.retrieveFreshOrcidProfile(orcid, LoadOptions.ALL);
            if (op != null) {
                profileCache.put(key, op);
            }
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
        OrcidCacheKey key = new OrcidCacheKey(orcid, releaseName);
        profileCache.put(key, orcidProfile);
    }

    public void removeAll() {
        profileCache.clear();
    }

    public void remove(String orcid) {
        profileCache.remove(new OrcidCacheKey(orcid, releaseName));
    }

    static public boolean needsFresh(Date dbDate, OrcidProfile orcidProfile) {
        if (orcidProfile == null)
            return true;
        if (dbDate == null) // not sure when this happens?
            return true;
        if (orcidProfile.extractLastModifiedDate().getTime() != dbDate.getTime())
            return true;
        return false;
    }
    
}
