package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

public interface OrgAffiliationRelationDao extends GenericDao<OrgAffiliationRelationEntity, Long> {

    /**
     * Removes the relationship that exists between a affiliation and a profile.
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffilationRelation that will be removed from the client
     *            profile
     * @param userOrcid
     *            The user orcid
     * @return true if the relationship was deleted
     * */
    boolean removeOrgAffiliationRelation(String userOrcid, Long orgAffiliationRelationId);

    /**
     * Updates the visibility of an existing profile affiliation relationship
     * 
     * @param userOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffilationRelation that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile orgAffilationRelation relationship
     * 
     * @return true if the relationship was updated
     * */
    boolean updateVisibilityOnOrgAffiliationRelation(String userOrcid, Long orgAffiliationRelationIds, String visibility);

    /**
     * Updates the visibility of multiple existing profile affiliation relationships
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationIds
     *            List of ids of orgAffiliationRelations that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile affiliation relationships
     * 
     * @return true if each relationship was updated
     * */
    boolean updateVisibilitiesOnOrgAffiliationRelation(String userOrcid, ArrayList<Long> orgAffiliationRelationIds, String visibility);
    
    /**
     * Get the affiliation associated with the client orcid and the orgAffiliationRelationId
     * 
     * @param userOrcid
     *            The user orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffilationRelation that will be updated
     * 
     * @return the orgAffiliationRelation object
     * */
    OrgAffiliationRelationEntity getOrgAffiliationRelation(String userOrcid, Long orgAffiliationRelationId);

    /**
     * Creates a new profile entity relationship between the provided orgAffilationRelation and
     * the given profile.
     * 
     * @param orcid
     *            The profile id
     * 
     * @param orgAffiliationRelationId
     *            The orgAffilationRelation id
     * 
     * @param visibility
     *            The orgAffilationRelation visibility
     * 
     * @return true if the profile orgAffilationRelation relationship was created
     * */
    boolean addOrgAffiliationRelation(String clientOrcid, long orgAffiliationRelationId, String visibility);

    /**
     * Updates an existing OrgAffiliationRelationEntity
     * 
     * @param OrgAffiliationRelationEntity
     *          The entity to update
     * @return the updated OrgAffiliationRelationEntity
     * */
    OrgAffiliationRelationEntity updateOrgAffiliationRelationEntity(OrgAffiliationRelationEntity orgAffiliationRelationEntity);
    
    void removeOrgAffiliationByClientSourceId(String clientSourceId);
    
    /**
     * Get all affiliations that belongs to a user and matches given type
     * @param userOrcid
     *          The owner of the affiliation
     * @param type
     *          The affiliation type
     * @return a list of all affiliations that belongs to the given user and matches the given type                 
     * */
    List<OrgAffiliationRelationEntity> getByUserAndType(String userOrcid, String type);        
    
    /**
     * Get all affiliations that belongs to the given user
     * @param orcid: the user id
     * @return the list of affiliations that belongs to the user
     * */
    List<OrgAffiliationRelationEntity> getByUser(String orcid);
    
    /**
     * Removes all affiliations that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all funding will be
     *            removed.
     */
    void removeAllAffiliations(String orcid);

    List<OrgAffiliationRelationEntity> getDistinctionSummaries(String orcid, long lastModified);
    
    List<OrgAffiliationRelationEntity> getEducationSummaries(String orcid, long lastModified);

    List<OrgAffiliationRelationEntity> getEmploymentSummaries(String orcid, long lastModified);

    List<OrgAffiliationRelationEntity> getInvitedPositionSummaries(String orcid, long lastModified);
    
    List<OrgAffiliationRelationEntity> getMembershipSummaries(String orcid, long lastModified);
    
    List<OrgAffiliationRelationEntity> getQualificationSummaries(String orcid, long lastModified);
    
    List<OrgAffiliationRelationEntity> getServiceSummaries(String orcid, long lastModified);
    
    /**
     * Updates the display index of a given affiliation
     * 
     * @param orcid
     *            The affiliation owner
     * @param putCode
     *            The affiliation id
     * @return true if it was able to update the display index
     * */
    Boolean updateToMaxDisplay(String orcid, Long putCode);
    
    /**
     * Checks if there is any public affiliation for a specific user
     * 
     * @param orcid
     *          the Id of the user
     * @return true if there is at least one public affiliation for a specific user
     * */
    Boolean hasPublicAffiliations(String orcid);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);
}
