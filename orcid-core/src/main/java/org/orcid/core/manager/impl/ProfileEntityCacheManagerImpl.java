package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.transaction.annotation.Transactional;

public class ProfileEntityCacheManagerImpl implements ProfileEntityCacheManager {

    @Resource(name = "profileEntityGenericCacheManager")
    private GenericCacheManager<OrcidString, ProfileEntity> profileEntityGenericCacheManager;
    
    @Override
    @Transactional
    public ProfileEntity retrieve(String orcid) throws IllegalArgumentException {
        ProfileEntity profileEntity = profileEntityGenericCacheManager.retrieve(new OrcidString(orcid));
        if (profileEntity == null) {
            throw new IllegalArgumentException("Invalid orcid: " + orcid);
        }
        return profileEntity;
    }

    @Override
    public void removeAll() {
        // TODO: Implement this
    }

    @Override
    public void remove(String orcid) {
        profileEntityGenericCacheManager.remove(new OrcidString(orcid));
    }

}
