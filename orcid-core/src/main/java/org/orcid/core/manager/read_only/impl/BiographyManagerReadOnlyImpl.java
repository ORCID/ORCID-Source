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

import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.jaxb.model.common_v2.CreatedDate;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class BiographyManagerReadOnlyImpl implements BiographyManagerReadOnly {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BiographyManagerReadOnlyImpl.class);
    
    protected BiographyDao biographyDao;

    public void setBiographyDao(BiographyDao biographyDao) {
        this.biographyDao = biographyDao;
    }
    
    @Override
    @Cacheable(value = "biography", key = "#orcid.concat('-').concat(#lastModified)")
    public Biography getBiography(String orcid, long lastModified) {
        Biography bio = new Biography();
        BiographyEntity biographyEntity = null;
        try {
            biographyEntity = biographyDao.getBiography(orcid);
        } catch(Exception e) {
            LOGGER.warn("Couldn't find biography for " + orcid); 
        }
        if(biographyEntity != null) {
            bio.setContent(biographyEntity.getBiography());
            bio.setVisibility(biographyEntity.getVisibility());
            bio.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(biographyEntity.getLastModified())));
            bio.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(biographyEntity.getDateCreated())));            
        }         
        return bio;
    }
    
    @Override
    @Cacheable(value = "public-biography", key = "#orcid.concat('-').concat(#lastModified)")
    public Biography getPublicBiography(String orcid, long lastModified) {
        Biography bio = getBiography(orcid, lastModified);
        if(bio != null && org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.equals(bio.getVisibility())) {
            return bio;
        }
        return new Biography();
    }

    @Override
    public boolean exists(String orcid) {
        return biographyDao.exists(orcid);
    }    
}
