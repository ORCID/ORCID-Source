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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.OtherNameEntity;

public interface OtherNameDao extends GenericDao<OtherNameEntity, Long> {

    /**
     * Get other names for an specific orcid account
     * @param orcid          
     * @return
     * The list of other names related with the specified orcid profile
     * */
    List<OtherNameEntity> getOtherNames(String orcid, long lastModified);

    /**
     * Get other names for an specific orcid account and with the specific visibility
     * @param orcid          
     * @return
     * The list of other names related with the specified orcid profile
     * */
    List<OtherNameEntity> getOtherNames(String orcid, org.orcid.jaxb.model.common_rc4.Visibility visibility);

    
    /**
     * Update other name entity with new values
     * @param otherName
     * @return
     *          true if the other name was sucessfully updated, false otherwise
     * */
    boolean updateOtherName(OtherNameEntity otherName);

    /**
     * Create other name for the specified account
     * @param orcid
     * @param displayName
     * @return
     *          true if the other name was successfully created, false otherwise 
     * */
    boolean addOtherName(String orcid, String displayName);

    /**
     * Delete other name from database
     * @param otherName
     * @return 
     *          true if the other name was successfully deleted, false otherwise
     * */
    boolean deleteOtherName(OtherNameEntity otherName);
    
    OtherNameEntity getOtherName(String orcid, Long putCode);
}
