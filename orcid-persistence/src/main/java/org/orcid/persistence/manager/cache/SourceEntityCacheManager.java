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
package org.orcid.persistence.manager.cache;

import org.orcid.persistence.jpa.entities.SourceEntity;

/**
 * @author Angel Montenegro
 * */
public interface SourceEntityCacheManager {

    void setProfileEntityCacheManager(EntityCacheManager profileEntityCacheManager);
    
    void setClientDetailsEntityCacheManager(EntityCacheManager clientDetailsEntityCacheManager);
    
    public SourceEntity retrieve(String id) throws IllegalArgumentException;
    
    public void put(SourceEntity sourceEntity);
    
    public void removeAll();
    
    public void remove(String id);
    
}
