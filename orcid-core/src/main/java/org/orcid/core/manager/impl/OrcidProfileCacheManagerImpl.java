package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.util.TimeUtil;

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
    private Cache publicProfileCache;
    private int publicProfileCacheTTI;
    private int publicProfileCacheTTL;
    
    @Resource(name = "publicBioCache")
    private Cache publicBioCache;
    private int publicBioCacheTTI;
    private int publicBioCacheTTL;

    @Resource(name = "profileCache")
    private Cache profileCache;
    private int profileCacheTTI;
    private int profileCacheTTL;
    
    @Resource(name = "profileBioAndInternalCache")
    private Cache profileBioAndInternalCache;
    private int profileBioAndInternalCacheTTI;
    private int profileBioAndInternalCacheTTL;
    
    private String releaseName = ReleaseNameUtils.getReleaseName();

    private static final Logger LOG = LoggerFactory.getLogger(OrcidProfileCacheManagerImpl.class);
    
    public void setOrcidProfileManager(OrcidProfileManagerReadOnly orcidProfileManager) {
        this.orcidProfileManager = orcidProfileManager;
    }

    @PostConstruct
    private void init() {
        CacheConfiguration config1 = publicProfileCache.getCacheConfiguration();
        publicProfileCacheTTI = config1.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config1.getTimeToIdleSeconds()) : DEFAULT_TTI;
        publicProfileCacheTTL = config1.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config1.getTimeToLiveSeconds()) : DEFAULT_TTL;

        CacheConfiguration config2 = publicBioCache.getCacheConfiguration();
        publicBioCacheTTI = config2.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config2.getTimeToIdleSeconds()) : DEFAULT_TTI;
        publicBioCacheTTL = config2.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config2.getTimeToLiveSeconds()) : DEFAULT_TTL;

        CacheConfiguration config3 = profileCache.getCacheConfiguration();
        profileCacheTTI = config3.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config3.getTimeToIdleSeconds()) : DEFAULT_TTI;
        profileCacheTTL = config3.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config3.getTimeToLiveSeconds()) : DEFAULT_TTL;

        CacheConfiguration config4 = profileBioAndInternalCache.getCacheConfiguration();
        profileBioAndInternalCacheTTI = config4.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config4.getTimeToIdleSeconds()) : DEFAULT_TTI;
        profileBioAndInternalCacheTTL = config4.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config4.getTimeToLiveSeconds()) : DEFAULT_TTL;
    }
    
    @Override
    public OrcidProfile retrievePublic(String orcid) {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = null;
        try {
            publicProfileCache.acquireReadLockOnKey(key);
            op = toOrcidProfile(publicProfileCache.get(key));
        } finally {
            publicProfileCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, op))
            try {
                publicProfileCache.acquireWriteLockOnKey(key);
                op = toOrcidProfile(publicProfileCache.get(orcid));
                if (needsFresh(dbDate, op)) {
                    op = orcidProfileManager.retrievePublicOrcidProfile(orcid);
                    publicProfileCache.put(createElement(key, op, publicProfileCache));
                }
            } finally {
                publicProfileCache.releaseWriteLockOnKey(key);
            }
        return op;
    }
    
    @Override
    public OrcidProfile retrievePublicBio(String orcid) {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = null;
        try {
            publicBioCache.acquireReadLockOnKey(key);
            op = toOrcidProfile(publicBioCache.get(key));
        } finally {
            publicBioCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, op))
            try {
                publicBioCache.acquireWriteLockOnKey(key);
                op = toOrcidProfile(publicBioCache.get(orcid));
                if (needsFresh(dbDate, op)) {
                    op = orcidProfileManager.retrievePublicOrcidProfile(orcid, LoadOptions.BIO_ONLY);
                    publicBioCache.put(createElement(key, op, publicBioCache));
                }
            } finally {
                publicBioCache.releaseWriteLockOnKey(key);
            }
        return op;
    }

    @Override
    public OrcidProfile retrieveProfileBioAndInternal(String orcid) {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = null;
        try {
            profileBioAndInternalCache.acquireReadLockOnKey(key);
            op = toOrcidProfile(profileBioAndInternalCache.get(key));
        } finally {
            profileBioAndInternalCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, op))
            try {
                profileBioAndInternalCache.acquireWriteLockOnKey(key);
                op = toOrcidProfile(profileBioAndInternalCache.get(orcid));
                if (needsFresh(dbDate, op)) {
                    op = orcidProfileManager.retrieveFreshOrcidProfile(orcid, LoadOptions.BIO_AND_INTERNAL_ONLY);
                    profileBioAndInternalCache.put(createElement(key, op, profileBioAndInternalCache));
                }
            } finally {
                profileBioAndInternalCache.releaseWriteLockOnKey(key);
            }
        return op;
    }
    
    @Override
    public OrcidProfile retrieve(String orcid) {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = retrieveLastModifiedDate(orcid);
        OrcidProfile op = null;
        try {
            profileCache.acquireReadLockOnKey(key);
            op = toOrcidProfile(profileCache.get(key));
        } finally {
            profileCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, op))
            try {
                profileCache.acquireWriteLockOnKey(key);
                    op = toOrcidProfile(profileCache.get(orcid));
                    if (needsFresh(dbDate, op)) {
                        op = orcidProfileManager.retrieveFreshOrcidProfile(orcid, LoadOptions.ALL);
                        profileCache.put(createElement(key, op, profileCache));
                    }
            } finally {
                profileCache.releaseWriteLockOnKey(key);
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
        Object key = new OrcidCacheKey(orcid, releaseName);
        try {
            profileCache.acquireWriteLockOnKey(key);
            profileCache.put(createElement(key, orcidProfile, profileCache));
        } finally {
            profileCache.releaseWriteLockOnKey(key);
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
        if (dbDate == null) // not sure when this happens?
            return true;
        if (orcidProfile.extractLastModifiedDate().getTime() != dbDate.getTime())
            return true;
        return false;
    }
    
    @Override
    public void evictExpiredElements() {   
        LOG.info("Elements on profileCache before eviction: " + profileCache.getSize());
        profileCache.evictExpiredElements();
        profileCache.flush();
        LOG.info("Elements on profileCache after eviction: " + profileCache.getSize());
    }
    
    private Element createElement(Object key, Object element, Cache cache) {
        if(cache.equals(publicProfileCache)) {  
            return new Element(key, element, publicProfileCacheTTI, publicProfileCacheTTL);
        } else if (cache.equals(publicBioCache)) {
            return new Element(key, element, publicBioCacheTTI, publicBioCacheTTL);
        } else if (cache.equals(profileCache)) {
            return new Element(key, element, profileCacheTTI, profileCacheTTL);
        } else if (cache.equals(profileBioAndInternalCache)) {
            return new Element(key, element, profileBioAndInternalCacheTTI, profileBioAndInternalCacheTTL);
        } else {
            return new Element(key, element, DEFAULT_TTI, DEFAULT_TTL);
        }                
    }
}
