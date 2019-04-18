package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbQualificationAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Qualification qualification);

    Qualification toQualification(OrgAffiliationRelationEntity entity);
    
    QualificationSummary toQualificationSummary(OrgAffiliationRelationEntity entity);

    List<Qualification> toQualification(Collection<OrgAffiliationRelationEntity> entities);
    
    List<QualificationSummary> toQualificationSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Qualification qualification, OrgAffiliationRelationEntity existing);

}
