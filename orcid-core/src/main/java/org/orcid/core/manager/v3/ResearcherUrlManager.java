package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;

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
    
    /**
     * Removes all researcher urls that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all researcher urls will be
     *            removed.
     */
    void removeAllResearcherUrls(String orcid);
}
