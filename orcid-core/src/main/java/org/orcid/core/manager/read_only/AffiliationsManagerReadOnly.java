package org.orcid.core.manager.read_only;

import java.util.List;

import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;

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
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of employments that belongs to this user
     * */
    List<EmploymentSummary> getEmploymentSummaryList(String userOrcid);
    
    /**
     * Get the list of educations that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
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
