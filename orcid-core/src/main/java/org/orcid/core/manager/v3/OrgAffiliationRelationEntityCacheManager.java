package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

public interface OrgAffiliationRelationEntityCacheManager {
    
    List<OrgAffiliationRelationEntity> getAffiliationEntities(String orcid);

}
