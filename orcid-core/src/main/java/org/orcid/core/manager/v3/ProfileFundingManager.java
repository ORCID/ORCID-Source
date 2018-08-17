package org.orcid.core.manager.v3;

import java.util.ArrayList;

import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public interface ProfileFundingManager extends ProfileFundingManagerReadOnly {
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
     * Updates visibility of multiple existing profile funding relationships
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingIds
     *            The ids of the profile fundings that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding objects
     *           
     * @return true if the relationships were updated
     */
    boolean updateProfileFundingVisibilities(String orcid, ArrayList<Long> profileFundingIds, Visibility visibility);

        
    /**
     * Add a new funding subtype to the list of pending for indexing subtypes
     * */
    void addFundingSubType(String subtype, String orcid);        
    
    /**
     * A process that will process all funding subtypes, filter and index them. 
     * */
    void indexFundingSubTypes();        
    
    /**
     * Get the funding associated with the given profileFunding id
     * 
     * @param profileFundingId
     *            The id of the ProfileFundingEntity object
     * 
     * @return the ProfileFundingEntity object
     * */
    @Deprecated
    ProfileFundingEntity getProfileFundingEntity(Long profileFundingId);
    
    boolean updateToMaxDisplay(String orcid, Long fundingId);
        
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
     * Removes all funding that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all funding will be
     *            removed.
     */
    void removeAllFunding(String orcid);
}
