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
package org.orcid.core.mock;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.manager.cache.EntityCacheManager;
import org.orcid.persistence.manager.cache.SourceEntityCacheManager;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.context.annotation.Profile;

/**
 * @author Angel Montenegro
 * */
@Profile("test")
public class SourceEntityCacheManagerTest implements SourceEntityCacheManager {

    @Override
    public SourceEntity retrieve(String id) throws IllegalArgumentException {
        SourceEntity source = new SourceEntity();
        if(OrcidStringUtils.isValidOrcid(id)){
            source.setSourceProfile(new ProfileEntity(id));
        } else {
            source.setSourceClient(new ClientDetailsEntity(id));
        }
        return source;
    }

    @Override
    public void put(SourceEntity sourceEntity) {
        return;
    }

    @Override
    public void removeAll() {
        return;
    }

    @Override
    public void remove(String id) {
        return;
    }

    @Override
    public void setProfileEntityCacheManager(EntityCacheManager profileEntityCacheManager) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClientDetailsEntityCacheManager(EntityCacheManager clientDetailsEntityCacheManager) {
        // TODO Auto-generated method stub
        
    }
    
}
