/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import java.util.List;

import org.orcid.jaxb.model.message.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface ProfileKeywordManager {
    /**
     * Return the list of keywords associated to a specific profile
     * @param orcid
     * @return 
     *          the list of keywords associated with the orcid profile
     * */
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid);
    /**
     * Deleted a keyword from database
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully deleted
     * */
    public boolean deleteProfileKeyword(String orcid, String keyword);
    /**
     * Adds a keyword to a specific profile
     * @param orcid
     * @param keyword
     * @return true if the keyword was successfully created on database
     * */
    public void addProfileKeyword(String orcid, String keyword);
    /**
     * Update the list of keywords associated with a specific account
     * @param orcid
     * @param keywords    
     * */
    public void updateProfileKeyword(String orcid, Keywords keywords);
}
