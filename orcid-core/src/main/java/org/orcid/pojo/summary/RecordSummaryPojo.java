package org.orcid.pojo.summary;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
public class RecordSummaryPojo {
    @JsonInclude(Include.NON_NULL)
    private String name;
    private String orcid;
    private List<AffiliationSummary> employmentAffiliations;
    private int employmentAffiliationsCount;
    private String creation;
    private String lastModified;
    private int validatedWorks;
    private int selfAssertedWorks;
    private int selfAssertedPeerReviews;
    private int peerReviewsTotal;
    private int peerReviewPublicationGrants;
    private int validatedFunds;
    private int selfAssertedFunds;
    private List<AffiliationSummary> professionalActivities;
    private int professionalActivitiesCount;
    private List<ExternalIdentifiersSummary> externalIdentifiers;
    private String status;
    private List<AffiliationSummary> educationQualifications;
    private int educationQualificationsCount;
    private int validatedResearchResources;
    private int selfAssertedResearchResources;
    
    private List<EmailDomainSummary> emailDomains;
    private int emailDomainsCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public List<AffiliationSummary> getEmploymentAffiliations() {
        return employmentAffiliations;
    }

    public void setEmploymentAffiliations(List<AffiliationSummary> employmentAffiliations) {
        this.employmentAffiliations = employmentAffiliations;
    }

    public int getEmploymentAffiliationsCount() {
        return employmentAffiliationsCount;
    }

    public void setEmploymentAffiliationsCount(int employmentAffiliationsCount) {
        this.employmentAffiliationsCount = employmentAffiliationsCount;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public int getValidatedWorks() {
        return validatedWorks;
    }

    public void setValidatedWorks(int validatedWorks) {
        this.validatedWorks = validatedWorks;
    }

    public int getSelfAssertedWorks() {
        return selfAssertedWorks;
    }

    public void setSelfAssertedWorks(int selfAssertedWorks) {
        this.selfAssertedWorks = selfAssertedWorks;
    }

    public int getSelfAssertedPeerReviews() {
        return selfAssertedPeerReviews;
    }

    public void setSelfAssertedPeerReviews(int selfAssertedPeerReviews) {
        this.selfAssertedPeerReviews = selfAssertedPeerReviews;
    }

    public int getPeerReviewsTotal() {
        return peerReviewsTotal;
    }

    public void setPeerReviewsTotal(int peerReviewsTotal) {
        this.peerReviewsTotal = peerReviewsTotal;
    }

    public int getPeerReviewPublicationGrants() {
        return peerReviewPublicationGrants;
    }

    public void setPeerReviewPublicationGrants(int peerReviewPublicationGrants) {
        this.peerReviewPublicationGrants = peerReviewPublicationGrants;
    }

    public int getValidatedFunds() {
        return validatedFunds;
    }

    public void setValidatedFunds(int validatedFunds) {
        this.validatedFunds = validatedFunds;
    }

    public int getSelfAssertedFunds() {
        return selfAssertedFunds;
    }

    public void setSelfAssertedFunds(int selfAssertedFunds) {
        this.selfAssertedFunds = selfAssertedFunds;
    }

    public List<AffiliationSummary> getProfessionalActivities() {
        return professionalActivities;
    }

    public void setProfessionalActivities(List<AffiliationSummary> professionalActivities) {
        this.professionalActivities = professionalActivities;
    }

    public int getProfessionalActivitiesCount() {
        return professionalActivitiesCount;
    }

    public void setProfessionalActivitiesCount(int professionalActivitiesCount) {
        this.professionalActivitiesCount = professionalActivitiesCount;
    }

    public List<ExternalIdentifiersSummary> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ExternalIdentifiersSummary> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<AffiliationSummary> getEducationQualifications() {
        return educationQualifications;
    }

    public void setEducationQualifications(List<AffiliationSummary> educationQualifications) {
        this.educationQualifications = educationQualifications;
    }

    public int getEducationQualificationsCount() {
        return educationQualificationsCount;
    }

    public void setEducationQualificationsCount(int educationQualificationsCount) {
        this.educationQualificationsCount = educationQualificationsCount;
    }

    public int getValidatedResearchResources() {
        return validatedResearchResources;
    }

    public void setValidatedResearchResources(int validatedResearchResources) {
        this.validatedResearchResources = validatedResearchResources;
    }

    public int getSelfAssertedResearchResources() {
        return selfAssertedResearchResources;
    }

    public void setSelfAssertedResearchResources(int selfAssertedResearchResources) {
        this.selfAssertedResearchResources = selfAssertedResearchResources;
    }

    public List<EmailDomainSummary> getEmailDomains() {
        return emailDomains;
    }

    public void setEmailDomains(List<EmailDomainSummary> emailDomains) {
        this.emailDomains = emailDomains;
    }

    public int getEmailDomainsCount() {
        return emailDomainsCount;
    }

    public void setEmailDomainsCount(int emailDomainsCount) {
        this.emailDomainsCount = emailDomainsCount;
    }
    
}
