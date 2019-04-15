package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
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
