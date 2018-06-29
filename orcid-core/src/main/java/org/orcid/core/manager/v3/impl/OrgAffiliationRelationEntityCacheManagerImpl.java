package org.orcid.core.manager.v3.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.manager.v3.OrgAffiliationRelationEntityCacheManager;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

public class OrgAffiliationRelationEntityCacheManagerImpl implements OrgAffiliationRelationEntityCacheManager {

    @Resource(name = "affiliationEntitiesGenericCacheManager")
    private GenericCacheManager<OrcidString, List<OrgAffiliationRelationEntity>> affiliationEntitiesGenericCacheManager;
    
    @Override
    public List<OrgAffiliationRelationEntity> getAffiliationEntities(String orcid) {
        return affiliationEntitiesGenericCacheManager.retrieve(new OrcidString(orcid));
    }

}
