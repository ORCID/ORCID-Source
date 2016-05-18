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

import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.manager.cache.EntityCacheManager;

public interface ProfileEntityCacheManager extends EntityCacheManager {

    public ProfileEntity retrieve(String orcid) throws IllegalArgumentException;
    
    public void put(ProfileEntity profileEntity);
    
    public void removeAll();
    
    public void remove(String orcid);
    
}
