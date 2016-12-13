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
package org.orcid.core.manager;

import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;

public interface ResearcherUrlManager extends ResearcherUrlManagerReadOnly {
    /**
     * Delete a researcher url
     * @param orcid
     * @param id
     * @param checkSource
     * @return true if the researcher url was deleted
     * */
    boolean deleteResearcherUrl(String orcid, Long id, boolean checkSource);      
    
    /**
     * Add a new researcher url to a specific profile
     * @param orcid
     * @param researcherUrl
     * @return true if the researcher url was successfully created on database
     * */
    ResearcherUrl createResearcherUrl(String orcid, ResearcherUrl researcherUrl, boolean isApiRequest);  
    
    /**
     * Updates an existing researcher url
     * @param orcid
     * @param researcherUrl
     * @return the updated researcher url
     * */
    ResearcherUrl updateResearcherUrl(String orcid, ResearcherUrl researcherUrl, boolean isApiRequest);        
    
    ResearcherUrls updateResearcherUrls(String orcid, ResearcherUrls researcherUrls);
}
