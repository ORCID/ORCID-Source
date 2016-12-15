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
package org.orcid.core.manager.read_only;

import java.util.List;

import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;

public interface AffiliationsManagerReadOnly {

    /**
     * 
     * */
    OrgAffiliationRelationEntity findAffiliationByUserAndId(String userOrcid, Long affiliationId);
        
    /**
     * 
     * */
    List<OrgAffiliationRelationEntity> findAffiliationsByUserAndType(String userOrcid, AffiliationType type);
    
    /**
     * Get an education based on the orcid and education id
     * @param orcid
     *          The education owner
     * @param affiliationId
     *          The affiliation id
     * @return the education
     * */
    Education getEducationAffiliation(String userOrcid, Long affiliationId);
    
    /**
     * Get a summary of an education affiliation based on the orcid and education id
     * @param orcid
     *          The education owner
     * @param affiliationId
     *          The affiliation id
     * @return the education summary
     * */
    EducationSummary getEducationSummary(String userOrcid, Long affiliationId);
    
    /**
     * Get an employment based on the orcid and education id
     * @param orcid
     *          The employment owner
     * @param employmentId
     *          The employment id
     * @return the employment
     * */
    Employment getEmploymentAffiliation(String userOrcid, Long employmentId);
    
    /**
     * Get a summary of an employment affiliation based on the orcid and education id
     * @param orcid
     *          The employment owner
     * @param employmentId
     *          The employment id
     * @return the employment summary
     * */
    EmploymentSummary getEmploymentSummary(String userOrcid, Long employmentId);
    
    /**
     * Get the list of employments that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of employments that belongs to this user
     * */
    List<EmploymentSummary> getEmploymentSummaryList(String userOrcid, long lastModified);
    
    /**
     * Get the list of educations that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of educations that belongs to this user
     * */
    List<EducationSummary> getEducationSummaryList(String userOrcid, long lastModified);  
    
    @Deprecated
    List<AffiliationForm> getAffiliations(String orcid);
}
