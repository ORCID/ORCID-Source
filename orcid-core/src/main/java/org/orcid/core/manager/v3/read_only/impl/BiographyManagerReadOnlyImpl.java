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
package org.orcid.core.manager.v3.read_only.impl;

import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.common.CreatedDate;
import org.orcid.jaxb.model.v3.dev1.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Biography;
import org.orcid.persistence.dao.BiographyDao;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class BiographyManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements BiographyManagerReadOnly {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BiographyManagerReadOnlyImpl.class);
    
    protected BiographyDao biographyDao;

    public void setBiographyDao(BiographyDao biographyDao) {
        this.biographyDao = biographyDao;
    }
    
    @Override
    public Biography getBiography(String orcid) {
        BiographyEntity biographyEntity = null;
        try {
            biographyEntity = biographyDao.getBiography(orcid, getLastModified(orcid));
        } catch(Exception e) {
            LOGGER.warn("Couldn't find biography for " + orcid); 
        }
        if(biographyEntity != null) {
            Biography bio = new Biography();
            bio.setContent(biographyEntity.getBiography());
            bio.setVisibility(Visibility.fromValue(biographyEntity.getVisibility().value()));
            bio.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(biographyEntity.getLastModified())));
            bio.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(biographyEntity.getDateCreated())));
            return bio;
        }         
        return null;
    }
    
    @Override
    public Biography getPublicBiography(String orcid) {
        Biography bio = getBiography(orcid);
        if(bio != null && Visibility.PUBLIC.equals(bio.getVisibility())) {
            return bio;
        }
        return null;
    }

    @Override
    public boolean exists(String orcid) {
        return biographyDao.exists(orcid);
    }    
}
