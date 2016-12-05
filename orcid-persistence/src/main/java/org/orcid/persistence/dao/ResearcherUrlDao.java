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

import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface ResearcherUrlDao extends GenericDao<ResearcherUrlEntity, Long> {

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, long lastModified);

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @param visibility
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, Visibility visibility);
    
    /**
     * Deleted a researcher url from database
     * @param orcid
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    public boolean deleteResearcherUrl(String orcid, long id);

    /**
     * Retrieve a researcher url from database
     * @param orcid
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    public ResearcherUrlEntity getResearcherUrl(String orcid, Long id);
    
    /**
     * Updates an existing researcher url
     * @param id
     * @param newUrl
     * @return true if the researcher url was updated
     * */
    public boolean updateResearcherUrl(long id, String newUrl);
}
