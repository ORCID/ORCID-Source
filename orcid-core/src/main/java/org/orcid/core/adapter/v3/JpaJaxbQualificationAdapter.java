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
package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.dev1.record.Qualification;
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
