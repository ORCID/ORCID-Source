package org.orcid.core.manager.v3.read_only;

import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.rc2.record.Affiliation;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ServiceSummary;

public interface AffiliationsManagerReadOnly {

    /**
     * Get a distinction based on the orcid and distinction id
     * 
     * @param orcid
     *            The distinction owner
     * @param affiliationId
     *            The affiliation id
     * @return the distinction
     */
    Distinction getDistinctionAffiliation(String userOrcid, Long affiliationId);

    /**
     * Get a summary of a distinction affiliation based on the orcid and
     * distinction id
     * 
     * @param orcid
     *            The distinction owner
     * @param affiliationId
     *            The affiliation id
     * @return the distinction summary
     */
    DistinctionSummary getDistinctionSummary(String userOrcid, Long affiliationId);

    /**
     * Get the list of distinctions that belongs to a user
     * 
     * @param userOrcid
     * @return the list of distinctions that belongs to this user
     */
    List<DistinctionSummary> getDistinctionSummaryList(String userOrcid);

    /**
     * Get an education based on the orcid and education id
     * 
     * @param orcid
     *            The education owner
     * @param affiliationId
     *            The affiliation id
     * @return the education
     */
    Education getEducationAffiliation(String userOrcid, Long affiliationId);

    /**
     * Get a summary of an education affiliation based on the orcid and
     * education id
     * 
     * @param orcid
     *            The education owner
     * @param affiliationId
     *            The affiliation id
     * @return the education summary
     */
    EducationSummary getEducationSummary(String userOrcid, Long affiliationId);

    /**
     * Get the list of educations that belongs to a user
     * 
     * @param userOrcid
     * @return the list of educations that belongs to this user
     */
    List<EducationSummary> getEducationSummaryList(String userOrcid);

    /**
     * Get an employment based on the orcid and education id
     * 
     * @param orcid
     *            The employment owner
     * @param employmentId
     *            The employment id
     * @return the employment
     */
    Employment getEmploymentAffiliation(String userOrcid, Long employmentId);

    /**
     * Get a summary of an employment affiliation based on the orcid and
     * education id
     * 
     * @param orcid
     *            The employment owner
     * @param employmentId
     *            The employment id
     * @return the employment summary
     */
    EmploymentSummary getEmploymentSummary(String userOrcid, Long employmentId);

    /**
     * Get the list of employments that belongs to a user
     * 
     * @param userOrcid
     * @return the list of employments that belongs to this user
     */
    List<EmploymentSummary> getEmploymentSummaryList(String userOrcid);

    /**
     * Get an invitedPosition based on the orcid and invitedPosition id
     * 
     * @param orcid
     *            The invitedPosition owner
     * @param affiliationId
     *            The affiliation id
     * @return the invitedPosition
     */
    InvitedPosition getInvitedPositionAffiliation(String userOrcid, Long affiliationId);

    /**
     * Get a summary of an invitedPosition affiliation based on the orcid and
     * invitedPosition id
     * 
     * @param orcid
     *            The invitedPosition owner
     * @param affiliationId
     *            The affiliation id
     * @return the invitedPosition summary
     */
    InvitedPositionSummary getInvitedPositionSummary(String userOrcid, Long affiliationId);

    /**
     * Get the list of invitedPositions that belongs to a user
     * 
     * @param userOrcid
     * @return the list of invitedPositions that belongs to this user
     */
    List<InvitedPositionSummary> getInvitedPositionSummaryList(String userOrcid);

    /**
     * Get a membership based on the orcid and membership id
     * 
     * @param orcid
     *            The membership owner
     * @param affiliationId
     *            The affiliation id
     * @return the membership
     */
    Membership getMembershipAffiliation(String userOrcid, Long affiliationId);

    /**
     * Get a summary of a membership affiliation based on the orcid and
     * membership id
     * 
     * @param orcid
     *            The membership owner
     * @param affiliationId
     *            The affiliation id
     * @return the membership summary
     */
    MembershipSummary getMembershipSummary(String userOrcid, Long affiliationId);

    /**
     * Get the list of memberships that belongs to a user
     * 
     * @param userOrcid
     * @return the list of memberships that belongs to this user
     */
    List<MembershipSummary> getMembershipSummaryList(String userOrcid);

    /**
     * Get a qualification based on the orcid and qualification id
     * 
     * @param orcid
     *            The qualification owner
     * @param affiliationId
     *            The affiliation id
     * @return the qualification
     */
    Qualification getQualificationAffiliation(String userOrcid, Long affiliationId);

    /**
     * Get a summary of a qualification affiliation based on the orcid and
     * qualification id
     * 
     * @param orcid
     *            The qualification owner
     * @param affiliationId
     *            The affiliation id
     * @return the qualification summary
     */
    QualificationSummary getQualificationSummary(String userOrcid, Long affiliationId);

    /**
     * Get the list of qualifications that belongs to a user
     * 
     * @param userOrcid
     * @return the list of qualifications that belongs to this user
     */
    List<QualificationSummary> getQualificationSummaryList(String userOrcid);

    /**
     * Get a service based on the orcid and service id
     * 
     * @param orcid
     *            The service owner
     * @param affiliationId
     *            The affiliation id
     * @return the service
     */
    Service getServiceAffiliation(String userOrcid, Long affiliationId);

    /**
     * Get a summary of a service affiliation based on the orcid and service id
     * 
     * @param orcid
     *            The service owner
     * @param affiliationId
     *            The affiliation id
     * @return the service summary
     */
    ServiceSummary getServiceSummary(String userOrcid, Long affiliationId);

    /**
     * Get the list of services that belongs to a user
     * 
     * @param userOrcid
     * @return the list of services that belongs to this user
     */
    List<ServiceSummary> getServiceSummaryList(String userOrcid);

    /**
     * Get the list of all affiliations that belongs to a user
     * 
     * @param userOrcid
     * 
     * @return the list of all affiliations that belongs to this user
     */
    List<Affiliation> getAffiliations(String orcid);

    /**
     * Get all the affiliations that belongs to a user grouped by type and external ids
     * 
     * @param justPublic
     *          Specify if we want to include only the public elements
     * @param userOrcid
     * 
     * @return grouped list of affiliations that belongs to this user
     */
    <T extends AffiliationSummary> Map<AffiliationType, List<AffiliationGroup<T>>> getGroupedAffiliations(String orcid, boolean justPublic);
    
    /**
     * Generate a grouped list of affiliations with the given list of AffiliationSummary objects
     * 
     * @param affiliations
     *          The list of AffiliationSummary objects to group
     * @param justPublic
     *          Specify if we want to group only the public elements in the given list
     * @return Affiliations element with the AffiliationSummary elements grouped                  
     * */
    <T extends AffiliationSummary> List<AffiliationGroup<T>> groupAffiliations(List<T> affiliations, boolean justPublic);
    
    /**
     * Checks if there is any public affiliation for a specific user
     * 
     * @param orcid
     *          the Id of the user
     * @return true if there is at least one public affiliation for a specific user
     * */
    Boolean hasPublicAffiliations(String orcid);

}
