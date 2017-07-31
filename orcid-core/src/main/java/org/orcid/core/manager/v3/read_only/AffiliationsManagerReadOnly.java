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
package org.orcid.core.manager.v3.read_only;

import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.dev1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.dev1.record.Affiliation;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;

public interface AffiliationsManagerReadOnly {

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
     * @return the list of employments that belongs to this user
     * */
    List<EmploymentSummary> getEmploymentSummaryList(String userOrcid);
    
    /**
     * Get the list of educations that belongs to a user
     * 
     * @param userOrcid
     * @return the list of educations that belongs to this user
     * */
    List<EducationSummary> getEducationSummaryList(String userOrcid);  
    
    /**
     * Get the list of all affiliations that belongs to a user
     * @param userOrcid
     * 
     * @return the list of all affiliations that belongs to this user
     * */
    List<Affiliation> getAffiliations(String orcid);
}
