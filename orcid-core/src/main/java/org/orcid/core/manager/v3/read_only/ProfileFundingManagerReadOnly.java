package org.orcid.core.manager.v3.read_only;

import java.util.List;

import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Fundings;

public interface ProfileFundingManagerReadOnly {    
    /**
     * Looks for the org defined funding subtypes that matches a given pattern
     * @param subtype pattern to look for
     * @param limit the max number of results to look for
     * @return a list of all org defined funding subtypes that matches the given pattern
     * */
    List<String> getIndexedFundingSubTypes(String subtype, int limit);       
    
    /**
     * Get a funding based on the orcid and funding id
     * @param orcid
     *          The funding owner
     * @param fundingId
     *          The funding id
     * @return the Funding          
     * */
    Funding getFunding(String orcid, Long fundingId);
    
    /**
     * Get a funding summary based on the orcid and funding id
     * @param orcid
     *          The funding owner
     * @param fundingId
     *          The funding id
     * @return the FundingSummary          
     * */
    FundingSummary getSummary(String orcid, Long fundingId);
        
    /**
     * Get the list of fundings summaries that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of fundings that belongs to this user
     * */
    List<FundingSummary> getFundingSummaryList(String userOrcid);
    
    /**
     * Get the list of fundings that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of fundings that belongs to this user
     * */
    List<Funding> getFundingList(String userOrcid);
    
    /**
     * Generate a grouped list of funding with the given list of funding
     * 
     * @param fundings
     *          The list of fundings to group
     * @param justPublic
     *          Specify if we want to group only the public elements in the given list
     * @return Fundings element with the FundingSummary elements grouped                  
     * */
    Fundings groupFundings(List<FundingSummary> fundings, boolean justPublic);
}
