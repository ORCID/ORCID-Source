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

import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface ProfileKeywordDao extends GenericDao<ProfileKeywordEntity, Long> {

    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    List<ProfileKeywordEntity> getProfileKeywors(String orcid, long lastModified);
    
    List<ProfileKeywordEntity> getProfileKeywors(String orcid, org.orcid.jaxb.model.common_rc2.Visibility visibility);

    /**
     * Deleted a keyword from database
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     * */
    boolean deleteProfileKeyword(String orcid, String keyword);
    
    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    boolean addProfileKeyword(String orcid, String keyword, String sourceId, String clientSourceId, org.orcid.jaxb.model.common_rc2.Visibility visibility);
    
    boolean deleteProfileKeyword(ProfileKeywordEntity entity);
    
    ProfileKeywordEntity getProfileKeyword(String orcid, Long putCode);
}
