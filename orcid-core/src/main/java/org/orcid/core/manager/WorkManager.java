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

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;

public interface WorkManager {
    /**
     * Add a new work to the work table
     * 
     * @param work
     *            The work that will be persited
     * @return the work already persisted on database
     * */
    WorkEntity addWork(WorkEntity work);
    
    /**
     * Edits an existing work
     * 
     * @param work
     *            The work to be edited
     * @return The updated entity
     * */
    WorkEntity editWork(WorkEntity work);
    
    /**
     * Find the works for a specific user
     * 
     * @param orcid
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    List<MinimizedWorkEntity> findWorks(String orcid, long lastModified); 
    
    /**
     * Find the public works for a specific user
     * 
     * @param orcid
     * 		the Id of the user
     * @return the list of works associated to the specific user 
     * */
    List<MinimizedWorkEntity> findPublicWorks(String orcid);
    
    /**
     * Updates the visibility of an existing work
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    boolean updateVisibilities(String orcid, ArrayList<Long> workIds, Visibility visibility);
 
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
    boolean removeWorks(String clientOrcid, ArrayList<Long> workIds);
    
    /**
     * Sets the display index of the new work
     * @param orcid     
     *          The work owner
     * @param workId
     *          The work id
     * @return true if the work index was correctly set                  
     * */
    boolean updateToMaxDisplay(String orcid, String workId);
}
