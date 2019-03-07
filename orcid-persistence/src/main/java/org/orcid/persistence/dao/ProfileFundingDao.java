package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public interface ProfileFundingDao extends GenericDao<ProfileFundingEntity, Long> {

    /**
     * Find and retrieve a profile funding that have the given id and belongs to the given user
     * 
     * @param userOrcid
     *            The owner of the funding
     * @param profileFundingId
     *            The id of the element
     * @return a profile funding entity that have the give id and belongs to the given user 
     * */
    public ProfileFundingEntity getProfileFunding(String userOrcid, Long profileFundingId);
    
    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param userOrcid
     *            The user orcid
     * @return true if the relationship was deleted
     * */
    boolean removeProfileFunding(String userOrcid, Long profileFundingId);

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
    boolean updateProfileFundingVisibility(String clientOrcid, Long profileFundingId, String visibility);
    
    /**
     * Updates the visibility of multiple existing profile funding relationships
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingIds
     *            The ids of the profile fundings that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationships were updated
     * */
    boolean updateProfileFundingVisibilities(String clientOrcid, ArrayList<Long> profileFundingIds, String visibility);

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
     * Get the funding associated with the client orcid and the organization id
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgId
     *            The id of the organization
     * 
     * @return the ProfileFundingEntity object
     * */
    ProfileFundingEntity getProfileFundingEntity(String orgId, String clientOrcid);

    /**
     * Get the funding associated with the given profileFunding id
     * 
     * @param profileFundingId
     *            The id of the ProfileFundingEntity object
     * 
     * @return the ProfileFundingEntity object
     * */
    ProfileFundingEntity getProfileFundingEntity(Long profileFundingId);
    
    /**
     * Get all the profile fundings where the amount is not null
     * @return a list of all profile fundings where the amount is not null 
     * */
    List<ProfileFundingEntity> getProfileFundingWithAmount();
    
    /**
     * Edits a profileFunding
     * 
     * @param profileFunding
     *            The profileFunding to be edited
     * @return the updated profileFunding
     * */
    ProfileFundingEntity updateProfileFunding(ProfileFundingEntity profileFunding);

    boolean updateToMaxDisplay(String orcid, Long id);
    
    List<BigInteger> findFundingNeedingExternalIdentifiersMigration(int chunkSize);
    
    void setFundingExternalIdentifiersInJson(BigInteger id, String extIdsJson);    
    
    void removeFundingByClientSourceId(String clientSourceId);
    
    List<ProfileFundingEntity> getByUser(String userOrcid, long lastModified);
    
    /**
     * Returns a list of external ids of fundings that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @return a list of funding ids with old ext ids          
     * */
    List<BigInteger> getFundingWithOldExtIds(long limit);
    
    boolean increaseDisplayIndexOnAllElements(String orcid);
    
    /**
     * Removes all funding that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all funding will be
     *            removed.
     */
    void removeAllFunding(String orcid);

    /**
     * Checks if there is any public funding for a specific user
     * 
     * @param orcid
     *          the Id of the user
     * @return true if there is at least one public funding for a specific user
     * */
    Boolean hasPublicFunding(String orcid);

    public List<BigInteger> getIdsForClientSourceCorrection(int limit);

    public void correctClientSource(List<BigInteger> ids);
}
