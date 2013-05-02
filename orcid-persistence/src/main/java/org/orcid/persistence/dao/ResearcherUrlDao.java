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
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface ResearcherUrlDao {

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid);

    /**
     * Deleted a researcher url from database
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    public boolean deleteResearcherUrl(long id);

    /**
     * Retrieve a researcher url from database
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    public ResearcherUrlEntity getResearcherUrl(long id);

    /**
     * Adds a researcher url to a specific profile
     * @param orcid
     * @param url
     * @param urlName
     * @return true if the researcher url was successfully created on database
     * */
    public boolean addResearcherUrls(String orcid, String url, String urlName);
}
