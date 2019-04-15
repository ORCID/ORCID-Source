package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbDistinctionAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Distinction distinction);

    Distinction toDistinction(OrgAffiliationRelationEntity entity);
    
    DistinctionSummary toDistinctionSummary(OrgAffiliationRelationEntity entity);

    List<Distinction> toDistinction(Collection<OrgAffiliationRelationEntity> entities);
    
    List<DistinctionSummary> toDistinctionSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Distinction distinction, OrgAffiliationRelationEntity existing);

}
