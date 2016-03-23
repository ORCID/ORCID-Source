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

import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface WorkCacheManager {

    List<WorkLastModifiedEntity> retrieveWorkLastModifiedList(String orcid, long profileLastModified);
    
    List<WorkLastModifiedEntity> retrievePublicWorkLastModifiedList(String orcid, long profileLastModified);

    MinimizedWorkEntity retrieveMinimizedWork(long workId, long workLastModified);

    List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified);

    List<MinimizedWorkEntity> retrievePublicMinimizedWorks(String orcid, long profileLastModified);

}
