/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
     * Updates the visibility of an existing profile work relationship
     * 
     * @param clientOrcid
     *          The client orcid
     *          
     * @param workId
     *          The id of the work that will be updated
     *          
     * @param visibility
     *          The new visibility value for the profile work relationship         
     *                     
     * @return true if the relationship was updated
     * */
    boolean updateWork(String clientOrcid, String workId, Visibility visibility);
    
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
}
