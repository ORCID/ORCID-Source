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

public interface ProfileWorkManager {
    
    /**
     * Removes the relationship that exists between a work and a profile.
     * 
     * @param workId
     *          The id of the work that will be removed from the client profile
     * @param clientOrcid
     *          The client orcid 
     * @return true if the relationship was deleted
     * */
    boolean removeWork(String workId, String clientOrcid);
}
