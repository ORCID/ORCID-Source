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

import javax.annotation.Resource;

import org.orcid.core.manager.BiographyManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class BiographyManagerImpl implements BiographyManager {

    @Resource
    private BiographyDao biographyDao;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Override
    public Biography getBiography(String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Biography bio = new Biography();
        bio.setVisibility(Visibility.fromValue(profile.getActivitiesVisibilityDefault() == null ? 
                OrcidVisibilityDefaults.BIOGRAPHY_DEFAULT.getVisibility().value() : profile.getActivitiesVisibilityDefault().value()));
        if(profile.getBiographyEntity() != null) {
            bio.setContent(profile.getBiographyEntity().getBiography());
            if(profile.getBiographyEntity().getVisibility() != null) {
                bio.setVisibility(profile.getBiographyEntity().getVisibility());
            } 
            //This should never be null
            if(profile.getBiographyEntity().getLastModified() != null) {
                bio.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(profile.getBiographyEntity().getLastModified())));
            } else {
                bio.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(profile.getLastModified())));
            }     
        } else {
            bio.setContent(profile.getBiography()); 
            if(profile.getBiographyVisibility() != null) {
                bio.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profile.getBiographyVisibility().value()));
            }
            bio.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(profile.getLastModified())));
        }
        
        return bio;
    }
    
    @Override
    public Biography getPublicBiography(String orcid) {
        Biography bio = getBiography(orcid);
        if(bio != null && org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC.equals(bio.getVisibility())) {
            return bio;
        }
        return null;
    }
    
    @Override
    public boolean updateBiography(String orcid, Biography bio) {
        if (bio == null || PojoUtil.isEmpty(bio.getContent()) || bio.getVisibility() == null) {
            return false;
        }
        return biographyDao.updateBiography(orcid, bio.getContent(), bio.getVisibility());
    }

    @Override
    public void createBiography(String orcid, Biography bio) {
        if (bio == null || PojoUtil.isEmpty(bio.getContent()) || bio.getVisibility() == null) {
            return;
        }
        biographyDao.createBiography(orcid, bio.getContent(), bio.getVisibility());
    }
}
