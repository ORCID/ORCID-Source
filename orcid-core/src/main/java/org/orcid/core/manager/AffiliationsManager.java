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
package org.orcid.core.manager;

import java.util.List;

import org.orcid.jaxb.model.record.AffiliationType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

public interface AffiliationsManager {

    /**
     * 
     * */
    OrgAffiliationRelationEntity findAffiliationByUserAndId(String userOrcid, String affiliationId);
    
    /**
     * 
     * */
    List<OrgAffiliationRelationEntity> findAffiliationsByType(AffiliationType type);
    
    /**
     * 
     * */
    List<OrgAffiliationRelationEntity> findAffiliationsByUserAndType(String userOrcid, AffiliationType type);
    
    /**
     * 
     * */
    Education getEducationAffiliation(String userOrcid, String affiliationId);
}
