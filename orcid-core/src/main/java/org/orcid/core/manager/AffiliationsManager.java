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
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;

public interface AffiliationsManager {

    void setSourceManager(SourceManager sourceManager);
    
    /**
     * 
     * */
    OrgAffiliationRelationEntity findAffiliationByUserAndId(String userOrcid, Long affiliationId);
    
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
     * Add a new education to the given user
     * @param orcid
     *          The user to add the education
     * @param education
     *          The education to add
     * @return the added education
     * */
    Education createEducationAffiliation(String orcid, Education education, boolean isApiRequest);
    
    /**
     * Updates a education that belongs to the given user
     * @param orcid
     *          The user
     * @param education
     *          The education to update
     * @return the updated education
     * */
    Education updateEducationAffiliation(String orcid, Education education, boolean isApiRequest);
    
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
     * Add a new employment to the given user
     * @param orcid
     *          The user to add the employment
     * @param employment
     *          The employment to add
     * @return the added employment
     * */
    Employment createEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest);
    
    /**
     * Updates a employment that belongs to the given user
     * @param orcid
     *          The user
     * @param employment
     *          The employment to update
     * @return the updated employment
     * */
    Employment updateEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest);
    
    /**
     * Deletes a given affiliation, if and only if, the client that requested the delete is the source of the affiliation
     * @param orcid
     *          the affiliation owner
     * @param affiliationId
     *          The affiliation id                 
     * @return true if the affiliation was deleted, false otherwise
     * */
    boolean checkSourceAndDelete(String orcid, Long affiliationId);
    
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
    
    List<AffiliationForm> getAffiliations(String orcid);
    
    boolean updateVisibility(String orcid, Long affiliationId, Visibility visibility);
}
