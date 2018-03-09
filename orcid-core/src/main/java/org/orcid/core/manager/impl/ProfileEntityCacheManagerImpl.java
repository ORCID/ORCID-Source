package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.ReleaseNameUtils;
import org.springframework.transaction.annotation.Transactional;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class ProfileEntityCacheManagerImpl implements ProfileEntityCacheManager {

    @Resource(name = "profileEntityCache")
    private Cache profileCache;
    
    private ProfileEntityManagerReadOnly profileEntityManager;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    public void setProfileEntityManager(ProfileEntityManagerReadOnly profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    @Override
    @Transactional
    public ProfileEntity retrieve(String orcid) throws IllegalArgumentException {
        Object key = new OrcidCacheKey(orcid, releaseName);
        Date dbDate = profileEntityManager.getLastModifiedDate(orcid);
        ProfileEntity profile = null;
        try {
            profileCache.acquireReadLockOnKey(key);
            profile = toProfileEntity(profileCache.get(key));
        } finally {
            profileCache.releaseReadLockOnKey(key);
        }
        if (needsFresh(dbDate, profile))
            try {
                profileCache.acquireWriteLockOnKey(key);
                profile = toProfileEntity(profileCache.get(key));
                if (needsFresh(dbDate, profile)) {
                    profile = profileEntityManager.findByOrcid(orcid);
                    if (profile == null)
                        throw new IllegalArgumentException("Invalid orcid " + orcid);
                    if (profile.getGivenPermissionBy() != null) {
                        profile.getGivenPermissionBy().size();
                    }
                    if (profile.getGivenPermissionTo() != null) {
                        profile.getGivenPermissionTo().size();
                    }
                    profileCache.put(new Element(key, profile));
                }                
            } finally {
                profileCache.releaseWriteLockOnKey(key);
            }
        return profile;
    }

    @Override
    public void removeAll() {
        profileCache.removeAll();
    }

    @Override
    public void remove(String orcid) {
        profileCache.remove(new OrcidCacheKey(orcid, releaseName));
    }

    static public ProfileEntity toProfileEntity(Element element) {
        return (ProfileEntity) (element != null ? element.getObjectValue() : null);
    }

    static public boolean needsFresh(Date dbDate, ProfileEntity profileEntity) {
        if (profileEntity == null)
            return true;
        if (dbDate == null) // not sure when this happens?
            return true;
        if (profileEntity.getLastModified().getTime() != dbDate.getTime())
            return true;
        return false;
    }
}
