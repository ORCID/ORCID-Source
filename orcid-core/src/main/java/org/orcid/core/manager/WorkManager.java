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

import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record_rc2.Work;

public interface WorkManager {
    
    void setSourceManager(SourceManager sourceManager);        
    
    /**
     * Find the works for a specific user
     * 
     * @param orcid
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    List<Work> findWorks(String orcid, long lastModified); 
    
    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    List<Work> findPublicWorks(String orcid, long lastModified);
    
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
     * Sets the display index of the new work
     * @param orcid     
     *          The work owner
     * @param workId
     *          The work id
     * @return true if the work index was correctly set                  
     * */
    boolean updateToMaxDisplay(String orcid, Long workId);        

    /**
     * Get the given Work from the database
     * @param orcid
     *          The work owner
     * @param workId
     *          The work id             
     * */
    Work getWork(String orcid, Long workId, long lastModified);
    
    WorkSummary getWorkSummary(String orcid, Long workId, long lastModified);
    
    /**
     * Add a list of works to the give user
     * 
     * @param bulk
     *            a list of works to be added
     * @return a Bulk object containing the works created or the errors associated with a given work 
     * */
    Work createWork(String orcid, Work work, boolean applyValidations);
    
    /**
     * Edits an existing work
     * 
     * @param work
     *            The work to be edited
     * @param applyValidations
     *          Should the work be validated?    
     * @return The updated entity
     * */
    Work updateWork(String orcid, Work work, boolean applyValidations); 
    
    boolean checkSourceAndRemoveWork(String orcid, Long workId);
    
    /**
     * Get the list of works that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of works that belongs to this user
     * */
    List<WorkSummary> getWorksSummaryList(String orcid, long lastModified);
}
