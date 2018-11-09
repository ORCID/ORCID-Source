package org.orcid.core.manager.v3.read_only.impl;

import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class ProfileEntityManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileEntityManagerReadOnly { 

    protected ProfileDao profileDao;       
    
    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }    

    /**
     * Fetch a ProfileEntity from the database Instead of calling this function,
     * use the cache profileEntityCacheManager whenever is possible
     */
    @Override
    public ProfileEntity findByOrcid(String orcid) {
        return profileDao.find(orcid);
    }

    @Override
    public Boolean isLocked(String orcid) {
        return profileDao.isLocked(orcid);
    }       
}