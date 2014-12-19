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

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;

public interface ProfileWorkManager {

    /**
     * Removes the relationship that exists between a work and a profile.
     *      
     * @param clientOrcid
     *          The client orcid
     *          
     * @param workId
     *          The id of the work that will be removed from the client profile
     *                     
     * @return true if the relationship was deleted
     * */
    boolean removeWork(String clientOrcid, String workId);

    /**
     * Removes the relationship that exists between a work and a profile.
     *      
     * @param clientOrcid
     *          The client orcid
     *          
     * @param workId
     *          The id of the work that will be removed from the client profile
     *                     
     * @return true if the relationship was deleted
     * */
    boolean removeWorks(String clientOrcid, ArrayList<Long> workIds);

    
    /**
     * Updates the visibility of an existing profile work relationship
     * @param user orcid
     * @param workId
     *          The id of the work that will be updated
     * @param visibility
     *          The new visibility value for the profile work relationship         
     * @param clientOrcid
     *          The client orcid
     * 
     * @return true if the relationship was updated
     * */
    boolean updateVisibility(String orcid, String workId, Visibility visibility);

    /**
     * Updates the visibility of an existing profile work relationship
     * @param user orcid
     * @param workId
     *          The id of the work that will be updated
     * @param visibility
     *          The new visibility value for the profile work relationship         
     * @param clientOrcid
     *          The client orcid
     * 
     * @return true if the relationship was updated
     * */
    boolean updateVisibilities(String orcid, ArrayList<Long> workIds, Visibility visibility);
 
    
    /**
     * Get the profile work associated with the client orcid and the workId 
     * 
     * @param clientOrcid
     *          The client orcid
     *          
     * @param workId
     *          The id of the work that will be updated
     *          
     * @return the profileWork object
     * */
    ProfileWorkEntity getProfileWork(String clientOrcid, String workId);
    
    /**
     * Creates a new profile entity relationship between the provided work and
     * the given profile.
     * 
     * @param orcid
     *            The profile id
     * 
     * @param workId
     *            The work id
     * 
     * @param visibility
     *            The work visibility
     * 
     * @return true if the profile work relationship was created
     * */
    boolean addProfileWork(String orcid, long workId, Visibility visibility, String sourceOrcid);
    
    /**
     * Make the given work have the maxDisplay value, higher the value
     * equals how likely this work should be displayed before another 
     * work (or work version)
     * 
     * @param orcid
     * @param workId
     * @return
     */
    public boolean updateToMaxDisplay(String orcid, String workId);

}
