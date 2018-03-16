package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbEducationAdapter {

    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education);

    Education toEducation(OrgAffiliationRelationEntity entity);
    
    EducationSummary toEducationSummary(OrgAffiliationRelationEntity entity);

    List<Education> toEducation(Collection<OrgAffiliationRelationEntity> entities);
    
    List<EducationSummary> toEducationSummary(Collection<OrgAffiliationRelationEntity> entities);
    
    OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education, OrgAffiliationRelationEntity existing);

}
