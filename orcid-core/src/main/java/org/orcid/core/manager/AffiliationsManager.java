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

import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
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
     * Get an education based on the orcid and education id
     * @param orcid
     *          The education owner
     * @param affiliationId
     *          The affiliation id
     * @return the education
     * */
    Education getEducationAffiliation(String userOrcid, String affiliationId);
    
    /**
     * Get a summary of an education affiliation based on the orcid and education id
     * @param orcid
     *          The education owner
     * @param affiliationId
     *          The affiliation id
     * @return the education summary
     * */
    EducationSummary getEducationSummary(String userOrcid, String affiliationId);
    
    /**
     * Add a new education to the given user
     * @param orcid
     *          The user to add the education
     * @param education
     *          The education to add
     * @return the added education
     * */
    Education createEducationAffiliation(String orcid, Education education);
    
    /**
     * Updates a education that belongs to the given user
     * @param orcid
     *          The user
     * @param education
     *          The education to update
     * @return the updated education
     * */
    Education updateEducationAffiliation(String orcid, Education education);
    
    /**
     * Get an employment based on the orcid and education id
     * @param orcid
     *          The employment owner
     * @param employmentId
     *          The employment id
     * @return the employment
     * */
    Employment getEmploymentAffiliation(String userOrcid, String employmentId);
    
    /**
     * Get a summary of an employment affiliation based on the orcid and education id
     * @param orcid
     *          The employment owner
     * @param employmentId
     *          The employment id
     * @return the employment summary
     * */
    EmploymentSummary getEmploymentSummary(String userOrcid, String employmentId);
    
    /**
     * Add a new employment to the given user
     * @param orcid
     *          The user to add the employment
     * @param employment
     *          The employment to add
     * @return the added employment
     * */
    Employment createEmploymentAffiliation(String orcid, Employment employment);
    
    /**
     * Updates a employment that belongs to the given user
     * @param orcid
     *          The user
     * @param employment
     *          The employment to update
     * @return the updated employment
     * */
    Employment updateEmploymentAffiliation(String orcid, Employment employment);
    
    /**
     * Deletes a given affiliation, if and only if, the client that requested the delete is the source of the affiliation
     * @param orcid
     *          the affiliation owner
     * @param affiliationId
     *          The affiliation id                 
     * @return true if the affiliation was deleted, false otherwise
     * */
    boolean checkSourceAndDelete(String orcid, String affiliationId);
}
