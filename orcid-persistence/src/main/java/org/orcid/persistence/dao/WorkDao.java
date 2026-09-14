package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.MinimizedExtendedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface WorkDao extends GenericDao<WorkEntity, Long> {    
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    MinimizedWorkEntity getMinimizedWorkEntity(Long id);

    /**
     * Updates the visibility of an existing work
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibilities(String orcid, List<Long> workIds, String visibility);
    
    /**
     * Removes a work.
     * 
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the work was deleted
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeWorks(String clientOrcid, List<Long> workIds);        
    
    /**
     * Remove a single work
     * 
     * @param workId
     *          The id of the work that should be deleted
     * @return true if the work was correctly deleted         
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeWork(String orcid, Long workId);
    
    /**
     * Removes all works for a record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all works will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeWorks(String orcid);
    
    /**
     * Sets the display index of the new work
     * @param workId
     *          The work id
     * @param orcid
     *          The work owner                         
     * @return true if the work index was correctly set                  
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateToMaxDisplay(String orcid, Long workId);
    
    /**
     * Returns a list of work ids where the ext id relationship is null         
     * @return a list of work ids    
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getWorksWithNullRelationship();
    
    /**
     * Returns a list of work ids where the work matches the work type and ext ids type
     * @param workType
     *          The work type
     * @param extIdType
     *          The ext id type
     *         
     * @return a list of work ids    
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getWorksByWorkTypeAndExtIdType(String workType, String extIdType);
    
    /**
     * Retrieve a work from database
     * @param orcid
     * @param id
     * 
     * @return the WorkEntity associated with the parameter id
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    WorkEntity getWork(String orcid, Long id);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<WorkLastModifiedEntity> getWorkLastModifiedList(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<WorkLastModifiedEntity> getPublicWorkLastModifiedList(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<WorkLastModifiedEntity> getWorkLastModifiedList(String orcid, List<Long> ids);

    void detach(WorkBaseEntity workBaseEntity);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean increaseDisplayIndexOnAllElements(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<MinimizedWorkEntity> getMinimizedWorkEntities(List<Long> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<MinimizedExtendedWorkEntity> getMinimizedExtendedWorkEntities(List<Long> ids);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<WorkEntity> getWorkEntities(String orcid, List<Long> ids);        

    @Deprecated
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<WorkEntity> getWorksByOrcidId(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean hasPublicWorks(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean isPublic(String orcid, List<Long> workIds);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClientIds);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctClientSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClientIds);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctUserSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateUserOBODetails(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void revertUserOBODetails(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBORecords(int max);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsOfWorksReferencingClientProfiles(int i, List<String> clientProfileOrcidIds);

    /**
     * Retrieve a list of works by orcid from the database
     * @param orcid
     * @param featuredOnly
     *
     * @return a list of works associated with the provided orcid
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Object[]> getWorksByOrcid(String orcid, boolean featuredOnly);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Object[]> getWorksStartingFromWorkId(Long WorkId, int numberOfWorks);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateFeaturedDisplayIndex(String orcid, Long id, Integer featuredDisplayIndex);
}
