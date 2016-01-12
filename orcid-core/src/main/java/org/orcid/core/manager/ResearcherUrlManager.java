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

import java.util.List;

import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface ResearcherUrlManager {

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid);        
    
    /**
     * Update the researcher urls associated with a specific account
     * @param orcid
     * @param researcherUrls
     * */
    public boolean updateResearcherUrls(String orcid, ResearcherUrls researcherUrls);
    
    /**
     * Delete a researcher url
     * @param orcid
     * @param id
     * @return true if the researcher url was deleted
     * */
    public boolean deleteResearcherUrl(String orcid, Long id);

    /**
     * Retrieve a researcher url from database
     * @param orcid
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    public ResearcherUrlEntity getResearcherUrl(String orcid, Long id);                
    
    /**
     * Adds a researcher url to a specific profile
     * @param orcid
     * @param url
     * @param urlName
     * @return true if the researcher url was successfully created on database
     * */
    public void addResearcherUrls(String orcid, String url, String urlName);            
    
    /**
     * Return the list of public researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of public researcher urls associated with the orcid profile
     * */
    public org.orcid.jaxb.model.record_rc2.ResearcherUrls getPublicResearcherUrlsV2(String orcid);
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public org.orcid.jaxb.model.record_rc2.ResearcherUrls getResearcherUrlsV2(String orcid);
    
    /**
     * Retrieve a researcher url from database
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    public org.orcid.jaxb.model.record_rc2.ResearcherUrl getResearcherUrlV2(String orcid, long id);
    
    /**
     * Add a new researcher url to a specific profile
     * @param orcid
     * @param researcherUrl
     * @return true if the researcher url was successfully created on database
     * */
    public org.orcid.jaxb.model.record_rc2.ResearcherUrl createResearcherUrlV2(String orcid, org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrl);  
    
    /**
     * Updates an existing researcher url
     * @param orcid
     * @param researcherUrl
     * @return the updated researcher url
     * */
    public org.orcid.jaxb.model.record_rc2.ResearcherUrl updateResearcherUrlV2(String orcid, org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrl);        
}
