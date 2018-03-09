package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbEmploymentAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment);

    Employment toEmployment(OrgAffiliationRelationEntity entity);
    
    EmploymentSummary toEmploymentSummary(OrgAffiliationRelationEntity entity);

    List<Employment> toEmployment(Collection<OrgAffiliationRelationEntity> entities);
    
    List<EmploymentSummary> toEmploymentSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment, OrgAffiliationRelationEntity existing);

}
