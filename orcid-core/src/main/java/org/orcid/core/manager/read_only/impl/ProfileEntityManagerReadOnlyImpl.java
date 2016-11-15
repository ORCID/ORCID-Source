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
package org.orcid.core.manager.read_only.impl;

import java.util.Date;

import org.orcid.core.manager.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class ProfileEntityManagerReadOnlyImpl implements ProfileEntityManagerReadOnly {

    protected ProfileDao profileDao;       
    protected ProfileLastModifiedAspect profileLastModifiedAspect;        
    
    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }    

    public void setProfileLastModifiedAspect(ProfileLastModifiedAspect profileLastModifiedAspect) {
        this.profileLastModifiedAspect = profileLastModifiedAspect;
    }

    /**
     * Fetch a ProfileEntity from the database Instead of calling this function,
     * use the cache profileEntityCacheManager whenever is possible
     */
    @Override
    public ProfileEntity findByOrcid(String orcid) {
        return profileDao.find(orcid);
    }    
    
    /** 
     * Returns the date cached in the request scope. 
     * 
     */
    @Override
    public Date getLastModified(String orcid) {
        return profileLastModifiedAspect.retrieveLastModifiedDate(orcid);
    }       
}