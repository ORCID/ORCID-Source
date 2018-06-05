package org.orcid.core.manager.v3.read_only;

import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources;

public interface ResearchResourceManagerReadOnly {

    /**
     * Get a researchResource based on the orcid and researchResource id
     * 
     * @param orcid
     *            The researchResource owner
     * @param researchResourceId
     *            The researchResource id
     * @return the researchResource
     * */
    ResearchResource getResearchResource(String orcid, Long researchResourceId);

    /**
     * Get a researchResource summary based on the orcid and researchResource id
     * 
     * @param orcid
     *            The researchResource owner
     * @param researchResourceId
     *            The researchResource id
     * @return the researchResourceSummary
     * */
    ResearchResourceSummary getResearchResourceSummary(String orcid, Long researchResourceId);
            
    /**
     * Return the list of peer reviews that belongs to a specific user
     * 
     * @param orcid
     *            the researchResource owner
     * @param lastModified
     * @return a list containing the user peer reviews
     * */
    List<ResearchResource> findResearchResources(String orcid);
    
    /**
     * Get the list of peer reivews that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of peer reviews that belongs to this user
     * */
    List<ResearchResourceSummary> getResearchResourceSummaryList(String orcid);
    
    /**
     * Generate a grouped list of peer reviews with the given list of peer reviews
     * 
     * @param researchResources
     *          The list of peer reviews to group
     * @param justPublic
     *          Specify if we want to group only the public elements in the given list
     * @return researchResources element with the researchResourceSummary elements grouped                  
     * */
    ResearchResources groupResearchResources(List<ResearchResourceSummary> researchResources, boolean justPublic);

}
