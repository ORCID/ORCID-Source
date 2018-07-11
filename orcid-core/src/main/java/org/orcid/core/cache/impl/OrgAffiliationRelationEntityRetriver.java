package org.orcid.core.cache.impl;

import java.util.List;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

public class OrgAffiliationRelationEntityRetriver implements Retriever<OrcidString, List<OrgAffiliationRelationEntity>> {

    protected OrgAffiliationRelationDao orgAffiliationRelationDao; 
    
    public void setOrgAffiliationRelationDao(OrgAffiliationRelationDao orgAffiliationRelationDao) {
        this.orgAffiliationRelationDao = orgAffiliationRelationDao;
    }
    
    @Override
    public List<OrgAffiliationRelationEntity> retrieve(OrcidString key) {
        return orgAffiliationRelationDao.getByUser(key.getOrcid());
    }
}
