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
package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface WorkDao extends GenericDao<WorkEntity, Long> {

    /**
     * Add a new work to the work table
     * 
     * @param work
     *            The work that will be persisted
     * @return the work already persisted on database
     * */
    WorkEntity addWork(WorkEntity work);

    /**
     * Edits an existing work
     * 
     * @param work
     *            The work to be edited
     * @return the updated work
     * */
    WorkEntity editWork(WorkEntity work);

    /**
     * Find works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of minimized works associated to the specific user
     * */
    List<MinimizedWorkEntity> findWorks(String orcid, long lastModified);

    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     *            the Id of the user
     * @return the list of works associated to the specific user
     * */
    List<MinimizedWorkEntity> findPublicWorks(String orcid, long lastModified);
    
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
    boolean updateVisibilities(String orcid, List<Long> workIds, Visibility visibility);
    
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
    boolean removeWorks(String clientOrcid, List<Long> workIds);        
    
    /**
     * Remove a single work
     * 
     * @param workId
     *          The id of the work that should be deleted
     * @return true if the work was correctly deleted         
     * */
    boolean removeWork(String orcid, Long workId);
    
    /**
     * Removes all works for an record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all works will be
     *            removed.
     */
    void removeWorks(String orcid);
    
    /**
     * Sets the display index of the new work
     * @param workId
     *          The work id
     * @param orcid
     *          The work owner                         
     * @return true if the work index was correctly set                  
     * */
    boolean updateToMaxDisplay(String orcid, Long workId);
    
    /**
     * Returns a list of work ids of works that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @param workId
     *          The id of the latest work processed         
     * @return a list of work ids with old ext ids          
     * */
    List<BigInteger> getWorksWithOldExtIds(long workId, long limit);
    
    /**
     * Returns a list of work ids where the ext id relationship is null         
     * @return a list of work ids    
     * */
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
    List<BigInteger> getWorksByWorkTypeAndExtIdType(String workType, String extIdType);
    
    /**
     * Retrieve a work from database
     * @param orcid
     * @param id
     * @return the WorkEntity associated with the parameter id
     * */
    WorkEntity getWork(String orcid, Long id);

    List<WorkLastModifiedEntity> getWorkLastModifiedList(String orcid);

    List<WorkLastModifiedEntity> getPublicWorkLastModifiedList(String orcid);

    void detach(WorkBaseEntity workBaseEntity);
    
    boolean increaseDisplayIndexOnAllElements(String orcid);

    List<MinimizedWorkEntity> getMinimizedWorkEntities(List<Long> ids);
    
    List<WorkEntity> getWorkEntities(List<Long> ids);
}
