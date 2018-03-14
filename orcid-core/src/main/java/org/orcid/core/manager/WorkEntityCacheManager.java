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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkBaseEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.WorkLastModifiedEntity;

import net.sf.ehcache.Cache;

/**
 * 
 * @author Will Simpson
 *
 */
public interface WorkEntityCacheManager {

    List<WorkLastModifiedEntity> retrieveWorkLastModifiedList(String orcid, long profileLastModified);

    List<WorkLastModifiedEntity> retrievePublicWorkLastModifiedList(String orcid, long profileLastModified);

    MinimizedWorkEntity retrieveMinimizedWork(long workId, long workLastModified);

    <T extends WorkBaseEntity> List<T> retrieveWorkList(String orcid, Map<Long, Date> workIdsWithLastModified, Cache workCache,
            Function<List<Long>, List<T>> workRetriever);

    List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, long profileLastModified);

    List<MinimizedWorkEntity> retrievePublicMinimizedWorks(String orcid, long profileLastModified);
    
    List<MinimizedWorkEntity> retrieveMinimizedWorks(String orcid, List<Long> ids, long profileLastModified);

    WorkEntity retrieveFullWork(String orcid, long workId, long workLastModified);
    
    @Deprecated
    List<WorkEntity> retrieveFullWorks(String orcid, long profileLastModified);
    
    void evictExpiredElements();

}
