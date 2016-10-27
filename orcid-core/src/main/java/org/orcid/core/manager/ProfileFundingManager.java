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

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public interface ProfileFundingManager {

    void setSourceManager(SourceManager sourceManager);
    
    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    boolean removeProfileFunding(String clientOrcid, Long profileFundingId);

    /**
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingId
     *            The id of the profile funding that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationship was updated
     * */
    boolean updateProfileFundingVisibility(String clientOrcid, Long profileFundingId, Visibility visibility);

    /**
     * Updates an existing profile funding relationship between an organization and a
     * profile.
     * 
     * @param updatedProfileFundingEntity
     *            The object to be persisted
     * @return the updated profileFundingEntity
     * */
    ProfileFundingEntity updateProfileFunding(ProfileFundingEntity updatedProfileFundingEntity);
    
    /**
     * Creates a new profile funding relationship between an organization and a
     * profile.
     * 
     * @param newProfileFundingEntity
     *            The object to be persisted
     * @return the created newProfileFundingEntity with the id assigned on
     *         database
     * */
    ProfileFundingEntity addProfileFunding(ProfileFundingEntity newProfileFundingEntity);
        
    /**
     * Add a new funding subtype to the list of pending for indexing subtypes
     * */
    void addFundingSubType(String subtype, String orcid);        
    
    /**
     * A process that will process all funding subtypes, filter and index them. 
     * */
    void indexFundingSubTypes();
    
    /**
     * Looks for the org defined funding subtypes that matches a given pattern
     * @param subtype pattern to look for
     * @param limit the max number of results to look for
     * @return a list of all org defined funding subtypes that matches the given pattern
     * */
    List<String> getIndexedFundingSubTypes(String subtype, int limit);
    
    /**
     * Get the funding associated with the given profileFunding id
     * 
     * @param profileFundingId
     *            The id of the ProfileFundingEntity object
     * 
     * @return the ProfileFundingEntity object
     * */
    ProfileFundingEntity getProfileFundingEntity(Long profileFundingId);
    
    boolean updateToMaxDisplay(String orcid, Long fundingId);
    
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
     * Add a new funding to the given user
     * @param orcid
     *          The user to add the funding
     * @param funding
     *          The funding to add
     * @return the added funding                  
     * */
    Funding createFunding(String orcid, Funding funding, boolean isApiRequest);
    
    /**
     * Updates a funding that belongs to the given user
     * @param orcid
     *          The user
     * @param funding
     *          The funding to update
     * @return the updated funding                  
     * */
    Funding updateFunding(String orcid, Funding funding, boolean isApiRequest);
    
    /**
     * Deletes a given funding, if and only if, the client that requested the delete is the source of the funding
     * @param orcid
     *          the funding owner
     * @param fundingId
     *          The funding id                 
     * @return true if the funding was deleted, false otherwise
     * */
    boolean checkSourceAndDelete(String orcid, Long fundingId);   
    
    /**
     * Get the list of fundings summaries that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of fundings that belongs to this user
     * */
    List<FundingSummary> getFundingSummaryList(String userOrcid, long lastModified);
    
    /**
     * Get the list of fundings that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of fundings that belongs to this user
     * */
    List<Funding> getFundingList(String userOrcid, long lastModified);
    
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
